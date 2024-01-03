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

    private final CategoryRepository repository;
    @Override
    public CategoryDto getCategoryParent(CategoryParentEnum parent) {
        return repository.findByName(parent.getBbddName())
                .map(CategoryMapper.MAPPER::toDto)
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.CATEGORY_PARENT_NOT_FOUND.formatted(parent.getName(), parent.getBbddName())));
    }
}
