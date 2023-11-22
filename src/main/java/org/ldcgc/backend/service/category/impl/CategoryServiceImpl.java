package org.ldcgc.backend.service.category.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.repository.category.CategoryRepository;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.mapper.category.CategoryMapper;
import org.ldcgc.backend.service.category.CategoryService;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository repository;
    @Override
    public List<CategoryDto> getAllCategories() {
        return repository.findAll().stream()
                .map(CategoryMapper.MAPPER::toDto)
                .toList();
    }
}
