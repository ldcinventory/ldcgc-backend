package org.ldcgc.backend.service.test.impl;

import org.ldcgc.backend.service.test.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TestServiceImpl implements TestService {
    public ResponseEntity<?> testAccessWithCredentials() {
        return ResponseEntity.status(200).body("Everything OK with credentials!");
    }

    public ResponseEntity<?> testAccessWithAdminCredentials() {
        return ResponseEntity.status(200).body("Everything OK with admin credentials!");
    }
}
