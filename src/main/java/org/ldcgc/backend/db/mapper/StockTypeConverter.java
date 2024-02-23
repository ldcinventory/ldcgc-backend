package org.ldcgc.backend.db.mapper;

import jakarta.persistence.AttributeConverter;
import org.ldcgc.backend.util.common.EStockType;

public class StockTypeConverter implements AttributeConverter<EStockType, Integer> {

    public Integer convertToDatabaseColumn(EStockType eStockType) { return eStockType.getId(); }

    public EStockType convertToEntityAttribute(Integer id) { return EStockType.getStockTypeFromId(id); }
}
