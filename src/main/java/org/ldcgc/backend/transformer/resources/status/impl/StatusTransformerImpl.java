package org.ldcgc.backend.transformer.resources.status.impl;

import org.ldcgc.backend.db.model.resources.Status;
import org.ldcgc.backend.transformer.resources.status.StatusToEStatus;
import org.ldcgc.backend.util.common.EStatus;
import org.springframework.stereotype.Component;

@Component
public class StatusTransformerImpl {

    @StatusToEStatus
    public EStatus toDto(Status mo) {
        return mo.getName();
    }

}
