package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Auction;
import com.github.ccyban.liveauction.shared.models.classes.Bid;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

public class MockHelper {

    private static ArrayList<Auction> auctions = new ArrayList<Auction>();

    public static void mockAuctionRepository(AuctionRepository auctionRepository) {

        for (int x = 0; x < 100; x ++) {
            LocalDateTime randomExpiry = LocalDateTime.now().plusSeconds((int)(Math.random()*(70-5+1)+5));

            ArrayList<Bid> randomBids = new ArrayList<>();

            Random random = new Random();

            if (random.nextBoolean()) {
                Bid randomBid = new Bid(new BigDecimal((int)(Math.random()*(60))), UUID.fromString("8fc03087-d265-11e7-b8c6-83e29cd24f4c"));
                randomBids.add(randomBid);
            }

            Boolean randomHasFavourited = random.nextBoolean();

            auctions.add(new Auction("Some Auction", randomBids, randomExpiry, randomHasFavourited, "Boris", new BigDecimal(15), new BigDecimal(5)));
        }

        auctionRepository.auctions = auctions;

        // Mocks getAuctionByUUID
        for (Auction auction: auctions) {
            Mockito.when(auctionRepository.getAuctionByUUID(auction.getAuctionUUID())).thenCallRealMethod();
            Mockito.doCallRealMethod().when(auctionRepository).bidOnAuction(any(UUID.class), any(Bid.class));
        }

        // Mocks getListOfAllAuctions
        Mockito.when(auctionRepository.getListOfAllAuctions()).thenCallRealMethod();
    }
}
