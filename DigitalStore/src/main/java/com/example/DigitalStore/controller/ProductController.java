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

    // GET return productOptions baserat på code
    @GetMapping("/{productCode}")
    public ResponseEntity<?> getOptionsForProduct(@PathVariable String productCode) {
        Products productC = productsRepository.findByProductCode(productCode);
        return ResponseEntity.ok(productC.getProductOptions());
    }


    // DELETE - Ta bort en produkt baserat på ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        // Kontrollera om produkten finns
        if (!productsRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Returnera 404 om produkten inte finns
        }

        try {
            // Hämta produkten för att säkerställa korrekt hantering av relaterade data
            Products product = productsRepository.findById(id).orElseThrow();

            // Radera produkten (relaterade ProductOptions tas bort p.g.a. CascadeType.ALL)
            productsRepository.delete(product);

            return ResponseEntity.noContent().build(); // Returnera 204 vid lyckad radering
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Returnera 500 vid fel
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


                existingProduct.setProductCode(productCode);
                existingProduct.setProductName(productName);
                existingProduct.setDescription(description);
                existingProduct.setPrice(price);

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
 main
    }
}


