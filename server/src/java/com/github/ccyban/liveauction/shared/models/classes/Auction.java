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
    private ArrayList<Bid> bids;
    private ArrayList<UUID> userFollowers;
    private String sellerName;
    private BigDecimal startingBidPrice;
    private BigDecimal incrementalBidPace;

    public Auction(String name, ArrayList<Bid> bids, LocalDateTime expireDate, ArrayList<UUID> userFollowers, String sellerName, BigDecimal startingBidPrice, BigDecimal incrementalBidPace) {
        // Generate a UUID for the class
        this.auctionUUID = UUID.randomUUID();

        // Use payload values
        this.name = name;
        this.bids = bids;
        this.expireDate = expireDate;
        this.userFollowers = userFollowers;
        this.sellerName = sellerName;
        this.startingBidPrice = startingBidPrice;
        this.incrementalBidPace = incrementalBidPace;
    }

    public UUID getAuctionUUID() {
        return auctionUUID;
    }

    public String getName() {
        return name;
    }

    public SimpleStringProperty getNameStringProperty() {
        return new SimpleStringProperty(getName());
    }

    public Bid getTopBid() {
        Bid noBid = new Bid(new BigDecimal(0), null);

        Bid topBid = noBid;

        for (int i = 0; i < bids.size(); i++) {
            if (bids.get(i).amount.compareTo(topBid.amount) == 1) {
                topBid = bids.get(i);
            }
        }

        if (topBid.amount.equals(noBid.amount)) {
            return noBid;
        }
        else {
            return topBid;
        }
    }

    public SimpleStringProperty getTopBidStringProperty() {
        return new SimpleStringProperty(getTopBid().amount.toString());
    }

    public long getSecondsLeft() {
        Duration timeLeft = Duration.between(LocalDateTime.now(), expireDate);

        long secondsLeft = timeLeft.toSeconds();

        return secondsLeft;
    }

    public SimpleStringProperty getTimeLeftStringProperty() {
        long secondsLeft = getSecondsLeft();

        String timeLeft;

        if (secondsLeft > 0) {
            timeLeft = String.format("Ends in %d:%02d:%02d",
                    secondsLeft / 3600, (secondsLeft % 3600) / 60, (secondsLeft % 60));
            return new SimpleStringProperty(timeLeft);
        }
        else {
            long secondsAgo = secondsLeft * -1;
            timeLeft = String.format("Ended %d:%02d:%02d ago",
                    secondsAgo / 3600, (secondsAgo % 3600) / 60, (secondsAgo % 60));
        }

        return new SimpleStringProperty(timeLeft);
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

    public boolean hasBid(UUID userUUID) {
        for (Bid bid: bids) {
            if (bid.userUUID.equals(userUUID)) {
                return true;
            }
        }
        return false;
    }

    public String getSellerName() {
        return sellerName;
    }

    public BigDecimal getStartingBidPrice() {
        return startingBidPrice;
    }

    public BigDecimal getIncrementalBidPace() {
        return incrementalBidPace;
    }

    public void bid(Bid newBid) {
        if (getSecondsLeft() > 0) {
            bids.add(newBid);
        }
    }

    public ArrayList<Bid> getBids() {
        return bids;
    }

    public ArrayList<UUID> getUserFollowers() {
        return userFollowers;
    }

    public void toggleFollow(UUID userUUID) {
        if (userFollowers.contains(userUUID)) {
            userFollowers.remove(userUUID);
        }
        else {
            userFollowers.add(userUUID);
        }
    }
}
