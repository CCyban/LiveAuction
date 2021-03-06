package com.github.ccyban.liveauction.shared.models.classes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public class Bid implements Serializable {
    public BigDecimal amount;
    public UUID userUUID;

    public Bid (BigDecimal amount, UUID userUUID) {
        this.amount = amount;
        this.userUUID = userUUID;
    }
}
