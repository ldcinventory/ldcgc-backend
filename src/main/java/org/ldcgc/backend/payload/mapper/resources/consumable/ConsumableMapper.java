package org.ldcgc.backend.payload.mapper.resources.consumable;

import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.mapper.category.CategoryMapper;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.ldcgc.backend.util.common.EStockType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { LocationMapper.class, CategoryMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConsumableMapper {

    ConsumableMapper MAPPER = Mappers.getMapper(ConsumableMapper.class);

    @Mapping(target = "group.location.locations", ignore = true)
    @Mapping(target = "stockType", source = "stockType", qualifiedByName = "mapStockTypeToDto")
    ConsumableDto toDto(Consumable consumable);

    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "stockType", source = "stockType", qualifiedByName = "mapStockTypeToMo")
    Consumable toMo(ConsumableDto consumableDto);

    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "group", ignore = true)
    void update(ConsumableDto from, @MappingTarget Consumable to);

    @Named("mapStockTypeToMo")
    static EStockType mapStockTypeToMo(String stockTypeName){
        return EStockType.getStockTypeByName(stockTypeName);
    }

    @Named("mapStockTypeToDto")
    static String mapStockTypeToDto(EStockType stockTypeName){
        return stockTypeName.getAbbr();
    }

}
