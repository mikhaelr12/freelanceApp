package com.freelance.app.domain.enumeration;

/**
 * The ProfileType enumeration.
 */
public enum ProfileType {
    FREELANCER("Freelancer"),
    CLIENT("Client");

    private final String value;

    ProfileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
