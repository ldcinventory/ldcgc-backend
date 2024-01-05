package org.ldcgc.backend.payload.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.util.common.EStatus;
import org.ldcgc.backend.util.common.ETimeUnit;

import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolDto {
    Integer id;
    String barcode;
    CategoryDto category;
    CategoryDto brand;
    String name;
    String model;
    String description;
    String urlImages;
    Integer maintenancePeriod;
    ETimeUnit maintenanceTime;
    LocalDateTime lastMaintenance;
    EStatus status;
    LocationDto location;
    GroupDto group;
}
