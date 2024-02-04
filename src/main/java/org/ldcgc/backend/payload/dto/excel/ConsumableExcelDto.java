package org.ldcgc.backend.payload.dto.excel;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Builder
@Value
public class ConsumableExcelDto {

    String brand;
    String category;
    String group;
    String locationLvl2;
    Integer minStock;
    Float price;
    LocalDate purchaseDate;
    Integer stock;
    String stockType;
    String barcode;
    String description;
    String model;
    String name;
    String urlImages;

}
