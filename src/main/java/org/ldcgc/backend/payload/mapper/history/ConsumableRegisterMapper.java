package org.ldcgc.backend.payload.mapper.history;

import org.ldcgc.backend.db.model.history.ConsumableRegister;
import org.ldcgc.backend.payload.dto.history.ConsumableRegisterDto;
import org.ldcgc.backend.payload.mapper.resources.consumable.ConsumableMapper;
import org.ldcgc.backend.payload.mapper.users.VolunteerMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { ConsumableMapper.class, VolunteerMapper.class },
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConsumableRegisterMapper {

    ConsumableRegisterMapper MAPPER = Mappers.getMapper(ConsumableRegisterMapper.class);

    @Mapping(target = "consumableBardcode", source = "consumable.barcode")
    @Mapping(target = "volunteerBAId", source = "volunteer.builderAssistantId")
    ConsumableRegisterDto toDto(ConsumableRegister tool);

    @Mapping(target = "consumable", ignore = true)
    @Mapping(target = "volunteer", ignore = true)
    ConsumableRegister toEntity(ConsumableRegisterDto toolDto);

    @Mapping(target = "consumable", ignore = true)
    @Mapping(target = "volunteer", ignore = true)
    void update(ConsumableRegisterDto from, @MappingTarget ConsumableRegister to);

}
