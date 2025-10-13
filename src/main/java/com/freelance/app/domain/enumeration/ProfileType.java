package com.freelance.app.domain.enumeration;

/**
 * The ProfileType enumeration.
 */
public enum ProfileType {
    CLIENT("Client"),
    FREELANCER("Freelancer");

    private final String value;

    ProfileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
