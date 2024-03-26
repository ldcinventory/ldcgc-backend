package org.ldcgc.backend.payload.mapper.history;

import org.apache.commons.lang3.ObjectUtils;
import org.ldcgc.backend.db.model.history.ConsumableRegister;
import org.ldcgc.backend.payload.dto.history.ConsumableRegisterDto;
import org.ldcgc.backend.payload.mapper.resources.consumable.ConsumableMapper;
import org.ldcgc.backend.payload.mapper.users.VolunteerMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(uses = { ConsumableMapper.class, VolunteerMapper.class },
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConsumableRegisterMapper {

    ConsumableRegisterMapper MAPPER = Mappers.getMapper(ConsumableRegisterMapper.class);

    @Mapping(target = "consumableBardcode", source = "consumable.barcode")
    @Mapping(target = "volunteerBAId", source = "volunteer.builderAssistantId")
    @Mapping(target = "volunteerName", source = "volunteer.name")
    @Mapping(target = "volunteerLastName", source = "volunteer.lastName")
    @Mapping(target = "processingStockChanges", ignore = true)
    @Mapping(target = "volunteer", ignore = true)
    ConsumableRegisterDto toDto(ConsumableRegister consumableRegister);

    @Mapping(target = "consumable", ignore = true)
    @Mapping(target = "volunteer", ignore = true)
    @Mapping(target = "registerFrom", source = "registerFrom", qualifiedByName = "mapRegistrationIn")
    @Mapping(target = "closedRegister", source = "closedRegister", qualifiedByName = "mapClosedRegister")
    ConsumableRegister toEntity(ConsumableRegisterDto consumableRegisterDto);

    @Named("mapRegistrationIn")
    static LocalDateTime mapRegistrationIn(LocalDateTime localDateTimeFromDto) {
        return ObjectUtils.defaultIfNull(localDateTimeFromDto, LocalDateTime.now());
    }

    @Named("mapClosedRegister")
    static Boolean mapClosedRegister(Boolean closedRegister) {
        return ObjectUtils.defaultIfNull(closedRegister, Boolean.FALSE);
    }

    @Mapping(target = "consumable", ignore = true)
    @Mapping(target = "volunteer", ignore = true)
    void update(ConsumableRegisterDto from, @MappingTarget ConsumableRegister to);

}
