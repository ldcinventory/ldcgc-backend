package org.ldcgc.backend.controller.users;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/absences")
public interface AbsenceController {

    // TODO
    //  Create an absence POST
    //   |-> (/search/absences)
    //  Read all my absences GET
    //   |-> (/search/absences?page={pageIndex}&size={sizeIndex}
    //                     &dateFrom={dateFrom(yyyy-MM-dd)}&dateTo={dateTo(yyyy-MM-dd)})
    //  Read specific by id GET
    //   |-> (/search/absences/{absenceId})
    //  Update an absence PUT
    //   |-> (/search/absences/{absenceId})
    //  Delete an absence DELETE
    //   |-> (/search/absences/{absenceId})

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> testAccessWithAdminCredentials();

}
