package com.freelance.app.domain.enumeration;

/**
 * The MediaKind enumeration.
 */
public enum MediaKind {
    IMAGE("Image"),
    VIDEO("Video"),
    DOCUMENT("Document");

    private final String value;

    MediaKind(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
