package com.stocksense.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class StockMoveResponseDto {

    private Long id;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_sku")
    private String productSku;

    @JsonProperty("location_id")
    private Long locationId;

    @JsonProperty("location_name")
    private String locationName;

    @JsonProperty("quantity_change")
    private Integer quantityChange;

    @JsonProperty("move_type")
    private String moveType;

    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private String note;

    @JsonProperty("supplier_name")
    private String supplierName;

    @JsonProperty("unit_of_measure")
    private String unitOfMeasure;

    public StockMoveResponseDto() {
    }

    public StockMoveResponseDto(Long id, Long productId, String productName, String productSku,
                                Long locationId, String locationName, Integer quantityChange,
                                String moveType, LocalDateTime timestamp, String note,
                                String supplierName, String unitOfMeasure) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productSku = productSku;
        this.locationId = locationId;
        this.locationName = locationName;
        this.quantityChange = quantityChange;
        this.moveType = moveType;
        this.timestamp = timestamp;
        this.note = note;
        this.supplierName = supplierName;
        this.unitOfMeasure = unitOfMeasure;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
}
