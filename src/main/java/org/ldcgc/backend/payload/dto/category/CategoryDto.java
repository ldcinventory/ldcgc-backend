package org.ldcgc.backend.payload.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Value
@Builder
@JsonInclude(NON_NULL)
public class CategoryDto {

    Integer id;
    String name;
    Boolean locked;
    @JsonInclude(NON_NULL)
    CategoryDto parent;
    @JsonInclude(NON_EMPTY)
    @Singular("category")
    List<CategoryDto> categories;

}
