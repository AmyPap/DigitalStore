package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Brands")
public class Brands {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand_name", nullable = false, unique = true)
    private String brandName;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;

    }
    public String getBrandName() {
        return brandName;
    }
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
