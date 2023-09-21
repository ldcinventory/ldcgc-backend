package org.ldcgc.backend.controller.history;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/resources/consumables/registers")
public interface ConsumableRegisterController {

    // TODO
    //  Create consumable register POST
    //   |-> (/resources/consumables/registers)
    //  Read all consumables' registers (paginated/filtered) GET
    //   |-> (/resources/consumables/registers?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //  Read specific consumable register GET
    //   |-> (/resources/consumables/registers/{registerId})
    //  Update consumable register details PUT
    //   |-> (/resources/consumables/registers/{registerId})
    //  Delete consumable register DELETE
    //   |-> (/resources/consumables/registers/{registerId})

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> testAccessWithAdminCredentials();

}
