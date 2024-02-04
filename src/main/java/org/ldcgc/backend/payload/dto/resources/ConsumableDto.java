package org.ldcgc.backend.payload.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;

import java.time.LocalDate;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsumableDto {

    Integer id;
    String barcode;
    CategoryDto category;
    CategoryDto brand;
    float price;
    LocalDate purchase_date;
    String name;
    String model;
    String description;
    String urlImages;
    Integer stock;
    Integer minStock;
    CategoryDto stockType;
    LocationDto location;
    GroupDto group;

}
