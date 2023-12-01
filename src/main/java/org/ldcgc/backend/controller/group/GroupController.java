package org.ldcgc.backend.controller.group;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.USER_LEVEL;

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
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> testAccessWithAdminCredentials();

}
