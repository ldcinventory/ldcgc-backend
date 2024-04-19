package org.ldcgc.backend.service.test.impl;

import org.ldcgc.backend.service.test.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.ldcgc.backend.util.constants.Messages.Info.TEST_ACCESS_WITH_ADMIN_CREDENTIALS;
import static org.ldcgc.backend.util.constants.Messages.Info.TEST_ACCESS_WITH_CREDENTIALS;
import static org.ldcgc.backend.util.constants.Messages.Info.TEST_ACCESS_WITH_MANAGER_CREDENTIALS;

@Component
public class TestServiceImpl implements TestService {
    public ResponseEntity<?> testAccessWithCredentials() {
        return ResponseEntity.status(200).body(TEST_ACCESS_WITH_CREDENTIALS);
    }

    public ResponseEntity<?> testAccessWithManagerCredentials() {
        return ResponseEntity.status(200).body(TEST_ACCESS_WITH_MANAGER_CREDENTIALS);
    }

    public ResponseEntity<?> testAccessWithAdminCredentials() {
        return ResponseEntity.status(200).body(TEST_ACCESS_WITH_ADMIN_CREDENTIALS);
    }
}
