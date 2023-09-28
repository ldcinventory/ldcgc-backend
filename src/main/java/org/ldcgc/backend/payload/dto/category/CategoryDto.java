package org.ldcgc.backend.payload.dto.category;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDto implements Serializable {

    Integer id;
    String name;
    Boolean locked;
    List<SubCategoryDto> subCategories;

    @JsonCreator
    public CategoryDto(@JsonProperty("id") Integer id,
                          @JsonProperty("name") String name,
                          @JsonProperty("locked") Boolean locked,
                          @JsonProperty("subCategories") List<SubCategoryDto> subCategories) {
        this.id = id;
        this.name = name;
        this.locked = locked;
        this.subCategories = subCategories;
    }

}
