package com.stocksense.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class DashboardResponseDto {

    @JsonProperty("total_products")
    private Integer totalProducts;

    @JsonProperty("low_stock_threshold")
    private Integer lowStockThreshold;

    @JsonProperty("low_stock_items")
    private List<LowStockItemDto> lowStockItems = new ArrayList<>();

    public DashboardResponseDto() {
    }

    public DashboardResponseDto(Integer totalProducts, Integer lowStockThreshold, List<LowStockItemDto> lowStockItems) {
        this.totalProducts = totalProducts;
        this.lowStockThreshold = lowStockThreshold;
        this.lowStockItems = lowStockItems != null ? lowStockItems : new ArrayList<>();
    }

    public Integer getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Integer totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(Integer lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public List<LowStockItemDto> getLowStockItems() {
        return lowStockItems;
    }

    public void setLowStockItems(List<LowStockItemDto> lowStockItems) {
        this.lowStockItems = lowStockItems;
    }
}
