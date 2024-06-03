package org.ldcgc.backend.db.mapper;

import jakarta.persistence.AttributeConverter;
import org.ldcgc.backend.util.common.EToolStatus;

import java.util.Optional;

public class StatusConverter implements AttributeConverter<EToolStatus, Integer> {

    public Integer convertToDatabaseColumn(EToolStatus status) {
        return Optional.ofNullable(status).map(EToolStatus::getId).orElse(null);
    }

    public EToolStatus convertToEntityAttribute(Integer statusId) {
        return Optional.ofNullable(statusId).map(EToolStatus::getStatusFromId).orElse(null);
    }
}
