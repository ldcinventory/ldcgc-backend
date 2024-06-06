package org.ldcgc.backend.controller.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class AliveController {

    @Profile("dev")
    @GetMapping("/alive")
    public ResponseEntity<?> getAlive() {
        log.info("Service is alive \uD83C\uDF89");
        return ResponseEntity.status(200).body("Everything OK!");
    }

}
