package com.example.DigitalStore.DTO;


public record ProductsDTO(
        Long id,
        String productCode,
        String productName,
        String description,
        Double price
) {}
