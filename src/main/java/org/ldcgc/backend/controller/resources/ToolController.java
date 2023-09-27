package org.ldcgc.backend.controller.resources;

import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/resources/tools")
public interface ToolController {

    // TODO
    //  Create tool POST
    //   |-> (/resources/tools)
    //  Read all tools (paginated/filtered) GET
    //   |-> (/resources/tools?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //  Read specific tool GET
    //   |-> (/resources/tools/{consumableId})
    //  Set barcode for tool PATCH
    //   |-> (/resources/tools/{consumableId})
    //  Update tool details PUT
    //   |-> (/resources/tools/{consumableId})
    //  Delete tool DELETE
    //   |-> (/resources/tools/{consumableId})

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> testAccessWithAdminCredentials();

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> createTool();
}
