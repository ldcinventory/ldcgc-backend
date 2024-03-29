package org.ldcgc.backend.payload.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.util.common.EStockType;

import java.time.LocalDate;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsumableDto {

    private Integer id;
    private String barcode;
    private CategoryDto category;
    private CategoryDto brand;
    private Float price;
    private LocalDate purchaseDate;
    private String name;
    private String model;
    private String description;
    private String[] urlImages;
    private Float quantityEachItem;
    private Float stock;
    private Float minStock;
    private EStockType stockType;
    private LocationDto location;
    private GroupDto group;

}
