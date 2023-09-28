package org.ldcgc.backend.transformer.status.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.resources.Status;
import org.ldcgc.backend.service.status.impl.StatusServiceImpl;
import org.ldcgc.backend.transformer.status.CustomStatusTransformer;
import org.ldcgc.backend.transformer.status.EStatusToStatus;
import org.ldcgc.backend.transformer.status.StatusToEStatus;
import org.ldcgc.backend.util.common.EStatus;
import org.springframework.stereotype.Component;

@Component
@CustomStatusTransformer
@RequiredArgsConstructor
public class CustomStatusTransformerImpl {

    private final StatusServiceImpl service;

    @StatusToEStatus
    public EStatus toDto(Status mo) {
        return mo.getName();
    }

    @EStatusToStatus
    public Status toMo(EStatus dto) {
        return Status.builder()
                .id(service.getIdByEStatus(dto))
                .name(dto)
                .build();
    }
}
