package org.ldcgc.backend.payload.dto.excel;

import lombok.Builder;
import lombok.Getter;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.db.model.location.Location;

import java.util.Map;

@Getter
@Builder
public class ResourceExcelMasterDto {
    private Map<String, Integer> tools;
    private Map<String, Integer> consumables;
    private Map<String, Brand> brands;
    private Map<String, ResourceType> resourceTypes;
    private Map<String, Location> locations;
}
