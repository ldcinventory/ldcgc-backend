package org.ldcgc.backend.payload.dto.excel;

import lombok.Builder;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;

import java.util.Map;

@Builder
public class ConsumableExcelMasterDto {
        public Map<String, ConsumableDto> consumable;
        public Map<String, CategoryDto> brands;
        public Map<String, CategoryDto> categories;
        public Map<String, LocationDto> locations;
        public Map<String, GroupDto> groups;
}
