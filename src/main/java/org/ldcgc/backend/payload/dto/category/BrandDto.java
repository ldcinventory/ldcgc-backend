package org.ldcgc.backend.payload.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrandDto {

    private Integer id;
    private String name;
    @Setter private Boolean locked;

}
