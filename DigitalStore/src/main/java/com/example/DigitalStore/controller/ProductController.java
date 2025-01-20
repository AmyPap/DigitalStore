package com.example.DigitalStore.controller;

import com.example.DigitalStore.DTO.NameAndPrice;
import com.example.DigitalStore.DTO.ProductOptionsDTO;
import com.example.DigitalStore.DTO.ProductsDTO;
import com.example.DigitalStore.DTO.ProductsUpdateDTO;
import com.example.DigitalStore.model.*;
import com.example.DigitalStore.repository.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductsRepository productsRepository;
    private final SizesRepository sizesRepository;
    private final ColorsRepository colorsRepository;
    private final BrandsRepository brandsRepository;
    private final CategoriesRepository categoriesRepository;

    public ProductController(ProductsRepository productsRepository, SizesRepository sizesRepository,ColorsRepository colorsRepository,BrandsRepository brandsRepository,CategoriesRepository categoriesRepository) {
        this.productsRepository = productsRepository;
        this.sizesRepository = sizesRepository;
        this.colorsRepository = colorsRepository;
        this.brandsRepository = brandsRepository;
        this.categoriesRepository = categoriesRepository;
    }

    // GET return all products
    @GetMapping
    public List<ProductsDTO> getAllProducts() {
        // Fetch all products from the database, map them to ProductsDTO, and return a list
        return productsRepository.findAll().stream()
                .map(product -> new ProductsDTO(
                        product.getId(),
                        product.getProductCode(),
                        product.getProductName(),
                        product.getDescription(),
                        product.getPrice()
                ))
                .toList();
    }

    // GET return productOptions based on product code
    @GetMapping("/{productCode}")
    public ResponseEntity<?> getOptionsForProduct(@PathVariable String productCode) {
        Products productC = productsRepository.findByProductCode(productCode);
        if (productC == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product with code '" + productCode + "' not found.");
        }
        return ResponseEntity.ok(productC.getProductOptions());
    }

    @GetMapping("/filter")
    public List<Products> Filter(@RequestParam(value = "Max Price", required = false) Double maxPrice) throws IOException {
        if (maxPrice == null) {
            return productsRepository.findAll();
        }
        if (maxPrice <= 0.0) {
            throw new IllegalArgumentException("Price can't not zero or lower.");
        }

        return productsRepository.findAll().stream()
                .filter(product -> product.getPrice() <= maxPrice)
                .toList();
    }

    @GetMapping("/name-price")
    public List<NameAndPrice> NameAndPrice() {
        return productsRepository.findAll().stream()
                .map(product -> new NameAndPrice(product.getProductName(), product.getPrice()))
                .toList();
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

    // PUT: Update an existing product with validation
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductsUpdateDTO updatedProduct) {
        // Check if the product exists in the database
        Products existingProduct = productsRepository.findById(id).orElse(null);

        if (existingProduct == null) {
            // If product not found, return 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        // Check for duplicate productCode
        if (!existingProduct.getProductCode().equals(updatedProduct.getProductCode()) &&
                productsRepository.existsByProductCode(updatedProduct.getProductCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product code already exists.");
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
        // Validation for brandId
        if (updatedProduct.getBrandId() == null || updatedProduct.getBrandId() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BrandId must be a valid ID and cannot be 0.");
        }
        Brands brand = brandsRepository.findById(updatedProduct.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Brand ID: " + updatedProduct.getBrandId()));

        // Validation for categoryId
        if (updatedProduct.getCategoryId() == null || updatedProduct.getCategoryId() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CategoryId must be a valid ID and cannot be 0.");
        }
        Categories category = categoriesRepository.findById(updatedProduct.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Category Id: " +updatedProduct.getCategoryId()));

        // Update the product fields
        existingProduct.setProductCode(updatedProduct.getProductCode());
        existingProduct.setProductName(updatedProduct.getProductName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setBrandId(brand);
        existingProduct.setCategoryId(category);

        // Validation and update for productOptions
        if (updatedProduct.getProductOptions() != null) {
            Map<Long, ProductOptions> existingProductOptionsMap = existingProduct.getProductOptions().stream()
                    .collect(Collectors.toMap(ProductOptions::getId, option -> option));

            for (ProductOptionsDTO newOption : updatedProduct.getProductOptions()) {
                if (newOption.getId() == null) {
                    // New ProductOption
                    ProductOptions newProductOption = new ProductOptions();
                    newProductOption.setProductId(existingProduct);

                    // Validate sizeId
                    if (newOption.getSizeId() == null || newOption.getSizeId() == 0) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Size Id must be a valid ID and cannot be 0.");
                    }
                    Sizes size = sizesRepository.findById(newOption.getSizeId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid Size ID: " + newOption.getSizeId()));
                    newProductOption.setSize(size);

                    // Validate colorId
                    if (newOption.getColorId() == null || newOption.getColorId() == 0) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Color Id must be a valid ID and cannot be 0.");
                    }
                    Colors color = colorsRepository.findById(newOption.getColorId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid Color ID: " + newOption.getColorId()));
                    newProductOption.setColor(color);

                    // Validate stockQuantity
                    if (newOption.getStockQuantity() == null || newOption.getStockQuantity() < 0) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Stock Quantity must be greater than or equal to 0.");
                    }
                    newProductOption.setStockQuantity(newOption.getStockQuantity());

                    existingProduct.getProductOptions().add(newProductOption);

                } else {
                    // Update existing ProductOption
                    ProductOptions existingOption = existingProductOptionsMap.get(newOption.getId());
                    if (existingOption == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Invalid ProductOption ID: " + newOption.getId());
                    }

                    // Update sizeId
                    if (newOption.getSizeId() != null) {
                        Sizes size = sizesRepository.findById(newOption.getSizeId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Size ID: " + newOption.getSizeId()));
                        existingOption.setSize(size);
                    }

                    // Update colorId
                    if (newOption.getColorId() != null) {
                        Colors color = colorsRepository.findById(newOption.getColorId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Color ID: " + newOption.getColorId()));
                        existingOption.setColor(color);
                    }

                    // Update stockQuantity
                    if (newOption.getStockQuantity() != null && newOption.getStockQuantity() >= 0) {
                        existingOption.setStockQuantity(newOption.getStockQuantity());
                    }
                }
            }
        }

        // Save the updated product
        productsRepository.save(existingProduct);
        return ResponseEntity.ok(existingProduct);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductsUpdateDTO newProductDTO) {
        // Validation for productCode
        if (newProductDTO.getProductCode() == null || newProductDTO.getProductCode().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Product code is required and cannot be blank.");
            }
        if (productsRepository.existsByProductCode(newProductDTO.getProductCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Product code already exists.");
            }

        // Validation for productName
        if (newProductDTO.getProductName() == null || newProductDTO.getProductName().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Product name is required and cannot be blank.");
            }

        // Validation for price
        if (newProductDTO.getPrice() == null || newProductDTO.getPrice() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Price must be greater than 0.");
            }

        // Create a new product
        Products productToSave = new Products();
        productToSave.setProductCode(newProductDTO.getProductCode());
        productToSave.setProductName(newProductDTO.getProductName());
        productToSave.setDescription(newProductDTO.getDescription());
        productToSave.setPrice(newProductDTO.getPrice());

        // Connection with Categories
        if (newProductDTO.getCategoryId() != null) {
            Categories category = categoriesRepository.findById(newProductDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Invalid Category ID: " + newProductDTO.getCategoryId()));
            productToSave.setCategoryId(category);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Category ID is required.");
        }

        // Connection with Brands
        if (newProductDTO.getBrandId() != null) {
            Brands brand = brandsRepository.findById(newProductDTO.getBrandId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Invalid Brand ID: " + newProductDTO.getBrandId()));
            productToSave.setBrandId(brand);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Brand ID is required.");
        }

        // Managing ProductOptions
        if (newProductDTO.getProductOptions() != null) {
            for (ProductOptionsDTO newOptionDTO : newProductDTO.getProductOptions()) {
                // Validate that the ID is null or 0 (new ProductOption)
                if (newOptionDTO.getId() != null && newOptionDTO.getId() > 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("ProductOption ID must be 0 or null when creating a new product.");
                }
                // Create a new ProductOption
                ProductOptions newProductOption = new ProductOptions();
                newProductOption.setProductId(productToSave);

                // Add the size by sizeId
                if (newOptionDTO.getSizeId() != null) {
                    Sizes size = sizesRepository.findById(newOptionDTO.getSizeId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Invalid Size ID: " + newOptionDTO.getSizeId()));
                    newProductOption.setSize(size);
                }
                // Add the color by colorId
                if (newOptionDTO.getColorId() != null) {
                    Colors color = colorsRepository.findById(newOptionDTO.getColorId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Invalid Color ID: " + newOptionDTO.getColorId()));
                    newProductOption.setColor(color);
                }
                // Setting stock quantity
                if (newOptionDTO.getStockQuantity() == null || newOptionDTO.getStockQuantity() < 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Stock quantity must be greater than or equal to 0.");
                }
                newProductOption.setStockQuantity(newOptionDTO.getStockQuantity());

                productToSave.getProductOptions().add(newProductOption);
            }
        }

        // Save the product and its options
        Products savedProduct = productsRepository.save(productToSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
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


