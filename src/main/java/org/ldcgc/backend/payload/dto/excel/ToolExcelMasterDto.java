package org.ldcgc.backend.payload.dto.excel;

import lombok.Builder;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;

import java.util.List;

@Builder
public class ToolExcelMasterDto {
    public List<ToolDto> tools;
    public CategoryDto brandParent;
    public CategoryDto maintenanceTimeParent;
    public CategoryDto categoryParent;
    public List<LocationDto> locations;
    public List<GroupDto> groups;
}
