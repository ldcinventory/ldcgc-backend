package org.ldcgc.backend.payload.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubCategoryDto implements Serializable {

    Integer id;
    String name;
    Boolean locked = false;
    CategoryDto category;

}
