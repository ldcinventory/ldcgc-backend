package org.ldcgc.backend.payload.dto.excel;

import lombok.Builder;
import org.ldcgc.backend.db.model.category.Category;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.db.model.resources.Consumable;

import java.util.Map;

@Builder
public class ConsumableExcelMasterDto {
        public Map<String, Consumable> consumable;
        public Map<String, Category> brands;
        public Map<String, Category> categories;
        public Map<String, Location> locations;
        public Map<String, Group> groups;
}
