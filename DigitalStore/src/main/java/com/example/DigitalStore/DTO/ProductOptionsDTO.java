package com.example.DigitalStore.DTO;

public class ProductOptionsDTO {
    private Long id;
    private Long sizeId;
    private Long colorId;
    private Integer stockQuantity;

    // Getters και Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getSizeId() {
        return sizeId;
    }
    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public Long getColorId() {
        return colorId;
    }
    public void setColorId(Long colorId) {
        this.colorId = colorId;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}



