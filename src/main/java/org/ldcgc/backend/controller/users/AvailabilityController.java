package org.ldcgc.backend.controller.users;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.USER_LEVEL;

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
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> testAccessWithAdminCredentials();

}
