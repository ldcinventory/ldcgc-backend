package org.ldcgc.backend.payload.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.ldcgc.backend.db.model.category.Category;

import java.io.Serializable;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Value
@Builder
@JsonInclude(NON_NULL)
public class CategoryDto implements Serializable {

    Integer id;
    String name;
    Boolean locked;
    @JsonInclude(NON_NULL)
    Category parent;
    @JsonInclude(NON_EMPTY)
    @Singular("category")
    List<CategoryDto> categories;

}
