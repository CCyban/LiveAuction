package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Account;
import com.github.ccyban.liveauction.shared.models.classes.Auction;
import com.github.ccyban.liveauction.shared.models.classes.Bid;
import org.javatuples.Pair;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

public class MockHelper {

    private static ArrayList<Auction> auctions = new ArrayList<Auction>();
    private static ArrayList<Account> accounts = new ArrayList<>();

    public static void mockAuctionRepository(AuctionRepository auctionRepository) {

        for (int x = 0; x < 100; x ++) {
            LocalDateTime randomExpiry = LocalDateTime.now().plusSeconds((int)(Math.random()*(70-5+1)+5));

            ArrayList<Bid> randomBids = new ArrayList<>();

            Random random = new Random();

            ArrayList<UUID> randomFollowers = new ArrayList<>();

            BigDecimal randomStartingBidPrice = new BigDecimal(random.nextInt(100) + 5);

            BigDecimal randomBidIncrementPacing = new BigDecimal(random.nextInt(50) + 5);

            if (random.nextBoolean()) {
                Bid randomBid = new Bid(randomStartingBidPrice, UUID.randomUUID());
                randomBids.add(randomBid);
            }

            String auctionName = "";
            switch (random.nextInt(6)) {
                case 0 -> { auctionName = "Sony 50 Inch TV"; }
                case 1 -> { auctionName = "DJI Mini 2 Drone"; }
                case 2 -> { auctionName = "Tesla Model 3"; }
                case 3 -> { auctionName = "Apple Pencil"; }
                case 4 -> { auctionName = "SecretLab Chair"; }
                case 5 -> { auctionName = "Philips Air Fryer"; }
            }

            auctions.add(new Auction(auctionName, randomBids, randomExpiry, randomFollowers, "Jane", randomStartingBidPrice, randomBidIncrementPacing));
        }

        auctionRepository.auctions = auctions;
    }

    public static void mockAccountRepository(AccountRepository accountRepository) {
        accounts.add(new Account("Harry", "P0tt3r"));
        accounts.add(new Account("Darth", "Vad3r!"));

        // The below accounts exist to make it easier for concurrent client testing against one server
        accounts.add(new Account("1", "1"));
        accounts.add(new Account("2", "2"));
        accounts.add(new Account("3", "3"));
        accounts.add(new Account("4", "4"));
        accounts.add(new Account("5", "5"));
        accounts.add(new Account("6", "6"));
        accounts.add(new Account("7", "7"));
        accounts.add(new Account("8", "8"));

        accountRepository.accounts = accounts;
    }
}
