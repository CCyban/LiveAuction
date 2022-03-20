package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Auction;
import com.github.ccyban.liveauction.shared.models.classes.Bid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class AuctionRepository {

    public static ArrayList getListOfAllAuctions() {

        ArrayList<Auction> listOfAllAuctions = new ArrayList<>();

        for (int x = 0; x < 100; x ++) {
            LocalDateTime randomExpiry = LocalDateTime.now().plusSeconds((int)(Math.random()*(70-5+1)+5));

            ArrayList<Bid> randomBids = new ArrayList<>();

            Random random = new Random();

            if (random.nextBoolean()) {
                Bid randomBid = new Bid(new BigDecimal((int)(Math.random()*(60))), UUID.fromString("8fc03087-d265-11e7-b8c6-83e29cd24f4c"));
                randomBids.add(randomBid);
            }

            Boolean randomHasFavourited = random.nextBoolean();

            listOfAllAuctions.add(new Auction("Some Auction", randomBids, randomExpiry, randomHasFavourited));
        }

        return listOfAllAuctions;
    }
}
