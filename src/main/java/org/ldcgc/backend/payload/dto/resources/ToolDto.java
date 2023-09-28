package org.ldcgc.backend.payload.dto.resources;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.ldcgc.backend.payload.dto.category.SubCategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationLvl2Dto;
import org.ldcgc.backend.util.common.EStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
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
