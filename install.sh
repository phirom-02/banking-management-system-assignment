#!/usr/bin/env bash
#
# install.sh — bootstrap script for the Banking Management System assignment.
#
# Starts the backend (Spring Boot + PostgreSQL) via Docker Compose, then
# builds the JavaFX desktop client so it's ready to run.
#
# Usage:
#   ./install.sh                 Start backend + build client (default)
#   ./install.sh --backend-only  Only start the backend (Docker)
#   ./install.sh --client-only   Only build the client (skips Docker entirely)
#   ./install.sh --appimage      Also build a native app-image for the client
#   ./install.sh --skip-checks   Skip the prerequisite tool checks
#   ./install.sh -h | --help     Show this help and exit
#
set -euo pipefail

# ---------------------------------------------------------------------------
# Config
# ---------------------------------------------------------------------------
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$ROOT_DIR/bms-backend"
CLIENT_DIR="$ROOT_DIR/bms-gui"
BACKEND_URL="http://localhost:8080"
HEALTH_ENDPOINT="$BACKEND_URL/actuator/health"
DEFAULT_ADMIN_USER="admin"
DEFAULT_ADMIN_PASS="Admin@12345"
HEALTH_CHECK_RETRIES=30
HEALTH_CHECK_INTERVAL=2

RUN_BACKEND=true
RUN_CLIENT=true
BUILD_APPIMAGE=false
SKIP_CHECKS=false

# ---------------------------------------------------------------------------
# Colors / logging helpers
# ---------------------------------------------------------------------------
if [[ -t 1 ]]; then
    C_RESET='\033[0m'; C_BOLD='\033[1m'; C_GREEN='\033[32m'; C_YELLOW='\033[33m'; C_RED='\033[31m'; C_BLUE='\033[34m'
else
    C_RESET=''; C_BOLD=''; C_GREEN=''; C_YELLOW=''; C_RED=''; C_BLUE=''
fi

log()     { echo -e "${C_BLUE}==>${C_RESET} $*"; }
success() { echo -e "${C_GREEN}✔${C_RESET} $*"; }
warn()    { echo -e "${C_YELLOW}⚠${C_RESET} $*"; }
error()   { echo -e "${C_RED}✘ $*${C_RESET}" >&2; }
die()     { error "$*"; exit 1; }

# ---------------------------------------------------------------------------
# Argument parsing
# ---------------------------------------------------------------------------
print_help() {
    sed -n '2,15p' "$0" | sed 's/^# \{0,1\}//'
}

for arg in "$@"; do
    case "$arg" in
        --backend-only) RUN_CLIENT=false ;;
        --client-only)  RUN_BACKEND=false ;;
        --appimage)     BUILD_APPIMAGE=true ;;
        --skip-checks)  SKIP_CHECKS=true ;;
        -h|--help)      print_help; exit 0 ;;
        *) die "Unknown option: $arg (use --help for usage)" ;;
    esac
done

# ---------------------------------------------------------------------------
# Prerequisite checks
# ---------------------------------------------------------------------------
check_command() {
    command -v "$1" >/dev/null 2>&1
}

check_prerequisites() {
    log "Checking prerequisites…"
    local missing=false

    if $RUN_BACKEND; then
        if ! check_command docker; then
            error "Docker is required to run the backend. Install it from https://docs.docker.com/get-docker/"
            missing=true
        fi
        if ! docker compose version >/dev/null 2>&1 && ! check_command docker-compose; then
            error "Docker Compose is required (either 'docker compose' or 'docker-compose'). It ships with Docker Desktop."
            missing=true
        fi
    fi

    if $RUN_CLIENT; then
        if ! check_command java; then
            error "Java 17+ is required to build the client. Install a JDK (e.g. https://adoptium.net)."
            missing=true
        else
            local major
            major=$(java -version 2>&1 | head -1 | grep -oE '"[0-9]+' | tr -d '"' || echo 0)
            if [[ "$major" -lt 17 ]]; then
                error "Java 17+ is required; found version $major. Please upgrade your JDK."
                missing=true
            fi
        fi

        if ! check_command mvn; then
            error "Maven is required to build the client. Install it from https://maven.apache.org/install.html"
            missing=true
        fi
    fi

    if $missing; then
        die "One or more prerequisites are missing. Install them and re-run this script."
    fi

    success "All prerequisites found."
}

# ---------------------------------------------------------------------------
# Backend (Docker Compose)
# ---------------------------------------------------------------------------
compose_cmd() {
    if docker compose version >/dev/null 2>&1; then
        echo "docker compose"
    else
        echo "docker-compose"
    fi
}

start_backend() {
    [[ -d "$BACKEND_DIR" ]] || die "Backend directory not found: $BACKEND_DIR"
    [[ -f "$ROOT_DIR/docker-compose.yml" ]] || die "docker-compose.yml not found in $ROOT_DIR"

    log "Starting backend + PostgreSQL via Docker Compose (this may take a while on first run)…"
    local dc
    dc=$(compose_cmd)
    (cd "$ROOT_DIR" && $dc up -d --build)

    log "Waiting for the backend to become healthy at $HEALTH_ENDPOINT …"
    local attempt=0
    until curl -sf "$HEALTH_ENDPOINT" >/dev/null 2>&1; do
        attempt=$((attempt + 1))
        if [[ $attempt -ge $HEALTH_CHECK_RETRIES ]]; then
            warn "Backend didn't respond healthy after $((HEALTH_CHECK_RETRIES * HEALTH_CHECK_INTERVAL))s."
            warn "Check logs with: $dc logs -f backend"
            return
        fi
        sleep "$HEALTH_CHECK_INTERVAL"
    done

    success "Backend is up at $BACKEND_URL (API base: $BACKEND_URL/api)"
    echo "   Default admin login: $DEFAULT_ADMIN_USER / $DEFAULT_ADMIN_PASS"
    echo "   (change these via SEED_ADMIN_USERNAME / SEED_ADMIN_PASSWORD before real use)"
}

# ---------------------------------------------------------------------------
# Client (Maven / JavaFX)
# ---------------------------------------------------------------------------
build_client() {
    [[ -d "$CLIENT_DIR" ]] || die "Client directory not found: $CLIENT_DIR"

    if $BUILD_APPIMAGE; then
        log "Building JavaFX client as a native app-image (mvn clean package -Pappimage)…"
        (cd "$CLIENT_DIR" && mvn -q clean package -Pappimage)
        success "App image built at: $CLIENT_DIR/target/dist/"
    else
        log "Building JavaFX client (mvn clean package)…"
        (cd "$CLIENT_DIR" && mvn -q clean package -DskipTests)
        success "Client built successfully."
        echo "   Run it with: cd bms-gui && mvn javafx:run"
    fi
}

# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------
main() {
    echo -e "${C_BOLD}Banking Management System — installer${C_RESET}"
    echo "Repository root: $ROOT_DIR"
    echo

    $SKIP_CHECKS || check_prerequisites
    echo

    if $RUN_BACKEND; then
        start_backend
        echo
    fi

    if $RUN_CLIENT; then
        build_client
        echo
    fi

    success "Done."
    if $RUN_BACKEND && $RUN_CLIENT && ! $BUILD_APPIMAGE; then
        echo
        echo "Next step:"
        echo "  cd bms-gui && mvn javafx:run"
    fi
}

main
