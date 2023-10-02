package org.ldcgc.backend.service.status.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.resources.Status;
import org.ldcgc.backend.db.repository.resources.StatusRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.service.status.StatusService;
import org.ldcgc.backend.util.common.EStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.STATUS_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final StatusRepository repository;

    public int getIdByEStatus(EStatus name) {
        return repository.findByName(name)
            .map(Status::getId)
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, STATUS_NOT_FOUND.getMessage()));
    }
}
