package org.ldcgc.backend.payload.dto.resources;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.category.EStatus;
import org.ldcgc.backend.payload.dto.category.SubCategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationLvl2Dto;

import java.io.Serializable;
import java.time.LocalDateTime;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolDto implements Serializable {

    Integer id;
    String barcode;
    SubCategoryDto category;
    SubCategoryDto brand;
    String name;
    String model;
    String description;
    String urlImages;
    Integer maintenancePeriod;
    SubCategoryDto maintenanceTime;
    LocalDateTime lastMaintenance;
    EStatus status;
    @JsonAlias("location")
    LocationLvl2Dto locationLvl2;
    GroupDto group;

}
