package org.ldcgc.backend.payload.dto.excel;

import lombok.Builder;
import lombok.Getter;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.payload.dto.category.BrandDto;
import org.ldcgc.backend.payload.dto.category.ResourceTypeDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;

import java.util.Map;

@Getter
@Builder
public class ConsumableExcelMasterDto {
    private Map<String, ConsumableDto> consumables;
    private Map<String, BrandDto> brands;
    private Map<String, ResourceTypeDto> resourceTypes;
    private Map<String, LocationDto> locations;
    private Map<String, GroupDto> groups;
}
