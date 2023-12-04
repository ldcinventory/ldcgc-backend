package org.ldcgc.backend.service.category;

import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.category.CategoryParentEnum;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {

    CategoryDto getCategoryParent(CategoryParentEnum parent);
    CategoryDto findCategorySonInParentByName(String name, CategoryDto parent);
}
