package com.github.ccyban.liveauction.client.models.classes;

import java.util.UUID;

public class User {
    private UUID userUUID;
    private String username;

    public User(UUID userUUID, String username) {
        this.userUUID = userUUID;
        this.username = username;
    }

    public UUID getUserUUID() {
        return userUUID;
    }

    public String getUsername() {
        return username;
    }
}
