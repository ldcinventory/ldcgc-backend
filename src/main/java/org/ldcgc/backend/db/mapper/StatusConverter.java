package org.ldcgc.backend.db.mapper;

import jakarta.persistence.AttributeConverter;
import org.ldcgc.backend.util.common.EStatus;

import java.util.Optional;

public class StatusConverter implements AttributeConverter<EStatus, Integer> {

    public Integer convertToDatabaseColumn(EStatus status) {
        return Optional.ofNullable(status).map(EStatus::getId).orElse(null);
    }

    public EStatus convertToEntityAttribute(Integer statusId) {
        return Optional.ofNullable(statusId).map(EStatus::getStatusFromId).orElse(null);
    }
}
