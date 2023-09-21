package org.ldcgc.backend.payload.dto.resources;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.payload.dto.category.SubCategory;
import org.ldcgc.backend.payload.dto.group.Group;
import org.ldcgc.backend.payload.dto.location.LocationLvl2;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Tool implements Serializable {

    @Value @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DTO {
        Integer id;
        String barcode;
        SubCategory.DTO category;
        SubCategory.DTO brand;
        String name;
        String model;
        String description;
        String urlImages;
        Integer maintenancePeriod;
        SubCategory.DTO maintenanceTime;
        LocalDateTime lastMaintenance;
        Status.DTO status;
        @JsonAlias("location")
        LocationLvl2.DTO locationLvl2;
        Group.DTO group;
    }

}
