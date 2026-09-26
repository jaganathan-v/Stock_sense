package com.stocksense.repository;

public interface ProductLocationStockProjection {
    Long getProductId();
    Long getLocationId();
    String getLocationName();
    Integer getCurrentStock();
}
