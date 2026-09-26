package com.stocksense.model;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Product Entity.
 * 
 * CORE DESIGN PRINCIPLE:
 * We intentionally DO NOT store any stock quantity column on this table.
 * All current stock calculations are dynamically derived from the append-only StockMove ledger.
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 60)
    private String sku;

    @Column(nullable = false, length = 80)
    private String category = "General";

    @Column(name = "unit_of_measure", nullable = false, length = 30)
    private String unitOfMeasure = "units";

    public Product() {
    }

    public Product(String name, String sku, String category, String unitOfMeasure) {
        this.name = name;
        this.sku = sku;
        this.category = (category == null || category.isBlank()) ? "General" : category;
        this.unitOfMeasure = (unitOfMeasure == null || unitOfMeasure.isBlank()) ? "units" : unitOfMeasure;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(sku, product.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sku);
    }
}
