package org.ldcgc.backend.payload.dto.excel;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConsumableExcelDto {

    private String barcode;
    private String category;
    private String brand;
    private String name;
    private String model;
    private String description;
    private String urlImages;
    private Integer stock;
    private Integer minStock;
    private String stockType;
    private String location;
    private String group;
}
