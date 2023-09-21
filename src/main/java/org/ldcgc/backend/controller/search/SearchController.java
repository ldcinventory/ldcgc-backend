package org.ldcgc.backend.controller.search;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/search")
public interface SearchController {

    // TODO @L
    //  search users
    //   |-> (/search/users?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //  search volunteers
    //   |-> (/search/volunteers?page={pageIndex}&size={sizeIndex}&filter={filterString}&barcode={barcodeId})
    //  search resources (use resource custom POJO to instantiate fields)
    //   - can search by category
    //   - can search by barcode
    //   - can search by text
    //   - can search by location
    //   - can search by registered or not to a user
    //   |-> (/search/resources/tools)
    //   |-> (/search/resources/consumables)
    //  search barcodes
    //   - can manage an array of barcodes to print them all
    //   - can search by barcode=null to detect resources without barcode
    //   |-> (/search/barcodes?page={pageIndex}&size={sizeIndex}&ids={barcodeId[]})
    //  search maintenance (use resource custom POJO to instantiate fields)
    //   - can search by range of dates
    //   - can search if maintenance date passed
    //   - can search if maintenance is going to passed in X days/weeks/months
    //   - can search by maintenance not done
    //   |-> (/search/maintenance?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //  search resources registered
    //   - can search by range of dates
    //   - can search by user (text)
    //   - can search by barcode (tools/consumables/users)
    //   - can search by category
    //   |-> (/search/register?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //  search locations
    //   |-> (/search/users?page={pageIndex}&size={sizeIndex}&filter={filterString})

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> testAccessWithAdminCredentials();

}
