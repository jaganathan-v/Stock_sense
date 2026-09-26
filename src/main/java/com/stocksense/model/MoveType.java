package com.stocksense.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MoveType {
    RECEIPT("receipt"),
    DELIVERY("delivery"),
    TRANSFER("transfer"),
    ADJUSTMENT("adjustment");

    private final String value;

    MoveType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static MoveType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (MoveType type : values()) {
            if (type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown move type: " + value);
    }
}
