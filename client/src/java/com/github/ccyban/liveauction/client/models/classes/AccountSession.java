package com.github.ccyban.liveauction.client.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Auction;

import java.util.UUID;

public class AccountSession {
    private static AccountSession accountSession;

    public UUID accountSessionUUID;
    private Auction selectedAuction;

    private AccountSession() {
        accountSessionUUID = UUID.randomUUID();
    }

    public static AccountSession getAccountSession() {
        if (accountSession == null) {
            accountSession = new AccountSession();
        }

        return accountSession;
    }

    public Auction getSelectedAuction() {
        return selectedAuction;
    }

    public void setSelectedAuction(Auction selectedAuction) {
        this.selectedAuction = selectedAuction;
    }
}
