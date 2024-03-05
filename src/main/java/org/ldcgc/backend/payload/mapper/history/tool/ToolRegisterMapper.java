package org.ldcgc.backend.payload.mapper.history.tool;


import org.apache.commons.lang3.ObjectUtils;
import org.ldcgc.backend.db.model.history.ToolRegister;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.payload.dto.history.ToolRegisterDto;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;


@Mapper(uses = ToolMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ToolRegisterMapper {
    ToolRegisterMapper MAPPER = Mappers.getMapper(ToolRegisterMapper.class);

    @Mapping(target = "toolName", source = "tool.name")
    @Mapping(target = "toolBarcode", source = "tool.barcode")
    @Mapping(target = "toolUrlImages", source = "tool.urlImages")
    @Mapping(target = "volunteerName", source = "volunteer.name")
    @Mapping(target = "volunteerLastName", source = "volunteer.lastName")
    @Mapping(target = "volunteerBuilderAssistantId", source = "volunteer.builderAssistantId")
    ToolRegisterDto toDto(ToolRegister mo);

    @Mapping(target = "tool", ignore = true)
    @Mapping(target = "volunteer", ignore = true)
    @Mapping(target = "registerFrom", source = "registerFrom", qualifiedByName = "mapRegisterFrom")
    ToolRegister toMo(ToolRegisterDto dto);

    @Mapping(target = "tool", ignore = true)
    @Mapping(target = "volunteer", ignore = true)
    void update(ToolRegisterDto from, @MappingTarget ToolRegister to);

    @Named("mapRegisterFrom")
    static LocalDateTime mapRegisterFrom(LocalDateTime localDateTimeFromDto) {
        return ObjectUtils.defaultIfNull(localDateTimeFromDto, LocalDateTime.now());
    }
}
