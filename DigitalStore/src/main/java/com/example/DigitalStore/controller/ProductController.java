package com.example.DigitalStore.controller;

import com.example.DigitalStore.DTO.*;
import com.example.DigitalStore.Service.NegativePriceException;
import com.example.DigitalStore.model.Products;
import com.example.DigitalStore.Service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductsDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{productCode}")
    public ResponseEntity<List<ProductOptionsGetDTO>> getOptionsForProduct(@PathVariable String productCode) {
        List<ProductOptionsGetDTO> product = productService.getProductOptions(productCode);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/filter-by-price")
    public ResponseEntity<?> filterProductsbyPrice(@RequestParam(value = "Max Price", required = false) Double maxPrice) {
        try {
            List<ProductsDTO> filteredProducts = productService.filterProductsbyPrice(maxPrice);
            return ResponseEntity.ok(filteredProducts);
        } catch (NegativePriceException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "Error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody(required = false) ProductsUpdateDTO updatedProduct) {
        try {
            Products updatedProductEntity = productService.updateProduct(id, updatedProduct);
            return ResponseEntity.ok(updatedProductEntity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductsUpdateDTO newProductDTO) {
        try {
            Products createdProduct = productService.createProduct(newProductDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}