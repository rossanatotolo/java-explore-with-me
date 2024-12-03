package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryInputDto;
import ru.practicum.category.dto.CategoryOutputDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DuplicatedDataException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryOutputDto createCategory(final CategoryInputDto categoryInputDto) {
        if (categoryRepository.findAll().contains(CategoryMapper.toCategory(categoryInputDto))) {
            log.warn("Категория с id = {} уже добавлена.", categoryInputDto.getId());
            throw new DuplicatedDataException("Эта категория уже существует.");
        }

        final Category category = categoryRepository.save(CategoryMapper.toCategory(categoryInputDto));
        log.info("Категория c id {} добавлена.", category.getId());

        return CategoryMapper.toCategoryOutputDto(category);
    }

    @Override
    public CategoryOutputDto updateCategory(final long catId, final CategoryInputDto categoryInputDto) {
        final Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категории с id = {} не существует." + catId));

        if (Objects.nonNull(categoryInputDto.getName())) {
            category.setName(categoryInputDto.getName());
        }

        final Category categoryUpdate = categoryRepository.save(category);
        log.info("Категория с id = {} обновлена.", categoryUpdate.getId());
        return CategoryMapper.toCategoryOutputDto(categoryUpdate);

    }

    @Override
    public void deleteCategory(final long catId) {
        if (eventRepository.existsByCategoryId(catId)) {
            log.warn("Категория с id {} связана с событием и не может быть удалена.", catId);
            throw new ConflictException("Нельзя удалить категорию, с которой связаны события.");
        }

        categoryRepository.deleteById(catId);
        log.info("Категория с id  = {} удалена.", catId);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryOutputDto getCategoryById(final long catId) {
        final Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категории с id = {} не существует." + catId));

        log.info("Получение категории по id {}.", catId);
        return CategoryMapper.toCategoryOutputDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryOutputDto> getAllCategories(final int from, final int size) {
        final Pageable pageable = PageRequest.of(from / size, size);

        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        log.info("Получение списка всех категорий.");
        return CategoryMapper.toList(categoryPage.getContent());
    }
}
