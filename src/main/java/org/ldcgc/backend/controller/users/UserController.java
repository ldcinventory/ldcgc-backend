package org.ldcgc.backend.controller.users;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public interface UserController {

    // TODO
    //  Read self account GET
    //   |-> (/users/me)
    //  Update self account PUT
    //   |-> (/users/me)
    //  Delete self account DELETE
    //   |-> (/users/me)
    //  -- ADMIN role
    //  Create account POST
    //   |-> (/users)
    //  Read all accounts (can filter by parameters) GET
    //   |-> (/users/?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //  Read specific account GET
    //   |-> (/users/{userId})
    //  Update another account PUT
    //   |-> (/users/{userId})
    //  Delete another account DELETE
    //   |-> (/users/{userId})

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> testAccessWithAdminCredentials();

}
