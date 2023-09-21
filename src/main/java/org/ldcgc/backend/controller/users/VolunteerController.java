package org.ldcgc.backend.controller.users;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/volunteers")
@Tag(name = "Volunteers", description = "Volunteers methods with CRUD functions")
public interface VolunteerController {

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> createVolunteer();

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> listVolunteers(
            @RequestParam(required = false) String pageIndex,
            @RequestParam(required = false) String sizeIndex,
            @RequestParam(required = false) String filterString,
            @RequestParam(required = false) String barcode);

    @GetMapping("/{volunteerId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> getVolunteer(
            @PathVariable String volunteerId);

    @PutMapping("/{volunteerId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> updateVolunteer(
            @PathVariable String volunteerId);

    @DeleteMapping("/{volunteerId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> deleteVolunteer(
            @PathVariable String volunteerId);

}
