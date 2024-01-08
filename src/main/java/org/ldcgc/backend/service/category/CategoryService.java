package org.ldcgc.backend.service.category;

import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.category.CategoryParentEnum;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    Integer getCategoryIdByName(String categoryName);

    CategoryDto getCategoryParent(CategoryParentEnum parent);


    CategoryDto findCategorySonInParentByName(String name, CategoryDto parent);

}
