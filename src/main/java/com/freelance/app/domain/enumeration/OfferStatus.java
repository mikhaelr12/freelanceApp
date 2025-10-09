package com.freelance.app.domain.enumeration;

/**
 * The OfferStatus enumeration.
 */
public enum OfferStatus {
    ACTIVE("Active"),
    PAUSED("Paused"),
    DENIED("Denied"),
    CANCELED("Canceled");

    private final String value;

    OfferStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
