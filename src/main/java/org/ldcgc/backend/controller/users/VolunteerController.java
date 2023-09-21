package org.ldcgc.backend.controller.users;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/volunteers")
public interface VolunteerController {

    // TODO
    //  -- ADMIN role
    //  Create specific volunteer POST
    //   |-> (/volunteers)
    //  Read all volunteers (can filter by parameters) GET
    //   |-> (/volunteers/?page={pageIndex}&size={sizeIndex}&filter={filterString}&barcode={barcodeId})
    //  Read specific volunteer GET
    //   |-> (/volunteers/{volunteerId})
    //  Update another volunteer UPDATE
    //   |-> (/volunteers/{volunteerId})
    //  Delete another volunteer DELETE
    //   |-> (/volunteers/{volunteerId})

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> testAccessWithAdminCredentials();

}
