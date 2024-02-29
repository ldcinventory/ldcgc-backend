package org.ldcgc.backend.payload.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Builder
@JsonInclude(NON_NULL)
public class CategoryDto {

    private Integer id;
    private String name;
    private Boolean locked;
    @JsonInclude(NON_NULL)
    private CategoryDto parent;
    @JsonInclude(NON_EMPTY)
    @Singular("category")
    private List<CategoryDto> categories;

}
