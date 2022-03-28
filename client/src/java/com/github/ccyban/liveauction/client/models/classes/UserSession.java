package com.github.ccyban.liveauction.client.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Auction;

import java.util.UUID;

public class UserSession {
    private static UserSession userSession;

    private UUID userSessionUUID;
    private User user;
    private Auction selectedAuction;

    private UserSession() {
        userSessionUUID = UUID.randomUUID();
    }

    public static UserSession getUserSession() {
        if (userSession == null) {
            userSession = new UserSession();
        }

        return userSession;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Auction getSelectedAuction() {
        return selectedAuction;
    }

    public void setSelectedAuction(Auction selectedAuction) {
        this.selectedAuction = selectedAuction;
    }
}
