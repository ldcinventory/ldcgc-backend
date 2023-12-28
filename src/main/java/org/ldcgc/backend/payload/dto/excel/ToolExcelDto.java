package org.ldcgc.backend.payload.dto.excel;

import lombok.Builder;
import lombok.Getter;
import uk.co.jemos.podam.common.PodamIntValue;

import java.time.LocalDateTime;

@Getter
@Builder
public class ToolExcelDto {
    String barcode;
    String name;
    String brand;
    String model;
    String category;
    String description;
    String urlImages;
    String status;
    String location;

    @PodamIntValue(minValue = 1, maxValue = 5)
    Integer maintenancePeriod;
    String maintenanceTime;
    LocalDateTime lastMaintenance;
    String group;
}
