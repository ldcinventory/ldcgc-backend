package org.ldcgc.backend.db.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.util.common.EWeekday;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class AvailabililtyConverterTest {

    @InjectMocks AvailabilityConverter availabilityConverter;

    @Test
    void convertToDatabaseColumnEmpty() {
        Set<EWeekday> weekdays = new HashSet<>();

        String result = availabilityConverter.convertToDatabaseColumn(weekdays);

        assertNull(result);
    }

    @Test
    void convertToDatabaseColumnNotEmpty() {
        Set<EWeekday> weekdays = Set.of(EWeekday.MONDAY, EWeekday.TUESDAY);

        String result = availabilityConverter.convertToDatabaseColumn(weekdays);

        assertNotNull(result);
    }

    @Test
    void convertToEntityAttributeEmpty() {
        String weekdays = null;

        assertNull(availabilityConverter.convertToEntityAttribute(weekdays));
    }

    @Test
    void convertToEntityAttributeNotEmpty() {
        String weekdays = "L,M,X,S,D";

        Set<EWeekday> weekdaySet = availabilityConverter.convertToEntityAttribute(weekdays);

        assertNotNull(weekdaySet);
    }

}
