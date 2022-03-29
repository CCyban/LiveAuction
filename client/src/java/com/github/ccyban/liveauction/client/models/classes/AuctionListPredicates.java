package com.github.ccyban.liveauction.client.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Auction;

import java.util.function.Predicate;

public class AuctionListPredicates {
    public static Predicate<Auction> predicateOnlyOngoings = a -> (!a.isFinished());
    public static Predicate<Auction> predicateOnlyFinished = a -> (a.isFinished());
    public static Predicate<Auction> predicateOnlyHasBid = a -> (a.hasBid(AccountSession.getAccountSession().accountSessionUUID));
    public static Predicate<Auction> predicateOnlyFollowed = a -> (a.getUserFollowers().contains(AccountSession.getAccountSession().accountSessionUUID));
}
