package org.ldcgc.backend.payload.dto.excel;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ConsumableExcelDto {

    String barcode;
    String category;
    String brand;
    String name;
    String model;
    String description;
    String urlImages;
    Integer stock;
    Integer minStock;
    String stockType;
    String location;
    String group;
}
