package com.example.DigitalStore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "product_options")
public class ProductOptions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    @JsonIgnore
    private Products productId;

    @ManyToOne
    @JoinColumn(name = "size_id", nullable = true)
    private Sizes size;

    @ManyToOne
    @JoinColumn(name = "color_id", nullable = true)
    private Colors color;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Products getProductId() {
        return productId;
    }
    public void setProductId(Products productId) {
        this.productId = productId;
    }

    public Sizes getSize() {
        return size;
    }
    public void setSize(Sizes size) {
        this.size = size;
    }

    public Colors getColor() {
        return color;
    }
    public void setColor(Colors color) {
        this.color = color;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
