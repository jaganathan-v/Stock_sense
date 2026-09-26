package com.stocksense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReceiptRequestDto {

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

    @JsonProperty("supplier_name")
    @Size(max = 120, message = "Supplier name cannot exceed 120 characters")
    private String supplierName;

    public ReceiptRequestDto() {
    }

    public ReceiptRequestDto(Long productId, Integer quantity, String note) {
        this(productId, null, quantity, note, null);
    }

    public ReceiptRequestDto(Long productId, Long locationId, Integer quantity, String note, String supplierName) {
        this.productId = productId;
        this.locationId = locationId;
        this.quantity = quantity;
        this.note = note;
        this.supplierName = supplierName;
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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
