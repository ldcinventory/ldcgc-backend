package org.ldcgc.backend.payload.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.NonNull;

import java.io.Serializable;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubCategoryDto implements Serializable {

    private Integer id;
    private String name;

    @NonNull
    private Boolean locked;
    private CategoryDto category;
}
