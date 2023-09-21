package org.ldcgc.backend.payload.dto.resources;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.payload.dto.category.SubCategory;
import org.ldcgc.backend.payload.dto.group.Group;
import org.ldcgc.backend.payload.dto.location.LocationLvl2;

import java.io.Serializable;

public class Consumable implements Serializable {

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
        Integer stock;
        Integer minStock;
        SubCategory.DTO stockType;
        @JsonAlias("location")
        LocationLvl2.DTO locationLvl2;
        Group.DTO group;
    }

}
