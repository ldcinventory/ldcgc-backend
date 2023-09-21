package org.ldcgc.backend.controller.history;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> testAccessWithAdminCredentials();

}
