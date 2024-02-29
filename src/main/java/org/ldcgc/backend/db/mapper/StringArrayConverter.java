package org.ldcgc.backend.db.mapper;

import jakarta.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;

public class StringArrayConverter implements AttributeConverter<String[], String> {

    public String convertToDatabaseColumn(String[] s) {
        return s.length > 1 || StringUtils.isNotEmpty(s[0]) ? String.join(", ", s) : null;
    }

    public String[] convertToEntityAttribute(String s) {
        return StringUtils.isNotEmpty(s) ? s.split(", ") : null;
    }
}
