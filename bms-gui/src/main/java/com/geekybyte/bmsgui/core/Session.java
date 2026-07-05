package com.geekybyte.bmsgui.core;

/**
 * Holds the logged-in admin's JWT and identity for the lifetime of the app.
 * A simple singleton is enough here since this is a single-user desktop client.
 */
public final class Session {

    private static final Session INSTANCE = new Session();

    private String token;
    private String username;
    private String role;

    private Session() {
    }

    public static Session getInstance() {
        return INSTANCE;
    }

    public void set(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public void clear() {
        this.token = null;
        this.username = null;
        this.role = null;
    }

    public boolean isLoggedIn() {
        return token != null;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
