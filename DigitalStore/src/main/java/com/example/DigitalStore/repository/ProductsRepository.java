package com.example.DigitalStore.repository;

import com.example.DigitalStore.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {

    Products findByProductCode(String productCode);

    boolean existsByProductCode(String productCode);
}