package com.stocksense.repository;

public interface ProductStockProjection {
    Long getProductId();
    Integer getCurrentStock();
}
