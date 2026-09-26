package com.stocksense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductResponseDto {

    private Long id;
    private String name;
    private String sku;
    private String category;

    @JsonProperty("unit_of_measure")
    private String unitOfMeasure;

    @JsonProperty("current_stock")
    private Integer currentStock;

    public ProductResponseDto() {
    }

    public ProductResponseDto(Long id, String name, String sku, String category, String unitOfMeasure, Integer currentStock) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.unitOfMeasure = unitOfMeasure;
        this.currentStock = currentStock != null ? currentStock : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }
}
