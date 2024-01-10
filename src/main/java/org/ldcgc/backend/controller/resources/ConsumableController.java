package org.ldcgc.backend.controller.resources;

import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.USER_LEVEL;

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
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> testAccessWithAdminCredentials();

    @GetMapping("/{consumableId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> getConsumable(@PathVariable Integer consumableId);

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> createConsumable(@RequestBody ConsumableDto consumable);

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> updateConsumable(@RequestBody ConsumableDto consumable);

    @GetMapping
    //@PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    ResponseEntity<?> listConsumables(@RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                      @RequestParam(required = false, defaultValue = "25") Integer sizeIndex,
                                      @RequestParam(required = false) String filterString);

    @DeleteMapping("/{consumableId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> deleteConsumable(@PathVariable Integer consumableId);
    @PostMapping("/uploadExcel")
    ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file);
}
