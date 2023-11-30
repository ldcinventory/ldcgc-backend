package org.ldcgc.backend.service.category.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.repository.category.CategoryRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.category.CategoryParentEnum;
import org.ldcgc.backend.payload.mapper.category.CategoryMapper;
import org.ldcgc.backend.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.CATEGORY_PARENT_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.CATEGORY_SON_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private final CategoryRepository categoryRepository;
    @Override
    public Integer getCategoryIdByName(String categoryName) {
        return categoryRepository.findCategoryIdByName(categoryName);
    }

    @Override
    public CategoryDto getCategoryParent(CategoryParentEnum parent) {
        return categoryRepository.findByName(parent.getBbddName())
                .map(CategoryMapper.MAPPER::toDto)
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(CATEGORY_PARENT_NOT_FOUND).formatted(parent.getName(), parent.getBbddName())));
    }

    @Override
    public CategoryDto findCategorySonInParentByName(String name, CategoryDto parent) {
        return parent.getCategories().stream()
                .filter(category -> category.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND,
                        getErrorMessage(CATEGORY_SON_NOT_FOUND)
                                .formatted(parent.getName(), name, parent.getName(), parent.getCategories().stream().map(CategoryDto::getName).toList().toString())));
    }
}
