package org.ldcgc.backend.controller.users;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.USER_LEVEL;

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
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> testAccessWithAdminCredentials();

}
