package com.geekybyte.bmsgui.core;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Thin wrapper around java.net.http.HttpClient that talks JSON to the Spring
 * Boot backend, attaches the JWT from Session automatically, and converts
 * non-2xx responses into ApiException with the backend's error message.
 */
public class ApiClient {

    private static final ObjectMapper MAPPER = JsonMapper.get();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String baseUrl = AppConfig.getApiBaseUrl();

    public <T> T get(String path, Class<T> responseType) {
        return send(request(path, "GET", null), responseType);
    }

    public <T> T get(String path, TypeReference<T> typeRef) {
        return send(request(path, "GET", null), typeRef);
    }

    public <T> T post(String path, Object body, Class<T> responseType) {
        return send(request(path, "POST", body), responseType);
    }

    public <T> T put(String path, Object body, Class<T> responseType) {
        return send(request(path, "PUT", body), responseType);
    }

    public <T> T patch(String path, Object body, Class<T> responseType) {
        return send(request(path, "PATCH", body), responseType);
    }

    public void delete(String path) {
        send(request(path, "DELETE", null), Void.class);
    }

    // ---- internals -------------------------------------------------

    private HttpRequest request(String path, String method, Object body) {
        String json = body != null ? writeJson(body) : "";

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");

        String token = Session.getInstance().getToken();
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }

        HttpRequest.BodyPublisher publisher = body != null
                ? HttpRequest.BodyPublishers.ofString(json)
                : HttpRequest.BodyPublishers.noBody();

        return switch (method) {
            case "GET" -> builder.GET().build();
            case "POST" -> builder.method("POST", publisher).build();
            case "PUT" -> builder.method("PUT", publisher).build();
            case "PATCH" -> builder.method("PATCH", publisher).build();
            case "DELETE" -> builder.method("DELETE", publisher).build();
            default -> throw new IllegalArgumentException("Unsupported method: " + method);
        };
    }

    private <T> T send(HttpRequest request, Class<T> responseType) {
        HttpResponse<String> response = execute(request);
        checkStatus(response);
        if (responseType == Void.class || response.body() == null || response.body().isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(response.body(), responseType);
        } catch (IOException e) {
            System.out.println("Failed to parse server response: " + e.getMessage());
            throw new ApiException(response.statusCode(), "Failed to parse server response: " + e.getMessage());
        }
    }

    private <T> T send(HttpRequest request, TypeReference<T> typeRef) {
        HttpResponse<String> response = execute(request);
        checkStatus(response);
        try {
            return MAPPER.readValue(response.body(), typeRef);
        } catch (IOException e) {
            System.out.println("Failed to parse server response: " + e.getMessage());
            throw new ApiException(response.statusCode(), "Failed to parse server response: " + e.getMessage());
        }
    }

    private HttpResponse<String> execute(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new ApiException(0, "Could not reach server at " + baseUrl + " (" + e.getMessage() + ")");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(0, "Request interrupted");
        }
    }

    private void checkStatus(HttpResponse<String> response) {
        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            return;
        }
        String message = extractErrorMessage(response.body(), status);
        throw new ApiException(status, message);
    }

    private String extractErrorMessage(String body, int status) {
        if (body == null || body.isBlank()) {
            return "Request failed with status " + status;
        }
        try {
            JsonNode node = MAPPER.readTree(body);
            if (node.has("message")) {
                return node.get("message").asText();
            }
        } catch (IOException ignored) {
            // fall through to raw body
        }
        return body;
    }

    private String writeJson(Object body) {
        try {
            return MAPPER.writeValueAsString(body);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to serialize request body: " + e.getMessage(), e);
        }
    }
}
