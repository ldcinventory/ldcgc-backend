package org.ldcgc.backend.controller.resources;

import jakarta.websocket.server.PathParam;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/resources/tools")
public interface ToolController {

    // TODO
    //  Read all tools (paginated/filtered) GET
    //   |-> (/resources/tools?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //  Set barcode for tool PATCH
    //   |-> (/resources/tools/{toolId})

    @GetMapping("/{toolId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> getTool(@PathVariable Integer toolId);

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> createTool(@RequestBody ToolDto tool);

    @PutMapping("/{toolId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> updateTool(@PathVariable Integer toolId, @RequestBody ToolDto toolDto);

    @DeleteMapping("/{toolId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> deleteTool(@PathVariable Integer toolId);

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> getAllTools(@RequestParam Integer pageIndex, @RequestParam Integer size, @RequestParam String filterString);

}
