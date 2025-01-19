package com.example.DigitalStore.DTO;

import java.util.List;

public class ProductsUpdateDTO {
    private String productCode;
    private String productName;
    private String description;
    private Double price;
    private Long categoryId;
    private Long brandId;
    private List<ProductOptionsDTO> productOptions;



    // Getters και Setters
    public String getProductCode() {
        return productCode;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getBrandId() {
        return brandId;
    }
    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public List<ProductOptionsDTO> getProductOptions() {
        return productOptions;
    }
    public void setProductOptions(List<ProductOptionsDTO> productOptions) {
        this.productOptions = productOptions;
    }
}







