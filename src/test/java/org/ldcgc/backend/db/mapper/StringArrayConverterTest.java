package org.ldcgc.backend.db.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class StringArrayConverterTest {

    private final StringArrayConverter stringArrayConverter = new StringArrayConverter();

    @Test
    void convertToDatabaseColumnNull() {
        assertNull(stringArrayConverter.convertToDatabaseColumn(null));
    }

    @Test
    void convertToDatabaseColumnEmpty() {
        assertNull(stringArrayConverter.convertToDatabaseColumn(new String[]{}));
    }

    @Test
    void convertToDatabaseColumn1Element() {
        assertNotNull(stringArrayConverter.convertToDatabaseColumn(new String[]{"a"}));
    }

    @Test
    void convertToDatabaseColumnNElements() {
        assertNotNull(stringArrayConverter.convertToDatabaseColumn(new String[]{"a", "b", "c"}));
    }

    @Test
    void convertToEntityAttributeEmpty() {
        assertNull(stringArrayConverter.convertToEntityAttribute(""));
    }

    @Test
    void convertToEntityAttributeNotEmpty() {
        assertNotNull(stringArrayConverter.convertToEntityAttribute("a, b, c"));
    }

}
