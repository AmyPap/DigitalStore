package com.example.DigitalStore.controller;

import com.example.DigitalStore.model.Categories;
import com.example.DigitalStore.repository.CategoriesRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoriesController {

    private final CategoriesRepository categoriesRepository;

    public CategoriesController(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }
    // Get endpoint to fetch all Categories from the database
    @GetMapping
    public List<Categories> getAllCategories() {
        return categoriesRepository.findAll();
    }
}
