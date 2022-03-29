package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Auction;
import com.github.ccyban.liveauction.shared.models.classes.Bid;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class AuctionRepository {
    public ArrayList<Auction> auctions;

    public AuctionRepository() {
        // Mocking is used instead of having a real database call here
    }

    public ArrayList<Auction> getListOfAllAuctions() {
        return auctions;
    }

    public Auction getAuctionByUUID(UUID auctionUUID) {
        return auctions.stream().filter((auction -> auction.getAuctionUUID().equals(auctionUUID))).findFirst().orElse(null);
    }

    public void bidOnAuction(UUID auctionUUID, Bid newBid) {
        // Mocking is used instead of having a real database call here
        Auction auction = getAuctionByUUID(auctionUUID);
        if (auction != null) {
            auction.bid(newBid);
        }
        else {
            ServerLog.getInstance().log("Failed to find auction by UUID");
        }
    }

    public void toggleFollowByIds(UUID auctionUUID, UUID userUUID) {
        getAuctionByUUID(auctionUUID).toggleFollow(userUUID);
    }
}
