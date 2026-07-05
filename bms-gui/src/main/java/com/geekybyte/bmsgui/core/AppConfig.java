package com.geekybyte.bmsgui.core;

/**
 * Central place for client-side configuration. Override the API base URL with
 * either the CORE_BANKING_API_URL environment variable or the
 * -Dcore.banking.api.url=... JVM system property (system property wins).
 */
public final class AppConfig {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080/api";

    private AppConfig() {
    }

    public static String getApiBaseUrl() {
        String sysProp = System.getProperty("core.banking.api.url");
        if (sysProp != null && !sysProp.isBlank()) {
            return sysProp;
        }
        String env = System.getenv("CORE_BANKING_API_URL");
        if (env != null && !env.isBlank()) {
            return env;
        }
        return DEFAULT_BASE_URL;
    }
}
