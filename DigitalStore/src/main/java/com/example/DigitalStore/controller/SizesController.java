package com.example.DigitalStore.controller;

import com.example.DigitalStore.model.Sizes;
import com.example.DigitalStore.repository.SizesRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sizes")
public class SizesController {
    private final SizesRepository sizesRepository;

    public SizesController(SizesRepository sizesRepository) {
        this.sizesRepository = sizesRepository;
    }
    // Get endpoint to fetch all Sizes and its ids from the database
    @GetMapping
    public Map<Long, String> getAllSizesAsMap() {
        return sizesRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Sizes::getId,       // Key
                        Sizes::getSize // Value
                ));
    }
}

