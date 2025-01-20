package com.example.DigitalStore.Service;

import com.example.DigitalStore.DTO.NameAndPrice;
import com.example.DigitalStore.DTO.ProductOptionsDTO;
import com.example.DigitalStore.DTO.ProductsDTO;
import com.example.DigitalStore.DTO.ProductsUpdateDTO;
import com.example.DigitalStore.model.*;
import com.example.DigitalStore.repository.*;
import com.example.DigitalStore.Service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductsRepository productsRepository;
    private final SizesRepository sizesRepository;
    private final ColorsRepository colorsRepository;
    private final BrandsRepository brandsRepository;
    private final CategoriesRepository categoriesRepository;

    public ProductService(ProductsRepository productsRepository, SizesRepository sizesRepository, ColorsRepository colorsRepository, BrandsRepository brandsRepository, CategoriesRepository categoriesRepository) {
        this.productsRepository = productsRepository;
        this.sizesRepository = sizesRepository;
        this.colorsRepository = colorsRepository;
        this.brandsRepository = brandsRepository;
        this.categoriesRepository = categoriesRepository;
    }

    public List<ProductsDTO> getAllProducts() {
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

    public Products getProductByCode(String productCode) {
        return productsRepository.findByProductCode(productCode);
    }

    public List<Products> filterProducts(Double maxPrice) {
        if (maxPrice == null) {
            return productsRepository.findAll();
        }
        if (maxPrice <= 0.0) {
            throw new IllegalArgumentException("Price can't be zero or lower.");
        }

        return productsRepository.findAll().stream()
                .filter(product -> product.getPrice() <= maxPrice)
                .toList();
    }

    public List<NameAndPrice> getNameAndPrice() {
        return productsRepository.findAll().stream()
                .map(product -> new NameAndPrice(product.getProductName(), product.getPrice()))
                .toList();
    }

    public void deleteProduct(Long id) {
        if (!productsRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found.");
        }
        productsRepository.deleteById(id);
    }

    public Products updateProduct(Long id, ProductsUpdateDTO updatedProduct) {
        Products existingProduct = productsRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Product not found."));

        // Check for duplicate product code
        if (!existingProduct.getProductCode().equals(updatedProduct.getProductCode()) &&
                productsRepository.existsByProductCode(updatedProduct.getProductCode())) {
            throw new IllegalArgumentException("Product code already exists.");
        }

        existingProduct.setProductCode(updatedProduct.getProductCode());
        existingProduct.setProductName(updatedProduct.getProductName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());

        // Update brand and category
        Brands brand = brandsRepository.findById(updatedProduct.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Brand ID."));
        existingProduct.setBrandId(brand);

        Categories category = categoriesRepository.findById(updatedProduct.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Category ID."));
        existingProduct.setCategoryId(category);

        // Handle product options
        if (updatedProduct.getProductOptions() != null) {
            existingProduct.setProductOptions(updatedProduct.getProductOptions().stream()
                    .map(option -> {
                        ProductOptions productOption = new ProductOptions();
                        productOption.setProductId(existingProduct);

                        Sizes size = sizesRepository.findById(option.getSizeId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Size ID."));
                        productOption.setSize(size);

                        Colors color = colorsRepository.findById(option.getColorId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid Color ID."));
                        productOption.setColor(color);

                        productOption.setStockQuantity(option.getStockQuantity());
                        return productOption;
                    }).collect(Collectors.toList()));
        }

        return productsRepository.save(existingProduct);
    }

    public Products createProduct(ProductsUpdateDTO newProductDTO) {
        if (productsRepository.existsByProductCode(newProductDTO.getProductCode())) {
            throw new IllegalArgumentException("Product code already exists.");
        }

        Products productToSave = new Products();
        productToSave.setProductCode(newProductDTO.getProductCode());
        productToSave.setProductName(newProductDTO.getProductName());
        productToSave.setDescription(newProductDTO.getDescription());
        productToSave.setPrice(newProductDTO.getPrice());

        Categories category = categoriesRepository.findById(newProductDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Category ID."));
        productToSave.setCategoryId(category);

        Brands brand = brandsRepository.findById(newProductDTO.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Brand ID."));
        productToSave.setBrandId(brand);

        return productsRepository.save(productToSave);
    }
}