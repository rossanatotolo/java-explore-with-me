package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryInputDto;
import ru.practicum.category.dto.CategoryOutputDto;

import java.util.List;

public interface CategoryService {
    CategoryOutputDto createCategory(final CategoryInputDto categoryInputDto);

    CategoryOutputDto updateCategory(final long catId, final CategoryInputDto categoryInputDto);

    void deleteCategory(final long catId);

    CategoryOutputDto getCategoryById(final long catId);

    List<CategoryOutputDto> getAllCategories(final int from, final int size);
}
