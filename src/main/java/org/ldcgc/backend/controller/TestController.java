package org.ldcgc.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public interface TestController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> testAccessWithAdminCredentials();

}
