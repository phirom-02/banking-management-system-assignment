# Core Banking Management System

_(Repository: [`banking-management-system-assignment`](https://github.com/phirom-02/banking-management-system-assignment))_

A full-stack banking back-office simulator built as a university group project:
a **Spring Boot REST API** backend with a **JavaFX** desktop client, matching
the system described in the project proposal (`Project Title: Core Banking
Management System`, Group 3).

The system lets a bank employee (admin) log in and manage customers, open and
manage accounts, process deposits/withdrawals/transfers, and review a
double-entry general ledger — all from a native desktop app talking to a REST
API backed by PostgreSQL.

```
Java Desktop App (JavaFX)  --  REST API (HTTP/JSON)  -->  Spring Boot Server  -->  PostgreSQL
```

---

## Table of contents

- [Project background](#project-background)
- [Repository structure](#repository-structure)
- [Tech stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Quick start](#quick-start)
- [Manual installation](#manual-installation)
- [Default admin login](#default-admin-login)
- [Configuration](#configuration)
- [Features implemented](#features-implemented)
- [API overview](#api-overview)
- [Packaging the client](#packaging-the-client)
- [Team & roles](#team--roles)
- [Future enhancements](#future-enhancements)

---

## Project background

### Objectives

- Provide a user-friendly GUI for a bank employee to manage banking operations
- Enable secure admin authentication
- Provide full CRUD on customer data
- Track account balances and transactions with proper double-entry bookkeeping

### Scope

The system targets small-to-medium organizations and covers:

- Dashboard with an activity feed
- Customer management (CRUD)
- Account management (open, freeze, close)
- Transactions (deposit, withdrawal, transfer)
- General ledger with a double-entry view

### Functional requirements

- Admin authentication (login)
- Customer management (CRUD)
- Transaction management
- Ledger management
- Account management (CRUD)

### Non-functional requirements

- Responsive GUI
- Secure login mechanism (hashed passwords, JWT sessions)
- Error handling and input validation
- Modular code structure for scalability
- User-friendly interface

---

## Repository structure

```
banking-management-system-assignment/
├── bms-backend/       Spring Boot REST API (Java 21, PostgreSQL, JWT auth)
├── bms-gui/           JavaFX desktop client (talks to the API over HTTP)
├── docker-compose.yml Runs the backend + PostgreSQL together
├── install.sh         One-command setup for both backend and client
└── README.md          You are here
```

Each subproject also has its own `README.md` with implementation-level detail
(entity design, endpoint list, class structure) if you need to dig deeper than
what's covered here.

---

## Tech stack

| Layer         | Technology                                                                 |
| ------------- | -------------------------------------------------------------------------- |
| Backend       | Java 21, Spring Boot 3, Spring Web, Spring Data JPA, Spring Security + JWT |
| Database      | PostgreSQL 16                                                              |
| Frontend      | Java 21, JavaFX 21 (Controls + FXML)                                       |
| Frontend HTTP | `java.net.http.HttpClient` + Jackson (no extra HTTP library)               |
| Build tools   | Maven (both subprojects)                                                   |
| Packaging     | Docker / Docker Compose (backend), `jpackage` app-image (client)           |
| VCS           | Git                                                                        |

---

## Prerequisites

| Tool                            | Needed for                                                         | Notes                                                          |
| ------------------------------- | ------------------------------------------------------------------ | -------------------------------------------------------------- |
| **Docker** + **Docker Compose** | Backend + PostgreSQL                                               | Docker Desktop includes both                                   |
| **JDK 21+**                     | Building/running the client (and the backend, if not using Docker) | e.g. [Adoptium Temurin](https://adoptium.net)                  |
| **Maven 3.9+**                  | Building the client                                                | Backend can be built by Docker without Maven installed locally |
| **curl**                        | Used by `install.sh` to check backend health                       | Preinstalled on macOS/Linux; on Windows use WSL or Git Bash    |

You do **not** need PostgreSQL installed separately — Docker Compose runs it
for you with a persisted volume.

---

## Quick start

From the repository root:

```bash
./install.sh
```

This will:

1. Check that Docker, Java 21+, and Maven are installed
2. Build and start the backend + PostgreSQL via `docker compose up -d --build`
3. Wait for the backend's health check to pass
4. Build the JavaFX client with Maven

Then launch the client:

```bash
cd bms-gui
mvn javafx:run
```

**Useful flags:**

```bash
./install.sh --backend-only     # only start the backend, skip the client build
./install.sh --client-only      # only build the client, skip Docker entirely
./install.sh --appimage         # also package the client as a native app-image
./install.sh --skip-checks      # skip the prerequisite checks
./install.sh --help             # show usage
```

To stop the backend later:

```bash
docker compose down
```

---

## Manual installation

If you'd rather not use `install.sh`, or something in it doesn't fit your
setup:

### Backend

From the **repository root** (the compose file's build context expects this):

```bash
docker compose up -d --build
```

Or, without Docker (requires a local PostgreSQL instance):

```bash
cd bms-backend
createdb bms          # or via your Postgres client of choice
export DB_HOST=localhost DB_PORT=5432 DB_NAME=bms DB_USER=bms DB_PASSWORD=change_me!
mvn spring-boot:run
```

The API listens on `http://localhost:8080`, with all endpoints under `/api`.

### Client

```bash
cd bms-gui
mvn javafx:run
```

To point the client at a backend running somewhere other than
`localhost:8080`:

```bash
mvn javafx:run -Dcore.banking.api.url=http://your-host:8080/api
```

See [Packaging the client](#packaging-the-client) below for building a
distributable native app instead of running through Maven each time.

---

## Default admin login

On first boot, the backend seeds one admin account if none exists yet:

| Username | Password      |
| -------- | ------------- |
| `admin`  | `Admin@12345` |

Override the seeded credentials via environment variables before first boot:
`SEED_ADMIN_USERNAME`, `SEED_ADMIN_PASSWORD` (see `docker-compose.yml`).
Rotate this password before using the system with any real data.

---

## Configuration

Backend configuration is environment-variable driven (see
`bms-backend/src/main/resources/application.yml` for the full list):

| Variable                                          | App default (`application.yml`)                   | Docker Compose override               | Purpose                                                                      |
| ------------------------------------------------- | ------------------------------------------------- | ------------------------------------- | ---------------------------------------------------------------------------- |
| `DB_HOST`                                         | `localhost`                                       | `postgres` (the compose service name) | PostgreSQL host                                                              |
| `DB_PORT` / `DB_NAME` / `DB_USER` / `DB_PASSWORD` | `5432` / `core_banking` / `postgres` / `postgres` | `5432` / `bms` / `bms` / `change_me!` | PostgreSQL connection details — Compose's values win when running via Docker |
| `JWT_SECRET`                                      | dev default, **change in production**             | same                                  | Signing key for JWTs                                                         |
| `JWT_EXPIRATION_MS`                               | `86400000` (24h)                                  | same                                  | Token lifetime                                                               |
| `CORS_ALLOWED_ORIGINS`                            | `http://localhost:3000`                           | same                                  | Adjust if a browser-based client is added later                              |
| `SEED_ADMIN_USERNAME` / `SEED_ADMIN_PASSWORD`     | `admin` / `Admin@12345`                           | same                                  | Initial admin account                                                        |

Client configuration is a single override:

| Setting      | How to set it                                                            | Default                     |
| ------------ | ------------------------------------------------------------------------ | --------------------------- |
| API base URL | `-Dcore.banking.api.url=...` JVM flag, or `CORE_BANKING_API_URL` env var | `http://localhost:8080/api` |

---

## Features implemented

- **Admin authentication** — JWT login, BCrypt-hashed passwords
- **Customers** — create/edit/search/paginate, active/inactive status
- **Accounts** — open, freeze/unfreeze, close; balances tracked as `BigDecimal`
- **Transactions** — deposit, withdrawal, transfer, with pessimistic row
  locking (no lost updates under concurrent requests) and deadlock-safe lock
  ordering on transfers
- **General ledger** — true double-entry bookkeeping: every transaction posts
  a matching DEBIT and CREDIT `LedgerEntry`, so the books are always
  auditable and balanced
- **Dashboard** — customer/account counts, total balance held, today's
  transaction count, recent activity feed
- **Combo/lookup endpoints** — dropdown-friendly `{label, value, metadata}`
  lists for customers and accounts, so the UI never asks a teller to type a
  raw ID or account number by hand

---

## API overview

All endpoints are under `/api` and (except `/api/auth/login`) require
`Authorization: Bearer <token>`.

| Resource     | Base path           | Key operations                                |
| ------------ | ------------------- | --------------------------------------------- |
| Auth         | `/api/auth`         | `POST /login`                                 |
| Customers    | `/api/customers`    | CRUD, search, status, `/combo` lookup         |
| Accounts     | `/api/accounts`     | open, list/filter, status, `/combo` lookup    |
| Transactions | `/api/transactions` | `/deposit`, `/withdraw`, `/transfer`, history |
| Ledger       | `/api/ledger`       | double-entry view, filterable by account      |
| Dashboard    | `/api/dashboard`    | summary + recent activity                     |

Full request/response shapes are documented in
`bms-backend/README.md`.

---

## Packaging the client

For day-to-day development, `mvn javafx:run` is all you need. To produce a
standalone, double-click-able app (no installer, no admin rights required):

```bash
cd bms-gui
mvn clean package -Pappimage
```

Output lands in `bms-gui/target/dist/CoreBankingClient/` (an
`.exe` on Windows, a `.app` bundle on macOS, or a `bin/` launcher on Linux).
Build on the OS you're targeting — `jpackage` doesn't cross-compile. Full
details are in `bms-gui/README.md`.

---

## Team & roles

| Role            | Responsibility                                     | Member(s)                                                             |
| --------------- | -------------------------------------------------- | --------------------------------------------------------------------- |
| Project Manager | Planning, coordination, initialization, deployment | Khim Phirom                                                           |
| Developer       | Code development, GUI design                       | Khim Phirom, Ly Diyamong, Den Vichet, Nhoeb Kimhov, Chhin Chhengheang |
| Database Admin  | Database design and management                     | Khim Phirom                                                           |
| Document Writer | Proposal, reports, documentation, user guidelines  | Khim Phirom, Ly Diyamong                                              |

---

## Future enhancements

Carried over from the original proposal, not yet implemented:

- Role-based access control (RBAC) enforced per endpoint (the JWT already
  carries a role claim — `SUPER_ADMIN` / `MANAGER` / `TELLER` — this just
  needs `@PreAuthorize` rules wired up)
- KYC (Know Your Customer) fields/flow for customer onboarding
- Exportable reports (transaction breakdown, total customers) as CSV/PDF
- A customer-facing side of the system (currently admin/teller only)
