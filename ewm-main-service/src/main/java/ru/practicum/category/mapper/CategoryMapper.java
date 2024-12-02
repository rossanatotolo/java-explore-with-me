package ru.practicum.category.mapper;

import ru.practicum.category.dto.CategoryInputDto;
import ru.practicum.category.dto.CategoryOutputDto;
import ru.practicum.category.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryMapper {

    public static Category toCategory(final CategoryInputDto categoryInputDto) {

        final Category category = new Category();
        category.setName(categoryInputDto.getName());

        return category;
    }

    public static CategoryOutputDto toCategoryOutputDto(final Category category) {

        final CategoryOutputDto categoryOutputDto = new CategoryOutputDto();
        categoryOutputDto.setId(category.getId());
        categoryOutputDto.setName(category.getName());

        return categoryOutputDto;
    }

    public static List<CategoryOutputDto> toList(Iterable<Category> categories) {
        final List<CategoryOutputDto> result = new ArrayList<>();

        for (Category category : categories) {
            result.add(toCategoryOutputDto(category));
        }

        return result;
    }
}