package com.example.DigitalStore.controller;

import com.example.DigitalStore.model.Colors;
import com.example.DigitalStore.repository.ColorsRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/colors")
public class ColorsController {

    private final ColorsRepository colorsRepository;

    public ColorsController(ColorsRepository colorsRepository) {
        this.colorsRepository = colorsRepository;
    }
    // Get endpoint to fetch all Colors with its ids from the database
    @GetMapping
    public Map<Long, String> getAllColorsAsMap() {
        return colorsRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Colors::getId,
                        Colors::getColor
                ));
    }
}
