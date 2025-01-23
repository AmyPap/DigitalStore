package com.example.DigitalStore.Service;

import com.example.DigitalStore.DTO.*;
import com.example.DigitalStore.model.*;
import com.example.DigitalStore.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Function;


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

    public List<ProductOptionsGetDTO> getProductOptions(String productCode) {
        Products product = productsRepository.findByProductCode(productCode);
        if (product == null) {
            throw new IllegalArgumentException("Product with code " + productCode + " not found.");
        }

        return product.getProductOptions().stream()
                .map(option -> {
                    ProductOptionsGetDTO getDTO = new ProductOptionsGetDTO();
                    getDTO.setId(option.getId());
                    getDTO.setSize(option.getSize() != null ? option.getSize().getSize() : "Unknown Size"); // if something is wrong with data
                    getDTO.setColor(option.getColor() != null ? option.getColor().getColor() : "Unknown Color");
                    getDTO.setStockQuantity(option.getStockQuantity());
                    return getDTO;
                })
                .toList();
    }

    /**
     * Filters list of products based on a specified maximum price and
     * Conversion of result to ProductsDTO objects
     *
     * @param maxPrice maximum price to filter products by, if null skips filtering
     * @return A list of ProductsDTO objects below the maximum Price
     * @throws NegativePriceException if maxPrice is equal to or less than zero
     */
    public List<ProductsDTO> filterProductsbyPrice(Double maxPrice) throws NegativePriceException {
        if (maxPrice == null) {
            return productsRepository.findAll().stream()
                    .map(product -> new ProductsDTO(
                                    product.getId(),
                                    product.getProductCode(),
                                    product.getProductName(),
                                    product.getDescription(),
                                    product.getPrice()
                            )
                    ).toList();
        }
        if (maxPrice <= 0.0) {
            throw new NegativePriceException("Price cannot be zero or lower.");
        }
        return productsRepository.findAll().stream()
                .filter(product -> product.getPrice() <= maxPrice)
                .map(product -> new ProductsDTO(
                                product.getId(),
                                product.getProductCode(),
                                product.getProductName(),
                                product.getDescription(),
                                product.getPrice()
                        )
                ).toList();
    }

    public void deleteProduct(Long id) {
        if (!productsRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found.");
        }
        productsRepository.deleteById(id);
    }

    /**
     * Updates an existing product with new details.
     * Only fields provided in the request will be updated.
     *
     * @param id             The ID of the product to update.
     * @param updatedProduct The details to update the product with.
     * @return The updated product.
     */
    @Transactional
    public Products updateProduct(Long id, ProductsUpdateDTO updatedProduct) {
        Products existingProduct = productsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));

        // Update fields if provided
        if (updatedProduct.getProductCode() != null) {
            existingProduct.setProductCode(updatedProduct.getProductCode());
        }
        if (updatedProduct.getProductName() != null) {
            existingProduct.setProductName(updatedProduct.getProductName());
        }
        if (updatedProduct.getPrice() != null) {
            existingProduct.setPrice(updatedProduct.getPrice());
        }
        if (updatedProduct.getCategoryId() != null) {
            Categories category = categoriesRepository.findById(updatedProduct.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category ID."));
            existingProduct.setCategoryId(category);
        }
        if (updatedProduct.getBrandId() != null) {
            Brands brand = brandsRepository.findById(updatedProduct.getBrandId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid brand ID."));
            existingProduct.setBrandId(brand);
        }
        if (updatedProduct.getDescription() != null) {
            existingProduct.setDescription(updatedProduct.getDescription());
        }

        // Update or add product options
        if (updatedProduct.getProductOptions() != null) {
            updateProductOptions(existingProduct, updatedProduct.getProductOptions());
        }

        return productsRepository.save(existingProduct);
    }
    /**
     * Updates or creates product options for a product.
     *
     * @param product          The product to update options for.
     * @param productOptionsDTOs The list of options to update or create.
     */
    private void updateProductOptions(Products product, List<ProductOptionsDTO> productOptionsDTOs) {
        // Map existing options
        Map<Long, ProductOptions> existingOptionsMap = product.getProductOptions().stream()
                .collect(Collectors.toMap(ProductOptions::getId, Function.identity()));

        for (ProductOptionsDTO optionDTO : productOptionsDTOs) {
            if (optionDTO.getId() != null) {
                // Update existing option
                ProductOptions existingOption = existingOptionsMap.get(optionDTO.getId());
                if (existingOption == null || !existingOption.getProductId().getId().equals(product.getId())) {
                    throw new IllegalArgumentException("Invalid product option ID: " + optionDTO.getId());
                }

                if (optionDTO.getSizeId() != null) {
                    Sizes size = sizesRepository.findById(optionDTO.getSizeId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid size ID: " + optionDTO.getSizeId()));
                    existingOption.setSize(size);
                }
                if (optionDTO.getColorId() != null) {
                    Colors color = colorsRepository.findById(optionDTO.getColorId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid color ID: " + optionDTO.getColorId()));
                    existingOption.setColor(color);
                }
                if (optionDTO.getStockQuantity() != null) {
                    existingOption.setStockQuantity(optionDTO.getStockQuantity());
                }
            } else { // Create new option
                if (optionDTO.getSizeId() == null || optionDTO.getColorId() == null || optionDTO.getStockQuantity() == null) {
                    throw new IllegalArgumentException("All fields (sizeId, colorId, stockQuantity) must be provided for new product options.");
                }

                ProductOptions newOption = new ProductOptions();
                newOption.setProductId(product);
                Sizes size = sizesRepository.findById(optionDTO.getSizeId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid size ID: " + optionDTO.getSizeId()));
                newOption.setSize(size);

                Colors color = colorsRepository.findById(optionDTO.getColorId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid color ID: " + optionDTO.getColorId()));
                newOption.setColor(color);

                newOption.setStockQuantity(optionDTO.getStockQuantity());
                product.getProductOptions().add(newOption);
            }
        }
    }
    /**
     * Creates a new product and its options.
     *
     * @param newProductDTO The product details to create.
     * @return The created product.
     */
    @Transactional
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
                .orElseThrow(() -> new IllegalArgumentException("Invalid Category ID: " + newProductDTO.getCategoryId()));
        productToSave.setCategoryId(category);

        Brands brand = brandsRepository.findById(newProductDTO.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Brand ID: " + newProductDTO.getBrandId()));
        productToSave.setBrandId(brand);


        if (newProductDTO.getProductOptions() != null) {
            for (ProductOptionsDTO newOptionDTO : newProductDTO.getProductOptions()) {
                if (newOptionDTO.getSizeId() == null || newOptionDTO.getColorId() == null  || newOptionDTO.getStockQuantity() == null) {
                    throw new IllegalArgumentException("All fields (SizeId,ColorId,Stock) must be provided.");
                }
                ProductOptions newOption = new ProductOptions();
                newOption.setProductId(productToSave);

                Sizes size = sizesRepository.findById(newOptionDTO.getSizeId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Size ID: " + newOptionDTO.getSizeId()));
                newOption.setSize(size);

                Colors color = colorsRepository.findById(newOptionDTO.getColorId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Color ID: " + newOptionDTO.getColorId()));
                newOption.setColor(color);

                if (newOptionDTO.getStockQuantity() == null || newOptionDTO.getStockQuantity() < 0) {
                    throw new IllegalArgumentException("Stock quantity must be greater than or equal to 0.");
                }
                newOption.setStockQuantity(newOptionDTO.getStockQuantity());
                productToSave.getProductOptions().add(newOption);
            }
        }
        return productsRepository.save(productToSave);
    }
}