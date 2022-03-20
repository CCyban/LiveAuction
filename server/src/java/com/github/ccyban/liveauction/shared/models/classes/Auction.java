package com.github.ccyban.liveauction.shared.models.classes;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class Auction implements Serializable {
    private UUID auctionUUID;
    private String name;
    private LocalDateTime expireDate;
    private Boolean hasFavourited;
    private ArrayList<Bid> bids;

    public Auction(String name, ArrayList<Bid> bids, LocalDateTime expireDate, Boolean hasFavourited) {
        // Generate a UUID for the class
        this.auctionUUID = UUID.randomUUID();

        // Use payload values
        this.name = name;
        this.bids = bids;
        this.expireDate = expireDate;
        this.hasFavourited = hasFavourited;
    }

    public UUID getAuctionUUID() {
        return auctionUUID;
    }

    public SimpleStringProperty getNameStringProperty() {
        return new SimpleStringProperty(name.toString());
    }

    public SimpleStringProperty getTopBidStringProperty() {

        BigDecimal noBid = new BigDecimal(-1);

        BigDecimal topBid = noBid;

        for(int i = 0; i < bids.size(); i++) {
            if (bids.get(i).amount.compareTo(topBid) == 1) {
                topBid = bids.get(i).amount;
            }
        }

        if (topBid.equals(noBid)) {
            return new SimpleStringProperty("None");
        }
        else {
            return new SimpleStringProperty(topBid.toString());
        }
    }

    public SimpleStringProperty getTimeLeftStringProperty() {

        Duration timeLeft = Duration.between(LocalDateTime.now(), expireDate);

        long secondsLeft = timeLeft.toSeconds();

        if (secondsLeft > 0) {
            String formattedTimeLeft = String.format("Ends in %d:%02d:%02d",
                    secondsLeft / 3600, (secondsLeft % 3600) / 60, (secondsLeft % 60));
            return new SimpleStringProperty(formattedTimeLeft);

        }
        else {
            long secondsAgo = secondsLeft * -1;
            String formattedTimeLeft = String.format("Ended %d:%02d:%02d ago",
                    secondsAgo / 3600, (secondsAgo % 3600) / 60, (secondsAgo % 60));
            return new SimpleStringProperty(formattedTimeLeft);
        }
    }

    public SimpleStringProperty getHasFavouritedStringProperty() {
        return new SimpleStringProperty(hasFavourited ? ("★") : ("☆"));
    }

    public void setHasFavourited(Boolean hasFavourited) {
        this.hasFavourited = hasFavourited;
    }

    public boolean isFinished() {
        Duration timeLeft = Duration.between(LocalDateTime.now(), expireDate);

        long secondsLeft = timeLeft.toSeconds();

        if (secondsLeft > 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean hasFavourited() {
        return hasFavourited;
    }

    public boolean hasBid(UUID userUUID) {
        for (Bid bid: bids) {
            if (bid.userUUID.equals(userUUID)) {
                return true;
            }
        }
        return false;
    }
}
