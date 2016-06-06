package com.couchpod.authentication;

import java.security.Principal;

public class AuthUser implements Principal {
    private final Long userId;
    private final String name;

    public AuthUser(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Long getUserId() {
        return userId;
    }
}
