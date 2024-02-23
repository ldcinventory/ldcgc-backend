package org.ldcgc.backend.payload.dto.excel;

import lombok.Builder;
import lombok.Getter;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;

import java.util.Map;

@Getter
@Builder
public class ToolExcelMasterDto {
    private Map<String, ToolDto> tools;
    private Map<String, CategoryDto> brands;
    private Map<String, CategoryDto> categories;
    private Map<String, LocationDto> locations;
    private Map<String, GroupDto> groups;
}
