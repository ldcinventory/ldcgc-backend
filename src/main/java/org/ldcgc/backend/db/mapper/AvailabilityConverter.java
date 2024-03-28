package org.ldcgc.backend.db.mapper;

import jakarta.persistence.AttributeConverter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.util.common.EWeekday;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AvailabilityConverter implements AttributeConverter<Set<EWeekday>, String> {

    private static final String DELIMITER = ",";

    public String convertToDatabaseColumn(Set<EWeekday> attribute) {
        if (CollectionUtils.isEmpty(attribute))
            return null;

        return attribute.stream()
                .map(EWeekday::getAbbrv)
                .collect(Collectors.joining(DELIMITER));
    }

    public LinkedHashSet<EWeekday> convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData))
            return null;

        return Arrays.stream(dbData.split(DELIMITER))
                .map(EWeekday.abbrvWeekdayMap::get)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
