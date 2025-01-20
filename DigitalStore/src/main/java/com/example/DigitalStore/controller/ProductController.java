package com.example.DigitalStore.controller;

import com.example.DigitalStore.DTO.NameAndPrice;
import com.example.DigitalStore.DTO.ProductsDTO;
import com.example.DigitalStore.DTO.ProductsUpdateDTO;
import com.example.DigitalStore.model.Products;
import com.example.DigitalStore.Service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> getOptionsForProduct(@PathVariable String productCode) {
        Products product = productService.getProductByCode(productCode);
        if (product == null) {
            return ResponseEntity.status(404).body("Product not found.");
        }
        return ResponseEntity.ok(product.getProductOptions());
    }

    @GetMapping("/filter")
    public List<Products> filterProducts(@RequestParam(value = "Max Price", required = false) Double maxPrice) {
        return productService.filterProducts(maxPrice);
    }

    @GetMapping("/name-price")
    public List<NameAndPrice> getNameAndPrice() {
        return productService.getNameAndPrice();
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
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductsUpdateDTO updatedProduct) {
        try {
            Products updated = productService.updateProduct(id, updatedProduct);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductsUpdateDTO newProductDTO) {
        try {
            Products createdProduct = productService.createProduct(newProductDTO);
            return ResponseEntity.status(201).body(createdProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}