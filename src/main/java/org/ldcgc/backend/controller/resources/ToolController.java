package org.ldcgc.backend.controller.resources;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;

@Controller
@RequestMapping("/resources/tools")
public interface ToolController {

    // TODO
    //  Read all tools (paginated/filtered) GET
    //   |-> (/resources/tools?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //  Set barcode for tool PATCH
    //   |-> (/resources/tools/{toolId})

    @GetMapping("/{toolId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> getTool(@PathVariable Integer toolId);

    @PostMapping
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> createTool(@RequestBody ToolDto tool);

    @PutMapping("/{toolId}")
    @PreAuthorize("hasAnyRole(ADMIN_LEVEL)")
    ResponseEntity<?> updateTool(@PathVariable Integer toolId, @RequestBody ToolDto toolDto);

    @DeleteMapping("/{toolId}")
    @PreAuthorize("hasAnyRole(ADMIN_LEVEL)")
    ResponseEntity<?> deleteTool(@PathVariable Integer toolId);

    @GetMapping()
    @PreAuthorize("hasAnyRole(ADMIN_LEVEL)")
    ResponseEntity<?> getAllTools(@RequestParam Integer pageIndex, @RequestParam Integer size, @RequestParam String filterString);

    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ToolDto.class)))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Brand not found", value = "Brand Hammer not found. Please, enter a valid Brand.")
                    })
    )
    @PostMapping("/excel")
    @PreAuthorize("hasAnyRole(ADMIN_LEVEL)")
    ResponseEntity<?> uploadToolsExcel(@RequestParam("excel") MultipartFile file);

}
