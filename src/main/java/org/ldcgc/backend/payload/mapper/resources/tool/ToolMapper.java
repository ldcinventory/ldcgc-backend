package org.ldcgc.backend.payload.mapper.resources.tool;

import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.category.CategoryMapper;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { LocationMapper.class, CategoryMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ToolMapper {

    ToolMapper MAPPER = Mappers.getMapper(ToolMapper.class);

    //@Mapping(source = "status.name", target = "status")
    ToolDto toDto(Tool tool);

    //@Mapping(target = "status", ignore = true)
    Tool toMo(ToolDto toolDto);

}
