package org.ldcgc.backend.payload.dto.excel;

import lombok.Builder;
import lombok.Getter;
import org.ldcgc.backend.payload.dto.category.BrandDto;
import org.ldcgc.backend.payload.dto.category.ResourceTypeDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;

import java.util.Map;

@Getter
@Builder
public class ToolExcelMasterDto {
    private Map<String, ToolDto> tools;
    private Map<String, BrandDto> brands;
    private Map<String, ResourceTypeDto> resourceTypes;
    private Map<String, LocationDto> locations;
    private Map<String, GroupDto> groups;
}
