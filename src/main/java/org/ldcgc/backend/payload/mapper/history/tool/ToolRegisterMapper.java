package org.ldcgc.backend.payload.mapper.history.tool;


import org.apache.commons.lang3.ObjectUtils;
import org.ldcgc.backend.db.model.history.ToolRegister;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.payload.dto.history.ToolRegisterDto;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
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

    @Mapping(target = "tool", source = "toolBarcode", qualifiedByName = "mapTool")
    @Mapping(target = "volunteer", source = "volunteerBuilderAssistantId", qualifiedByName = "mapVolunteer")
    @Mapping(target = "registerFrom", source = "registerFrom", qualifiedByName = "mapRegisterFrom")
    ToolRegister toMo(ToolRegisterDto dto);

    @Mapping(target = "tool", ignore = true)
    @Mapping(target = "volunteer", ignore = true)
    void update(ToolRegisterDto from, @MappingTarget ToolRegister to);

    @Named("mapRegisterFrom")
    static LocalDateTime mapRegisterFrom(LocalDateTime localDateTimeFromDto) {
        return ObjectUtils.defaultIfNull(localDateTimeFromDto, LocalDateTime.now());
    }
    @Named("mapTool")
    static Tool mapTool(String toolBarcode) {
        return Tool.builder()
                .barcode(toolBarcode)
                .build();
    }
    @Named("mapVolunteer")
    static Volunteer mapVolunteer(String volunteerBuilderAssistantId) {
        return Volunteer.builder()
                .builderAssistantId(volunteerBuilderAssistantId)
                .build();
    }
}
