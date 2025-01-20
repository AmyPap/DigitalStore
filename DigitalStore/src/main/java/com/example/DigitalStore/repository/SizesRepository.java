package com.example.DigitalStore.repository;

import com.example.DigitalStore.model.Sizes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizesRepository extends JpaRepository<Sizes,Long> {
}
