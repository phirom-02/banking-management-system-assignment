package com.geekybyte.bmsgui.core;

/**
 * Thrown for any non-2xx response from the backend. Carries the human-readable
 * message extracted from the API's ApiError JSON body when available.
 */
public class ApiException extends RuntimeException {

    private final int statusCode;

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
