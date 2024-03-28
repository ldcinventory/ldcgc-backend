package org.ldcgc.backend.db.mapper;

import jakarta.persistence.AttributeConverter;
import org.ldcgc.backend.util.common.ETimeUnit;

import java.util.Optional;

public class TimeUnitConverter implements AttributeConverter<ETimeUnit, Integer> {

    public Integer convertToDatabaseColumn(ETimeUnit eTimeUnit) {
        return Optional.ofNullable(eTimeUnit).map(ETimeUnit::getId).orElse(null);
    }

    public ETimeUnit convertToEntityAttribute(Integer id) {
        return Optional.ofNullable(id).map(ETimeUnit::getTimeUnitFromId).orElse(null);
    }
}
