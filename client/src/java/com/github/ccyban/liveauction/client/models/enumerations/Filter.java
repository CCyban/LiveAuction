package com.github.ccyban.liveauction.client.models.enumerations;

public enum Filter {
    All("All"),
    Ongoing("Ongoing"),
    Finished("Finished"),
    PlacedABid("Placed a bid"),
    Favourited("Favourited");

    private String stringForm;

    Filter(String stringForm) {
        this.stringForm = stringForm;
    }

    // overriding toString() method for the enum instances
    public String toString() {
        return stringForm;
    }
}
