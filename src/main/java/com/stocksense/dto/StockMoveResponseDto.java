package com.stocksense.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class StockMoveResponseDto {

    private Long id;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("location_id")
    private Long locationId;

    @JsonProperty("quantity_change")
    private Integer quantityChange;

    @JsonProperty("move_type")
    private String moveType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private String note;

    public StockMoveResponseDto() {
    }

    public StockMoveResponseDto(Long id, Long productId, Long locationId, Integer quantityChange, String moveType, LocalDateTime timestamp, String note) {
        this.id = id;
        this.productId = productId;
        this.locationId = locationId;
        this.quantityChange = quantityChange;
        this.moveType = moveType;
        this.timestamp = timestamp;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }

    public String getMoveType() {
        return moveType;
    }

    public void setMoveType(String moveType) {
        this.moveType = moveType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
