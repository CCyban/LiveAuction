package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Auction;
import com.github.ccyban.liveauction.shared.models.classes.Bid;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

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

            System.out.println("top bid is currently: " + auction.getTopBid());

            auction.bid(newBid);
            System.out.println("bid applied 💰");
            System.out.println("top bid is now: " + auction.getTopBid());
        }
        else {
            System.out.println("couldn't find auction off UUID");
        }
    }
}
