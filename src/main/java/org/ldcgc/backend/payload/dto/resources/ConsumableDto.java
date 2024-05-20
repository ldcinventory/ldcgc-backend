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
import org.ldcgc.backend.util.common.EStockType;
import org.ldcgc.backend.util.common.EUploadStatus;

import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsumableDto {

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
    private Float price;
    private LocalDate purchaseDate;
    private String[] urlImages;
    private Float quantityEachItem;
    @Schema(requiredMode = REQUIRED)
    private Float stock;
    private Float minStock;
    @Schema(requiredMode = REQUIRED)
    private EStockType stockType;
    @Schema(requiredMode = REQUIRED)
    private LocationDto location;
    @Schema(requiredMode = REQUIRED)
    private GroupDto group;
    @Setter private EUploadStatus uploadStatus;

}
