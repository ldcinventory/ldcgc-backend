package org.ldcgc.backend.db.mapper;

import jakarta.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;

public class StringArrayConverter implements AttributeConverter<String[], String> {

    public String convertToDatabaseColumn(String[] s) {
        if(s == null || s.length == 0) // no elements
            return null;
        else if (s.length == 1) // just 1 element, no joinin delimiter required
            return s[0];

        return String.join(", ", s);
    }

    public String[] convertToEntityAttribute(String s) {
        return StringUtils.isNotEmpty(s) ? s.split(", ") : null;
    }
}
