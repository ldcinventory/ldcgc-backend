package org.ldcgc.backend.payload.mapper.resources.tool;

import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.common.MapperMethods;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(uses = MapperMethods.class,
        nullValuePropertyMappingStrategy = IGNORE)
public interface ToolMapper {

    ToolMapper MAPPER = Mappers.getMapper(ToolMapper.class);

    @Mapping(target = "brand.locked", ignore = true)
    @Mapping(target = "resourceType.locked", ignore = true)
    @Mapping(target = "location.locations", ignore = true)
    @Mapping(target = "location.parentLocationId", source = "location.parent.id")
    @Mapping(target = "group.location.locations", ignore = true)
    @Mapping(target = "urlImages", source = "urlImages", qualifiedByName = "mapUrlImages")
    ToolDto toDto(Tool tool);

    static ToolDto cleanProps(ToolDto toolDto) {
        toolDto.getGroup().setLocation(null);
        return toolDto;
    }

    @Mapping(target = "barcode", source = "barcode", qualifiedByName = "mapBarcode")
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

}
