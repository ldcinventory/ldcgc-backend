package org.ldcgc.backend.payload.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.util.common.EStatus;
import org.ldcgc.backend.util.common.EStockType;
import org.ldcgc.backend.util.common.ETimeUnit;

import java.time.LocalDate;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolDto {
    private Integer id;
    private String barcode;
    private CategoryDto category;
    private CategoryDto brand;
    private String name;
    private String model;
    private String description;
    private Float weight;
    private EStockType stockWeightType;
    private Float price;
    private LocalDate purchaseDate;
    private String[] urlImages;
    private Integer maintenancePeriod;
    private ETimeUnit maintenanceTime;
    private LocalDate lastMaintenance;
    private LocalDate nextMaintenance;
    private EStatus status;
    private LocationDto location;
    private GroupDto group;
}
