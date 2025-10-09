package com.freelance.app.domain.enumeration;

/**
 * The OrderStatus enumeration.
 */
public enum OrderStatus {
    PENDING("Pending"),
    ACTIVE("Active"),
    DELIVERED("Delivered"),
    COMPLETED("Completed"),
    CANCELED("Canceled"),
    DISPUTED("Disputed"),
    REFUNDED("Refunded");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
