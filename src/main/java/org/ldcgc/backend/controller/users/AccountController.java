package org.ldcgc.backend.controller.users;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/accounts")
public interface AccountController {

    // TODO
    //  -- ANY role
    //  Login POST
    //   |-> (/account/login)
    //  Logout POST
    //   |-> (/account/logout)
    //  Reset password or Recover account (series of endpoints)
    //   # Send credentials POST (send email with token in url)
    //   |-> (/account/recover)
    //   # Validate temp token GET
    //   |-> (/account/validate?recover-token={tokenId})
    //   # Set new credentials POST (send email + new pass + token in payload)
    //   |-> (/account/new-credentials)

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> testAccessWithAdminCredentials();

}
