package com.stocksense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LowStockItemDto {

    private Long id;
    private String name;
    private String sku;

    @JsonProperty("current_stock")
    private Integer currentStock;

    @JsonProperty("unit_of_measure")
    private String unitOfMeasure;

    public LowStockItemDto() {
    }

    public LowStockItemDto(Long id, String name, String sku, Integer currentStock, String unitOfMeasure) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.currentStock = currentStock;
        this.unitOfMeasure = unitOfMeasure;
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

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
}
