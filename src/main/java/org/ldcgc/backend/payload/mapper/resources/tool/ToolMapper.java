package org.ldcgc.backend.payload.mapper.resources.tool;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

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
        toolDto.getLocation().setStoresResources(null);
        toolDto.getGroup().setLocation(null);
        toolDto.getResourceType().setLocked(null);
        return toolDto;
    }

    @Mapping(target = "barcode", source = "barcode", qualifiedByName = "mapToolBarcode")
    @Mapping(target = "nextMaintenance", source = ".", qualifiedByName = "calculateNextMaintenance")
    Tool toMo(ToolDto toolDto);

    @Named("calculateNextMaintenance")
    static LocalDate calculateNextMaintenance(ToolDto toolDto) {
        return switch (toolDto.getMaintenanceTime()) {
            case HOURS   -> LocalDate.now();
            case DAYS    -> LocalDate.now().plusDays(toolDto.getMaintenancePeriod());
            case WEEKS   -> LocalDate.now().plusWeeks(toolDto.getMaintenancePeriod());
            case MONTHS  -> LocalDate.now().plusMonths(toolDto.getMaintenancePeriod());
            case YEARS   -> LocalDate.now().plusYears(toolDto.getMaintenancePeriod());
            case NEVER,
                 UNKNOWN -> null;
        };
    }

    List<Tool> toMo(List<ToolDto> tools);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nextMaintenance", source = ".", qualifiedByName = "calculateNextMaintenanceWhenTrue")
    @Mapping(target = "urlImages", source = "urlImages", qualifiedByName = "extractImagesId")
    void update(ToolDto from, @MappingTarget Tool to);

    @Named("calculateNextMaintenanceWhenTrue")
    static LocalDate calculateNextMaintenanceWhenTrue(ToolDto toolDto) {
        return toolDto.isModifyNextMaintenance()
            ? ToolMapper.calculateNextMaintenance(toolDto)
            : toolDto.getNextMaintenance();
    }

    @Named("extractImagesId")
    static String[] extractImagesId(String[] urlImages) {
        return Stream.of(urlImages)
                .map(url -> url.substring(url.lastIndexOf("id=") + 3))
                .toArray(String[]::new);
    }

    @Named("mapToolBarcode")
    static String mapToolBarcode(String barcode) {
        return StringUtils.defaultIfBlank(barcode,
            "#" + RandomStringUtils.randomAlphanumeric(8).toUpperCase());
    }
}
