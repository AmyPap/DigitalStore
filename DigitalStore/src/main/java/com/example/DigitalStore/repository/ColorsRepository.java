package com.example.DigitalStore.repository;

import com.example.DigitalStore.model.Colors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorsRepository extends JpaRepository<Colors, Long> {
}
