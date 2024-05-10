package org.ldcgc.backend.db.mapper;

import org.junit.jupiter.api.Test;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.common.EStockType;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StockTypeConverterTest {

    private final StockTypeConverter stockTypeConverter = new StockTypeConverter();

    @Test
    void convertToDatabaseColumnNull() {
        assertNull(stockTypeConverter.convertToDatabaseColumn(null));
    }

    @Test
    void convertToDatabaseColumnValid() {
        assertNotNull(stockTypeConverter.convertToDatabaseColumn(EStockType.KILOGRAMS));
    }

    @Test
    void convertToEntityAttributeNull() {
        assertNull(stockTypeConverter.convertToEntityAttribute(null));
    }

    @Test
    void convertToEntityAttributeException() {
        assertThrows(RequestException.class, () -> stockTypeConverter.convertToEntityAttribute(100));
    }

    @Test
    void convertToEntityAttributeValid() {
        assertNotNull(stockTypeConverter.convertToEntityAttribute(0));
    }

}
