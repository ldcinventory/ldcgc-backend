package org.ldcgc.backend.controller.users;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/availabilities")
public interface AvailabilityController {

    // TODO
    //  -- ADMIN --
    //  Read all volunteers availabilities GET
    //   |-> (/availabilities?page={pageIndex}&size={sizeIndex}&filter={filterString}&barcode={barcodeId})
    //  Read specific volunteer availability GET
    //   |-> (/availabilities/{availabilityId})
    //  Update specific volunteer availability PUT
    //   |-> (/availabilities/{availabilityId})

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> testAccessWithAdminCredentials();

}
