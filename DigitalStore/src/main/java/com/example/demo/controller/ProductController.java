package com.example.demo.controller;

import com.example.demo.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping
    public List<Product> getAllProducts() {
        return Arrays.asList(
                new Product(1, "Football", "A professional football", 29.99, 100),
                new Product(2, "Basketball", "A professional basketball", 19.99, 50),
                new Product(3, "Tennis Racket", "A professional tennis racket", 89.99, 30)
        );
    }
}
