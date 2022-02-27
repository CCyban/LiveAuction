package com.github.ccyban.liveauction.client.models.classes;

import java.math.BigDecimal;
import java.util.UUID;

public class Bid {
    BigDecimal amount;
    UUID userUUID;

    public Bid (BigDecimal amount, UUID userUUID) {
        this.amount = amount;
        this.userUUID = userUUID;
    }
}
