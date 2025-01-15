package com.example.demo.controller;

import com.example.demo.model.Products;
import com.example.demo.repository.ProductsRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;


@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductsRepository productsRepository;

    public ProductController(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    // GET return all products
    @GetMapping
    public List<Products> getAllProducts() {
        return productsRepository.findAll();
    }
    // GET return productOptions baserat på code
    @GetMapping("/{productCode}")
    public ResponseEntity<?> getOptionsForProduct(@PathVariable String productCode) {
        Products productC = productsRepository.findByProductCode(productCode);
        return ResponseEntity.ok(productC.getProductOptions());
    }
}
