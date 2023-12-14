package org.ldcgc.backend.controller.users.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.users.EulaController;
import org.ldcgc.backend.service.users.EulaService;
import org.ldcgc.backend.util.common.EEULAStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;

@RequiredArgsConstructor
public class EulaControllerImpl implements EulaController {

    private final EulaService eulaService;

    public ResponseEntity<?> getEULA(String token) throws ParseException {
        return eulaService.getEULA(token);
    }

    public ResponseEntity<?> putEULA(String token, EEULAStatus action) throws ParseException {
        return eulaService.putEULA(token, action);
    }

}
