package org.ldcgc.backend.controller.test;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AliveController {

    @Profile("dev")
    @GetMapping("/alive")
    public ResponseEntity<?> getAlive() {
        return ResponseEntity.status(200).body("Everything OK!");
    }

}
