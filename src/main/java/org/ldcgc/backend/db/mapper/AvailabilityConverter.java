package org.ldcgc.backend.db.mapper;

import jakarta.persistence.AttributeConverter;
import org.ldcgc.backend.util.common.EWeekday;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AvailabilityConverter implements AttributeConverter<List<EWeekday>, String> {

    private static final String DELIMITER = ",";

    public String convertToDatabaseColumn(List<EWeekday> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return attribute.stream()
                .map(EWeekday::getAbbrv)
                .collect(Collectors.joining(DELIMITER));
    }

    public List<EWeekday> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return Arrays.stream(dbData.split(DELIMITER))
                .map(EWeekday.abbrvWeekdayMap::get)
                .toList();
    }

}
