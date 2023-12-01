package org.ldcgc.backend.controller.resources;

import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;

@Controller
@RequestMapping("/resources/tools")
public interface ToolController {

    // TODO
    //  Create tool POST
    //   |-> (/resources/tools)
    //  Read all tools (paginated/filtered) GET
    //   |-> (/resources/tools?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //  Read specific tool GET
    //   |-> (/resources/tools/{toolId})
    //  Set barcode for tool PATCH
    //   |-> (/resources/tools/{toolId})
    //  Update tool details PUT
    //   |-> (/resources/tools/{toolId})
    //  Delete tool DELETE
    //   |-> (/resources/tools/{toolId})

    @GetMapping("/{toolId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> getTool(@PathVariable Integer toolId);

    @PostMapping
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> createTool(@RequestBody ToolDto tool);

}
