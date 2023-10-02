package org.ldcgc.backend.controller.resources.tool;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> importTools(@RequestPart MultipartFile document);
     */

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> testAccessWithAdminCredentials();

}
