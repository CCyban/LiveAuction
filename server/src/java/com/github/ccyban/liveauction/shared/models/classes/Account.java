package com.github.ccyban.liveauction.shared.models.classes;

import java.io.Serializable;
import java.util.UUID;

public class Account implements Serializable {
    private UUID accountUUID;
    private String username;
    private byte[] hashedPassword;

    public Account(String username, String password) {
        accountUUID = UUID.randomUUID();
        this.username = username;
        hashedPassword = org.apache.commons.codec.digest.DigestUtils.sha512(password);
    }

    public UUID getAccountUUID() {
        return accountUUID;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getHashedPassword() {
        return hashedPassword;
    }
}
