package org.ldcgc.backend.controller.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.USER_LEVEL;

@Controller
@RequestMapping("/resources/import")
public interface ImportController {

    // TODO
    //   import for tools POST
    //   |-> (/resources/import --> accepts file)
    //   import for consumables POST
    //   |-> (/resources/import --> accepts file)

    /* TODO example
    @PostMapping(value = "/resources/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> importTools(@RequestPart MultipartFile document);
     */

    @GetMapping("/user")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> testAccessWithAdminCredentials();

}
