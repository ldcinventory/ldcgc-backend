package org.ldcgc.backend.service.category.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.repository.category.CategoryRepository;
import org.ldcgc.backend.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
}
