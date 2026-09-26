package com.stocksense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProductCreateDto {

    @NotBlank(message = "Product name is required")
    @Size(max = 120, message = "Product name cannot exceed 120 characters")
    private String name;

    @NotBlank(message = "SKU is required")
    @Size(max = 60, message = "SKU cannot exceed 60 characters")
    private String sku;

    @Size(max = 80, message = "Category cannot exceed 80 characters")
    private String category = "General";

    @JsonProperty("unit_of_measure")
    @Size(max = 30, message = "Unit of measure cannot exceed 30 characters")
    private String unitOfMeasure = "units";

    public ProductCreateDto() {
    }

    public ProductCreateDto(String name, String sku, String category, String unitOfMeasure) {
        this.name = name;
        this.sku = sku;
        this.category = (category == null || category.isBlank()) ? "General" : category;
        this.unitOfMeasure = (unitOfMeasure == null || unitOfMeasure.isBlank()) ? "units" : unitOfMeasure;
    }

    public String getName() {
        return name != null ? name.trim() : null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku != null ? sku.trim() : null;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCategory() {
        return (category == null || category.isBlank()) ? "General" : category.trim();
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnitOfMeasure() {
        return (unitOfMeasure == null || unitOfMeasure.isBlank()) ? "units" : unitOfMeasure.trim();
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
}
