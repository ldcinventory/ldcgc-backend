package org.ldcgc.backend.payload.dto.resources;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsumableDto {

    Integer id;
    String barcode;
    CategoryDto category;
    CategoryDto brand;
    String name;
    String model;
    String description;
    String urlImages;
    Integer stock;
    Integer minStock;
    CategoryDto stockType;
    @JsonAlias("location")
    Location locationLvl2;
    GroupDto group;

}
