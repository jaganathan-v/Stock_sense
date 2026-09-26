package com.stocksense.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_moves", indexes = {
    @Index(name = "idx_stock_moves_product_id", columnList = "product_id"),
    @Index(name = "idx_stock_moves_location_id", columnList = "location_id"),
    @Index(name = "idx_stock_moves_timestamp", columnList = "timestamp")
})
public class StockMove {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange;

    @Enumerated(EnumType.STRING)
    @Column(name = "move_type", nullable = false, length = 30)
    private MoveType moveType;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 255)
    private String note;

    @Column(name = "supplier_name", length = 120)
    private String supplierName;

    public StockMove() {
        this.timestamp = LocalDateTime.now();
    }

    public StockMove(Product product, Location location, Integer quantityChange, MoveType moveType, String note) {
        this(product, location, quantityChange, moveType, note, null);
    }

    public StockMove(Product product, Location location, Integer quantityChange, MoveType moveType, String note, String supplierName) {
        this.product = product;
        this.location = location;
        this.quantityChange = quantityChange;
        this.moveType = moveType;
        this.note = note;
        this.supplierName = supplierName;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
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
}
