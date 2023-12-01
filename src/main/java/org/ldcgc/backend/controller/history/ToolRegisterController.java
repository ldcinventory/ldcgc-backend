package org.ldcgc.backend.controller.history;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.USER_LEVEL;

@Controller
@RequestMapping("/resources/tools/registers")
public interface ToolRegisterController {

    // TODO
    //  Create tool register POST
    //   |-> (/resources/tools/registers)
    //  Read all tools' registers (paginated/filtered) GET
    //   |-> (/resources/tools/registers?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //  Read specific tool register GET
    //   |-> (/resources/tools/registers/{registerId})
    //  Update tool register details PUT
    //   |-> (/resources/tools/registers/{registerId})
    //  Delete tool register DELETE
    //   |-> (/resources/tools/registers/{registerId})

    @GetMapping("/user")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> testAccessWithAdminCredentials();

}
