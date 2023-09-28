package org.ldcgc.backend.payload.dto.resources;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.ldcgc.backend.payload.dto.category.SubCategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationLvl2Dto;
import org.ldcgc.backend.util.common.EStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Value
@Builder
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

    @JsonCreator
    public ToolDto(@JsonProperty("id") Integer id,
                   @JsonProperty("barcode") String barcode,
                   @JsonProperty("category") SubCategoryDto category,
                   @JsonProperty("brand") SubCategoryDto brand,
                   @JsonProperty("name") String name,
                   @JsonProperty("model") String model,
                   @JsonProperty("description") String description,
                   @JsonProperty("urlImages") String urlImages,
                   @JsonProperty("maintenancePeriod") Integer maintenancePeriod,
                   @JsonProperty("maintenanceTime") SubCategoryDto maintenanceTime,
                   @JsonProperty("lastMaintenance") LocalDateTime lastMaintenance,
                   @JsonProperty("status") EStatus status,
                   @JsonProperty("location") LocationLvl2Dto locationLvl2,
                   @JsonProperty("group") GroupDto group) {
        this.id = id;
        this.barcode = barcode;
        this.category = category;
        this.brand = brand;
        this.name = name;
        this.model = model;
        this.description = description;
        this.urlImages = urlImages;
        this.maintenancePeriod = maintenancePeriod;
        this.maintenanceTime = maintenanceTime;
        this.lastMaintenance = lastMaintenance;
        this.status = status;
        this.locationLvl2 = locationLvl2;
        this.group = group;
    }

}
