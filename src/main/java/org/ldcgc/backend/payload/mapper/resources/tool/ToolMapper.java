package org.ldcgc.backend.payload.mapper.resources.tool;

import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.category.CategoryMapper;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.ldcgc.backend.util.constants.GoogleConstants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

@Mapper(uses = { LocationMapper.class, CategoryMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ToolMapper {

    ToolMapper MAPPER = Mappers.getMapper(ToolMapper.class);

    @Mapping(target = "location.locations", ignore = true)
    @Mapping(target = "location.parent.locations", ignore = true)
    @Mapping(target = "group.location.locations", ignore = true)
    @Mapping(target = "urlImages", source = "urlImages", qualifiedByName = "mapUrlImagesToDto")
    ToolDto toDto(Tool tool);

    @Named("mapUrlImagesToDto")
    static String[] mapUrlImagesToDto(String[] urlImages){
        if(urlImages == null) return null;

        return Arrays.stream(urlImages)
            .map(url -> String.format(GoogleConstants.DRIVE_IMAGES_URL, url))
            .toArray(String[]::new);
    }

    Tool toMo(ToolDto toolDto);

    List<Tool> toMo(List<ToolDto> tools);

    void update(ToolDto from, @MappingTarget Tool to);

}
