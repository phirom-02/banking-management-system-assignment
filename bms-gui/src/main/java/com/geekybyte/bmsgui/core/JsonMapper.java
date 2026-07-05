package com.geekybyte.bmsgui.core;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Shared, correctly-configured Jackson mapper for talking to the Spring Boot API.
 */
public final class JsonMapper {

    private static final ObjectMapper INSTANCE = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    private JsonMapper() {
    }

    public static ObjectMapper get() {
        return INSTANCE;
    }
}
