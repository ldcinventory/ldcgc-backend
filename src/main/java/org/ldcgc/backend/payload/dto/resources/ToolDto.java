package org.ldcgc.backend.payload.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.ldcgc.backend.payload.dto.category.BrandDto;
import org.ldcgc.backend.payload.dto.category.ResourceTypeDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.util.common.EToolStatus;
import org.ldcgc.backend.util.common.EStockType;
import org.ldcgc.backend.util.common.ETimeUnit;
import org.ldcgc.backend.util.common.EUploadStatus;

import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolDto {

    private Integer id;
    @Schema(requiredMode = REQUIRED)
    private String barcode;
    @Schema(requiredMode = REQUIRED)
    private ResourceTypeDto resourceType;
    @Schema(requiredMode = REQUIRED)
    private BrandDto brand;
    @Schema(requiredMode = REQUIRED)
    private String name;
    private String model;
    private String description;
    private Float weight;
    @Schema(requiredMode = REQUIRED)
    private EStockType stockWeightType;
    private Float price;
    private LocalDate purchaseDate;
    private String[] urlImages;
    private Integer maintenancePeriod;
    private ETimeUnit maintenanceTime;
    private LocalDate lastMaintenance;
    private LocalDate nextMaintenance;
    private boolean modifyNextMaintenance;
    @Schema(requiredMode = REQUIRED)
    private EToolStatus status;
    @Schema(requiredMode = REQUIRED)
    private LocationDto location;
    @Schema(requiredMode = REQUIRED)
    private GroupDto group;
    @Setter private EUploadStatus uploadStatus;

}
