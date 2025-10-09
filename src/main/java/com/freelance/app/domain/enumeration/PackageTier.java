package com.freelance.app.domain.enumeration;

/**
 * The PackageTier enumeration.
 */
public enum PackageTier {
    BASIC("Basic"),
    PREMIUM("Premium"),
    STANDARD("Standard");

    private final String value;

    PackageTier(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
