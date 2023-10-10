package org.ldcgc.backend.db.mapper;

import jakarta.persistence.AttributeConverter;
import org.ldcgc.backend.util.common.EStatus;

public class StatusConverter implements AttributeConverter<EStatus, Integer> {

    public Integer convertToDatabaseColumn(EStatus status) {
        return status.getId();
    }

    public EStatus convertToEntityAttribute(Integer statusId) {
        return EStatus.getStatusFromId(statusId);
    }
}
