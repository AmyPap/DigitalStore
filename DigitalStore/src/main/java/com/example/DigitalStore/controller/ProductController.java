package com.example.DigitalStore.controller;

import com.example.DigitalStore.model.Products;
import com.example.DigitalStore.repository.ProductsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductsRepository productsRepository;

    public ProductController(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    // GET: Return all products
    @GetMapping
    public List<Products> getAllProducts() {
        return productsRepository.findAll();
    }

    // PUT: Update an existing product with validation
    @PutMapping("/{id}")
    public Products updateProduct(@PathVariable Long id, @RequestBody Products updatedProduct) {
        // Kontrollera om produkten finns i databasen
        return productsRepository.findById(id)
                .map(existingProduct -> {
                    // Validera inkommande data
                    if (updatedProduct.getProductCode() == null || updatedProduct.getProductCode().isBlank()) {
                        throw new IllegalArgumentException("Product code cannot be null or blank.");
                    }
                    if (updatedProduct.getPrice() == null || updatedProduct.getPrice() <= 0) {
                        throw new IllegalArgumentException("Price must be greater than 0.");
                    }
                    if (updatedProduct.getProductName() == null || updatedProduct.getProductName().isBlank()) {
                        throw new IllegalArgumentException("Product name cannot be null or blank.");
                    }

                    // Uppdatera produktens egenskaper
                    var productCode = updatedProduct.getProductCode();
                    var productName = updatedProduct.getProductName();
                    var description = updatedProduct.getDescription();
                    var price = updatedProduct.getPrice();
                    var brandId = updatedProduct.getBrandId();
                    var categoryId = updatedProduct.getCategoryId();

                    existingProduct.setProductCode(productCode);
                    existingProduct.setProductName(productName);
                    existingProduct.setDescription(description);
                    existingProduct.setPrice(price);
                    existingProduct.setBrandId(brandId);
                    existingProduct.setCategoryId(categoryId);

                    // Spara den uppdaterade produkten
                    return productsRepository.save(existingProduct);
                })
                .orElseThrow(() -> new RuntimeException("Product with ID " + id + " not found!"));
    }

    // Global exception handler för att hantera valideringsfel
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(IllegalArgumentException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return errors;
    }

    // Global exception handler för andra fel, t.ex. om produkten inte finns
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleRuntimeException(RuntimeException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return errors;
    }
}

