package org.ldcgc.backend.payload.dto.category;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubCategoryDto implements Serializable {

    Integer id;
    String name;
    Boolean locked;
    CategoryDto category;

    @JsonCreator
    public SubCategoryDto(@JsonProperty("id") Integer id,
                          @JsonProperty("name") String name,
                          @JsonProperty("locked") Boolean locked,
                          @JsonProperty("category") CategoryDto category) {
        this.id = id;
        this.name = name;
        this.locked = locked;
        this.category = category;
    }

}
