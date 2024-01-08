package org.ldcgc.backend.db.mapper;

import jakarta.persistence.AttributeConverter;
import org.ldcgc.backend.util.common.ETimeUnit;

public class TimeUnitConverter implements AttributeConverter<ETimeUnit, Integer> {

    public Integer convertToDatabaseColumn(ETimeUnit eTimeUnit) { return eTimeUnit.getId(); }

    public ETimeUnit convertToEntityAttribute(Integer id) { return ETimeUnit.getTimeUnitFromId(id); }
}
