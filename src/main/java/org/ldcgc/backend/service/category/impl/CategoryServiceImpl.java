package org.ldcgc.backend.service.category.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.repository.category.CategoryRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.category.CategoryParentEnum;
import org.ldcgc.backend.payload.mapper.category.CategoryMapper;
import org.ldcgc.backend.service.category.CategoryService;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Integer getCategoryIdByName(String categoryName) {
        return categoryRepository.findCategoryIdByName(categoryName);
    }

    @Override
    public CategoryDto getCategoryParent(CategoryParentEnum parent) {
        return categoryRepository.findByName(parent.getBbddName())
                .map(CategoryMapper.MAPPER::toDto)
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.CATEGORY_PARENT_NOT_FOUND.formatted(parent.getName(), parent.getBbddName())));
    }

    @Override
    public CategoryDto getCategoryByName(String name,CategoryDto parents) {
        return CategoryMapper.MAPPER.toDto(categoryRepository.getCategoryByName(name)
                .orElseThrow(() ->
                        new RequestException(HttpStatus.NOT_FOUND, Messages.Error.LOCATION_NOT_FOUND.formatted(parents.getCategories().stream().map(CategoryDto::getName).toList().toString())))

        );
    }
}
