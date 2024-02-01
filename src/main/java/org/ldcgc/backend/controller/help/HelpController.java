package org.ldcgc.backend.controller.help;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.USER_LEVEL;

public interface HelpController {

    // TODO
    //  -- USER role --
    //  List help/FAQ articles GET
    //   |-> (/help)
    //  Get any help/FAQ articles GET
    //   |-> (/help/{articleId})
    //  -- MANAGER/ADMIN role --
    //  Create help/FAQ articles POST
    //   |-> (/help)
    //  Update help/FAQ article PUT
    //   |-> (/help/{articleId})
    //  Delete help/FAQ article DELETE
    //   |-> (/help/{articleId})

    @GetMapping("/user")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> testAccessWithAdminCredentials();

}
