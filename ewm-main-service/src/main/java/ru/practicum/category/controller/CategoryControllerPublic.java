package ru.practicum.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.dto.CategoryOutputDto;
import ru.practicum.category.service.CategoryService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryControllerPublic {
    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    public CategoryOutputDto getCategoryById(@PathVariable @Positive final long catId) {
        return categoryService.getCategoryById(catId);
    }

    @GetMapping
    public List<CategoryOutputDto> getAllCategories(@RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                                    @RequestParam(defaultValue = "10") @Positive final int size) {
        return categoryService.getAllCategories(from, size);
    }
}
