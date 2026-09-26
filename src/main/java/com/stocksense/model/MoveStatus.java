package com.stocksense.model;

public enum MoveStatus {
    CONFIRMED("confirmed"),
    DRAFT("draft");

    private final String value;

    MoveStatus(String value) { this.value = value; }

    public String getValue() { return value; }

    public static MoveStatus fromValue(String value) {
        for (MoveStatus status : values()) if (status.value.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) return status;
        throw new IllegalArgumentException("Unknown move status: " + value);
    }
}
