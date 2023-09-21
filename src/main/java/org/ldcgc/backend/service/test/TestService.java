package org.ldcgc.backend.service.test;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface TestService {

    ResponseEntity<?> testAccessWithCredentials();

    ResponseEntity<?> testAccessWithAdminCredentials();
}
