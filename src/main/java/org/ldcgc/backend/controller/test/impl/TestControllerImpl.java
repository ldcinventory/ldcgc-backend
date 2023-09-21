package org.ldcgc.backend.controller.test.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.test.TestController;
import org.ldcgc.backend.service.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestControllerImpl implements TestController {

    private final TestService testService;

    public ResponseEntity<?> testAccessWithCredentials() {
        return testService.testAccessWithCredentials();
    }

    public ResponseEntity<?> testAccessWithAdminCredentials() {
        return testService.testAccessWithAdminCredentials();
    }

}
