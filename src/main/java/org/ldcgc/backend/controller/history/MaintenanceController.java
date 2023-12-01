package org.ldcgc.backend.controller.history;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.USER_LEVEL;

@Controller
@RequestMapping("/maintenance")
public interface MaintenanceController {

    // TODO
    //  Create maintenance associated to a tool POST
    //   |-> (/maintenance)
    //  Read all maintenances associated to tools (paginated/filtered) GET
    //   |-> (/maintenance?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //  Read specific maintenance associated to a tool GET
    //   |-> (/maintenance/{maintenanceId})
    //  Update maintenance details associated to a tool PUT
    //   |-> (/maintenance/{maintenanceId})
    //  Delete maintenance associated to a tool DELETE
    //   |-> (/maintenance/{maintenanceId})

    @GetMapping("/user")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> testAccessWithAdminCredentials();

}
