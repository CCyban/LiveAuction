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

            if (random.nextBoolean()) {
                Bid randomBid = new Bid(new BigDecimal((int)(Math.random()*(60))), UUID.fromString("8fc03087-d265-11e7-b8c6-83e29cd24f4c"));
                randomBids.add(randomBid);
            }

            Boolean randomHasFavourited = random.nextBoolean();

            auctions.add(new Auction("Some Auction", randomBids, randomExpiry, randomHasFavourited, "Boris", new BigDecimal(15), new BigDecimal(5)));
        }

        auctionRepository.auctions = auctions;
    }

    public static void mockAccountRepository(AccountRepository accountRepository) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        accounts.add(new Account("Harry", "Potter"));
        accounts.add(new Account("Darth", "Vader"));
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
