package org.ldcgc.backend.payload.dto.resources;

import lombok.Getter;
import org.ldcgc.backend.payload.dto.category.BrandDto;
import org.ldcgc.backend.payload.dto.category.ResourceTypeDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.util.common.EUploadStatus;

import java.time.LocalDate;

@Getter
public class ResourceDto {

    private Integer id;
    private String barcode;
    private ResourceTypeDto resourceType;
    private BrandDto brand;
    private String name;
    private String model;
    private String description;
    private Float price;
    private LocalDate purchaseDate;
    private String[] urlImages;
    private LocationDto location;
    private GroupDto group;
    private EUploadStatus uploadStatus;

}
