package com.stocksense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DeliveryRequestDto {

    @NotNull(message = "Product ID is required")
    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("location_id")
    private Long locationId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be a positive integer greater than zero")
    private Integer quantity;

    @Size(max = 255, message = "Note cannot exceed 255 characters")
    private String note;

    public DeliveryRequestDto() {
    }

    public DeliveryRequestDto(Long productId, Integer quantity, String note) {
        this(productId, null, quantity, note);
    }

    public DeliveryRequestDto(Long productId, Long locationId, Integer quantity, String note) {
        this.productId = productId;
        this.locationId = locationId;
        this.quantity = quantity;
        this.note = note;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
