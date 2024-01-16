package org.ldcgc.backend.payload.mapper.category;

import org.ldcgc.backend.db.model.category.Category;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    CategoryMapper MAPPER = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "parent.categories", expression = "java(null)")
    @Mapping(target = "categories", qualifiedByName = "mapNestedCategoriesParentAsNull")
    CategoryDto toDto(Category category);

    Category toMo(CategoryDto categoryDto);

    @Named("mapNestedCategoriesParentAsNull")
    static List<CategoryDto> mapNestedCategoriesParentAsNull(List<Category> categories) {
        return categories.stream().map(category -> {
            category.setParent(null);
            return CategoryMapper.MAPPER.toDto(category);
        }).toList();
    }

}
