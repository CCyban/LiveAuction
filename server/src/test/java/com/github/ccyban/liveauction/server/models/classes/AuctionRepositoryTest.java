package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Auction;
import com.github.ccyban.liveauction.shared.models.classes.Bid;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

class AuctionRepositoryTest {
    private AuctionRepository auctionRepository;
    private Auction newAuction;
    private UUID auctionUUID;

    private String auctionName;
    private ArrayList<Bid> bids;
    private UUID firstBidderUUID;
    private UUID topBidderUUID;
    private UUID followingUserUUID;
    private LocalDateTime auctionExpire;
    private ArrayList<UUID> userFollowers;
    private String seller;
    private BigDecimal startingBidPrice;
    private BigDecimal incrementalBidPace;

    @BeforeEach
    void setUp()  {
        auctionRepository = Mockito.spy(AuctionRepository.class);

        auctionName = "Some Auction";

        bids = new ArrayList<>();

        firstBidderUUID = UUID.randomUUID();
        topBidderUUID = UUID.randomUUID();

        bids.add(new Bid(new BigDecimal(50), firstBidderUUID));
        bids.add(new Bid(new BigDecimal(75), topBidderUUID));

        auctionExpire = LocalDateTime.of(1970, 1, 1, 0, 0, 0);

        userFollowers = new ArrayList<>();
        userFollowers.add(followingUserUUID);

        seller = "Some Seller";

        startingBidPrice = new BigDecimal(25);
        incrementalBidPace = new BigDecimal(5);

        newAuction = new Auction(auctionName, bids, auctionExpire, userFollowers, seller, startingBidPrice, incrementalBidPace);
        auctionUUID = newAuction.getAuctionUUID();
        auctionRepository.auctions.add(newAuction);
    }

    @Test
    void getListOfAllAuctions() {
        ArrayList<Auction> listOfAllAuctions = auctionRepository.getListOfAllAuctions();
        Assert.assertEquals(1, listOfAllAuctions.stream().count());

        Assert.assertTrue(listOfAllAuctions.contains(newAuction));
    }

    @Test
    void getAuctionByUUID() {
        Auction fetchedAuction = auctionRepository.getAuctionByUUID(auctionUUID);
        Assert.assertEquals(newAuction, fetchedAuction);

        Auction anotherFetchedAuction = auctionRepository.getAuctionByUUID(UUID.randomUUID());
        Assert.assertNull(anotherFetchedAuction);
    }

    @Test
    void bidOnAuction() {
        Bid newBid = new Bid(new BigDecimal(5000), UUID.randomUUID());
        auctionRepository.bidOnAuction(auctionUUID, newBid);
        Auction fetchedAuction = auctionRepository.getAuctionByUUID(auctionUUID);

        // Auction time filter stops bids on anything already finished
        Assert.assertNotEquals(newBid.userUUID, fetchedAuction.getTopBid().userUUID);
        Assert.assertNotEquals(newBid.amount, fetchedAuction.getTopBid().amount);


        // Now we test when bypassing time filter
        LocalDateTime newerExpireDateTime =  LocalDateTime.of(3030, 1, 1, 0, 0, 0);

        Auction anotherAuction = new Auction(auctionName, bids, newerExpireDateTime, userFollowers, seller, startingBidPrice, incrementalBidPace);
        auctionRepository.auctions.add(anotherAuction);

        auctionRepository.bidOnAuction(anotherAuction.getAuctionUUID(), newBid);
        fetchedAuction = auctionRepository.getAuctionByUUID(auctionUUID);

        // Bypasses the auction time filter by having a large finish date
        Assert.assertEquals(newBid.userUUID, fetchedAuction.getTopBid().userUUID);
        Assert.assertEquals(newBid.amount, fetchedAuction.getTopBid().amount);
    }

    @Test
    void toggleFollowByIds() {
        UUID randomUserUUID = UUID.randomUUID();

        // Follow toggle
        auctionRepository.toggleFollowByIds(auctionUUID, randomUserUUID);
        Auction fetchedAuction = auctionRepository.getAuctionByUUID(auctionUUID);
        ArrayList<UUID> userFollowers = fetchedAuction.getUserFollowers();
        Assert.assertTrue(userFollowers.contains(randomUserUUID));

        // Unfollow toggle
        auctionRepository.toggleFollowByIds(auctionUUID, randomUserUUID);
        fetchedAuction = auctionRepository.getAuctionByUUID(auctionUUID);
        userFollowers = fetchedAuction.getUserFollowers();
        Assert.assertFalse(userFollowers.contains(randomUserUUID));
    }
}