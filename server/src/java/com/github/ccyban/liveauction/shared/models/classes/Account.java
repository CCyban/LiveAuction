package com.github.ccyban.liveauction.shared.models.classes;

import java.io.Serializable;
import java.util.UUID;

public class Account implements Serializable {
    private UUID accountUUID;
    private String username;
    private String hashedPassword;

    public Account (String username, String hashedPassword) {
        accountUUID = UUID.randomUUID();
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}
