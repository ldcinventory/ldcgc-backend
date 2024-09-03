package org.ldcgc.backend.db.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.app.exception.RequestException;
import org.ldcgc.backend.shared.enums.ETimeUnit;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TimeUnitConverterTest {

    private final TimeUnitConverter timeUnitConverter = new TimeUnitConverter();

    @Test
    void convertToDatabaseColumnNull() {
        assertNull(timeUnitConverter.convertToDatabaseColumn(null));
    }

    @Test
    void convertToDatabaseColumnValid() {
        assertNotNull(timeUnitConverter.convertToDatabaseColumn(ETimeUnit.DAYS));
    }

    @Test
    void convertToEntityAttributeNull() {
        assertNull(timeUnitConverter.convertToEntityAttribute(null));
    }

    @Test
    void convertToEntityAttributeException() {
        assertThrows(RequestException.class, () -> timeUnitConverter.convertToEntityAttribute(100));
    }

    @Test
    void convertToEntityAttributeValid() {
        assertNotNull(timeUnitConverter.convertToEntityAttribute(1));
    }

}
