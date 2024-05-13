package org.ldcgc.backend.db.mapper;

import jakarta.persistence.AttributeConverter;
import org.ldcgc.backend.util.common.EStockType;

import java.util.Optional;

public class StockTypeConverter implements AttributeConverter<EStockType, Integer> {

    public Integer convertToDatabaseColumn(EStockType eStockType) {
        return Optional.ofNullable(eStockType).map(EStockType::getId).orElse(null);
    }

    public EStockType convertToEntityAttribute(Integer id) {
        return Optional.ofNullable(id).map(EStockType::getStockTypeFromId).orElse(null);
    }
}
