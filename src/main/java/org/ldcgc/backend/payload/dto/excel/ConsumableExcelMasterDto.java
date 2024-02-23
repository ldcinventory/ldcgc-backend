package org.ldcgc.backend.payload.dto.excel;

import lombok.Builder;
import lombok.Getter;
import org.ldcgc.backend.db.model.category.Category;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.db.model.resources.Consumable;

import java.util.Map;

@Getter
@Builder
public class ConsumableExcelMasterDto {
    private Map<String, Consumable> consumables;
    private Map<String, Category> brands;
    private Map<String, Category> resources;
    private Map<String, Location> locations;
    private Map<String, Group> groups;
}
