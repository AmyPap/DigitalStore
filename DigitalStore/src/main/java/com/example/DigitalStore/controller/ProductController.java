package com.example.DigitalStore.controller;

import com.example.DigitalStore.model.Products;
import com.example.DigitalStore.repository.ProductsRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

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
    }
}
