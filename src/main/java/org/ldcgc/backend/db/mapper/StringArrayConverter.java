package org.ldcgc.backend.db.mapper;

import jakarta.persistence.AttributeConverter;

public class StringArrayConverter implements AttributeConverter<String[], String> {

    public String convertToDatabaseColumn(String[] s) { return String.join(", ", s); }

    public String[] convertToEntityAttribute(String s) { return s.split(", "); }
}
