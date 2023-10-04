package org.ldcgc.backend.controller.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/resources/consumables")
public interface ConsumableController {

    // TODO
    //  Create consumable POST
    //   |-> (/resources/consumables)
    //  Read all consumables (paginated/filtered) GET
    //   |-> (/resources/consumables?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //  Read specific consumable GET
    //   |-> (/resources/consumables/{consumableId})
    //  Set barcode for consumable PATCH
    //   |-> (/resources/consumables/{consumableId})
    //  Update consumable details PUT
    //   |-> (/resources/consumables/{consumableId})
    //  Delete consumable DELETE
    //   |-> (/resources/consumables/{consumableId})

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> testAccessWithAdminCredentials();

}
