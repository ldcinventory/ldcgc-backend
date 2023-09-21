package org.ldcgc.backend.payload.dto.resources;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.payload.dto.category.SubCategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationLvl2Dto;

import java.io.Serializable;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsumableDto implements Serializable {

    Integer id;
    String barcode;
    SubCategoryDto category;
    SubCategoryDto brand;
    String name;
    String model;
    String description;
    String urlImages;
    Integer stock;
    Integer minStock;
    SubCategoryDto stockType;
    @JsonAlias("location")
    LocationLvl2Dto locationLvl2;
    GroupDto group;

}
