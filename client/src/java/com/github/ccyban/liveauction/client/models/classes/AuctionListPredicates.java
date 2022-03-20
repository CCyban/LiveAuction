package com.github.ccyban.liveauction.client.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Auction;

import java.util.UUID;
import java.util.function.Predicate;

public class AuctionListPredicates {
    public static Predicate<Auction> predicateOnlyOngoings = a -> (!a.isFinished());
    public static Predicate<Auction> predicateOnlyFinished = a -> (a.isFinished());
    public static Predicate<Auction> predicateOnlyHasBid = a -> (a.hasBid(UUID.fromString("8fc03087-d265-11e7-b8c6-83e29cd24f4c")));
    public static Predicate<Auction> predicateOnlyFavourites = a -> (a.hasFavourited());
}
