package com.example.DigitalStore.repository;

import com.example.DigitalStore.model.Brands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandsRepository extends JpaRepository<Brands, Long> {
}
