package org.ldcgc.backend.payload.mapper.resources.tool;

import org.apache.commons.lang3.RandomStringUtils;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.apache.poi.util.StringUtil.isBlank;
import static org.ldcgc.backend.payload.mapper.common.MapperMethods.mapStringArrayWithPrefix;
import static org.ldcgc.backend.util.constants.Google.DRIVE_IMAGES_URL;

@Mapper(uses = LocationMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ToolMapper {

    ToolMapper MAPPER = Mappers.getMapper(ToolMapper.class);

    @Mapping(target = "location.locations", ignore = true)
    @Mapping(target = "group.location.locations", ignore = true)
    @Mapping(target = "urlImages", source = "urlImages", qualifiedByName = "mapToolUrlImagesToDto")
    ToolDto toDto(Tool tool);

    @Named("mapToolUrlImagesToDto")
    static String[] mapToolUrlImagesToDto(String[] urlImages){
        return mapStringArrayWithPrefix(urlImages, DRIVE_IMAGES_URL);
    }

    static ToolDto cleanProps(ToolDto toolDto) {
        toolDto.getLocation().setLocations(null);
        toolDto.getGroup().getLocation().setLocations(null);
        return toolDto;
    }

    @Mapping(target = "barcode", source = "barcode", qualifiedByName = "mapToolBarcode")
    Tool toMo(ToolDto toolDto);

    @Named("mapToolBarcode")
    static String mapBarcode(String barcodeDto) {
        return isBlank(barcodeDto)
            ? RandomStringUtils.randomAlphanumeric(10)
            : barcodeDto;
    }

    List<Tool> toMo(List<ToolDto> tools);

    @Mapping(target = "id", ignore = true)
    void update(ToolDto from, @MappingTarget Tool to);

}
