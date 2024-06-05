package org.ldcgc.backend.payload.mapper.history;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.model.history.ConsumableRegister;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.payload.dto.history.ConsumableRegisterDto;
import org.ldcgc.backend.payload.mapper.common.MapperMethods;
import org.ldcgc.backend.payload.mapper.resources.consumable.ConsumableMapper;
import org.ldcgc.backend.payload.mapper.users.VolunteerMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.ldcgc.backend.payload.mapper.common.MapperMethods.mapStringArrayWithPrefix;
import static org.ldcgc.backend.util.constants.Google.DRIVE_IMAGES_URL;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(uses = { ConsumableMapper.class, VolunteerMapper.class, MapperMethods.class },
        nullValuePropertyMappingStrategy = IGNORE)
public interface ConsumableRegisterMapper {

    ConsumableRegisterMapper MAPPER = Mappers.getMapper(ConsumableRegisterMapper.class);

    @Mapping(target = "consumableBarcode", source = "consumable.barcode")
    @Mapping(target = "consumableName", source = "consumable", qualifiedByName = "mapResourceName")
    @Mapping(target = "consumableUrlImages", source = "consumable", qualifiedByName = "mapConsumableRegisterUrlImagesToDto")
    @Mapping(target = "volunteerBuilderAssistantId", source = "volunteer.builderAssistantId")
    @Mapping(target = "volunteerName", source = "volunteer.name")
    @Mapping(target = "volunteerLastName", source = "volunteer.lastName")
    @Mapping(target = "consumableStockType", source = "consumable.stockType")
    @Mapping(target = "processingStockChanges", ignore = true)
    ConsumableRegisterDto toDto(ConsumableRegister consumableRegister);

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

    void update(ConsumableRegisterDto from, @MappingTarget ConsumableRegister to);

    @Named("mapConsumableRegisterUrlImagesToDto")
    static String[] mapConsumableRegisterUrlImagesToDto(Consumable consumable){
        return mapStringArrayWithPrefix(consumable.getUrlImages(), DRIVE_IMAGES_URL);
    }

}
