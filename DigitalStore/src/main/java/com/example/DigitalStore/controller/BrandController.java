package com.example.DigitalStore.controller;

import com.example.DigitalStore.model.Brands;
import com.example.DigitalStore.repository.BrandsRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/brands")
public class BrandController {

    private final BrandsRepository brandsRepository;
    // Constructor for DI (Dependency Injection)
    public BrandController(BrandsRepository brandsRepository) {
        this.brandsRepository = brandsRepository;
    }
    // Get endpoint to fetch all brands from the database
    @GetMapping
    public Map<Long, String> getAllBrandsAsMap() {
        return brandsRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Brands::getId,
                        Brands::getBrandName
                ));
    }
}
