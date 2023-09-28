package org.ldcgc.backend.service.status.impl;

import org.ldcgc.backend.db.repository.resources.StatusRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.service.status.StatusService;
import org.ldcgc.backend.util.common.EStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.STATUS_NOT_FOUND;

public class StatusServiceImpl implements StatusService {

    @Autowired
    private StatusRepository repository;

    @Override
    public int getIdByEStatus(EStatus name) {
        return repository.findByName(name)
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, STATUS_NOT_FOUND.getMessage()))
                .getId();
    }
}
