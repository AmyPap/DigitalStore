package com.example.DigitalStore.repository;

import com.example.DigitalStore.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {

    // list productCode
    List<Products> findByProductCode(String productCode);

    // Custom Query för att hitta produkten baserat på productcode
    @Query("SELECT p FROM Products p WHERE p.productCode = :productCode")
    Products findProductByCode(@Param("productCode") String productCode);
}
