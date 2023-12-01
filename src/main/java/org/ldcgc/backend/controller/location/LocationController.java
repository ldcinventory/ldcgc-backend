package org.ldcgc.backend.controller.location;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.USER_LEVEL;

@Controller
@RequestMapping("/locations")
public interface LocationController {

    // TODO
    //  Create location POST --> location + lvl1 + lvl2
    //   |-> (/locations)
    //   |-> (/locations/lvl1)
    //   |-> (/locations/lvl2)
    //  Read all locations (paginated/filtered) GET --> location + lvl1 + lvl2
    //   |-> (/locations?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //   |-> (/locations/lvl1?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //   |-> (/locations/lvl2?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //  Read specific location GET --> location + lvl1 + lvl2
    //   |-> (/locations/{locationId})
    //   |-> (/locations/lvl1/{locationId})
    //   |-> (/locations/lvl2/{locationId})
    //  Update location details PUT --> location + lvl1 + lvl2
    //   |-> (/locations/{locationId})
    //   |-> (/locations/lvl1/{locationId})
    //   |-> (/locations/lvl2/{locationId})
    //  Delete location DELETE --> location (cascade) + lvl1 (cascade) + lvl2
    //   |-> (/locations/{locationId})
    //   |-> (/locations/lvl1/{locationId})
    //   |-> (/locations/lvl2/{locationId})

    @GetMapping("/user")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> testAccessWithAdminCredentials();

}
