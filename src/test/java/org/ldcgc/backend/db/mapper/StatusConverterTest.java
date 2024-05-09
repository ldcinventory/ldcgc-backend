package org.ldcgc.backend.db.mapper;

import org.junit.jupiter.api.Test;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.common.EStatus;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StatusConverterTest {

    private final StatusConverter statusConverter = new StatusConverter();

    @Test
    void convertToDatabaseColumnStatusNull() {
        assertNull(statusConverter.convertToDatabaseColumn(null));
    }

    @Test
    void convertToDatabaseColumnValidStatus() {
        assertNotNull(statusConverter.convertToDatabaseColumn(EStatus.AVAILABLE));
    }

    @Test
    void convertToEntityAttributeThrowsException() {
        assertThrows(RequestException.class, () -> statusConverter.convertToEntityAttribute(10));
    }

    @Test
    void convertToEntityAttributeNull() {
        assertNull(statusConverter.convertToEntityAttribute(null));
    }

    @Test
    void convertToEntityValidAttribute() {
        assertNotNull(statusConverter.convertToEntityAttribute(0));
    }

}
