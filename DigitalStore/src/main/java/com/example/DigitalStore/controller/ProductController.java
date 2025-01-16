package com.example.DigitalStore.controller;

import com.example.DigitalStore.model.Products;
import com.example.DigitalStore.repository.ProductsRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

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

    // GET return all products
    @GetMapping
    public List<Products> getAllProducts() {
        return productsRepository.findAll();
    }

    // GET return productOptions based on code
    @GetMapping("/{productCode}")
    public ResponseEntity<?> getOptionsForProduct(@PathVariable String productCode) {
        Products productC = productsRepository.findByProductCode(productCode);
        return ResponseEntity.ok(productC.getProductOptions());
    }

    // DELETE - Remove a product by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productsRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 if product does not exist
        }

        try {
            Products product = productsRepository.findById(id).orElseThrow();
            productsRepository.delete(product); // Delete the product (related ProductOptions removed due to CascadeType.ALL)
            return ResponseEntity.noContent().build(); // Return 204 on success
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 on error
        }
    }

    // PUT: Update an existing product with validation
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Products updatedProduct) {
        // Check if the product exists in the database
        Products existingProduct = productsRepository.findById(id).orElse(null);

        if (existingProduct == null) {
            // If product not found, return 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        // Check for validation on the incoming data
        if (updatedProduct.getProductCode() == null || updatedProduct.getProductCode().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product code cannot be null or blank.");
        }
        if (updatedProduct.getPrice() == null || updatedProduct.getPrice() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Price must be greater than 0.");
        }
        if (updatedProduct.getProductName() == null || updatedProduct.getProductName().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product name cannot be null or blank.");
        }

        // Check for duplicate productCode
        if (!existingProduct.getProductCode().equals(updatedProduct.getProductCode()) &&
                productsRepository.existsByProductCode(updatedProduct.getProductCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product code already exists.");
        }

        // Update the product fields
        existingProduct.setProductCode(updatedProduct.getProductCode());
        existingProduct.setProductName(updatedProduct.getProductName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());

        // Save the updated product
        productsRepository.save(existingProduct);

        // Return the updated product with status 200 OK
        return ResponseEntity.ok(existingProduct);
    }

    // Global exception handler for validation errors
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(IllegalArgumentException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return errors;
    }

    // Global exception handler for other errors, e.g. if the product does not exist
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleRuntimeException(RuntimeException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return errors;
    }
}


