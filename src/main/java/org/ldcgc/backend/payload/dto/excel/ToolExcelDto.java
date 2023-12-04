package org.ldcgc.backend.payload.dto.excel;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDateTime;

@Getter
@Value
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
    Integer maintenancePeriod;
    String maintenanceTime;
    LocalDateTime lastMaintenance;
    String group;
}
