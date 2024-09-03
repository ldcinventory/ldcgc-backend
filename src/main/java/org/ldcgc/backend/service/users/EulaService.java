package org.ldcgc.backend.service.users;

import org.ldcgc.backend.shared.enums.EEULAStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface EulaService {

    ResponseEntity<?> getEULA(String token) throws ParseException;

    ResponseEntity<?> putEULA(String token, EEULAStatus action) throws ParseException;

}
