package org.ldcgc.backend.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface TestService {

    ResponseEntity<?> testAccessWithCredentials();

    ResponseEntity<?> testAccessWithAdminCredentials();
}
