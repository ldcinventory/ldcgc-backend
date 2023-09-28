package org.ldcgc.backend.transformer.status.impl;

import org.ldcgc.backend.db.model.resources.Status;
import org.ldcgc.backend.service.status.StatusService;
import org.ldcgc.backend.transformer.status.CustomStatusTransformer;
import org.ldcgc.backend.util.common.EStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomStatusTransformerImpl {

    @Autowired
    StatusService service;

    @CustomStatusTransformer
    public EStatus toDto(Status mo) {
        return mo.getName();
    }

    @CustomStatusTransformer
    public Status toMo(EStatus dto) {
        return Status.builder()
                .id(service.getIdByEStatus(dto))
                .name(dto)
                .build();
    }
}
