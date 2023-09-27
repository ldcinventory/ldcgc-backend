package org.ldcgc.backend.controller.group;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/groups")
public interface GroupController {

    // TODO
    //  Read own group information GET
    //   |-> (/groups/my)
    //  -- ADMIN role --
    //  Create group POST
    //   |-> (/groups)
    //  Read all groups information GET
    //   |-> (/groups)
    //  Read specific group information GET
    //   |-> (/groups/{groupId})
    //  Update group information PUT
    //   |-> (/groups/{groupId})
    //  Delete group DELETE
    //   |-> (/groups/{groupId})

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> testAccessWithAdminCredentials();

}
