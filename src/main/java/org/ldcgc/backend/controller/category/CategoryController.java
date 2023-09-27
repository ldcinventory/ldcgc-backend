package org.ldcgc.backend.controller.category;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categories")
public interface CategoryController {

    // TODO
    //  -- ADMIN role --
    //  --- Categories ---
    //  Create category POST
    //   |-> (/categories)
    //  Read all categories GET
    //   |-> (/categories)
    //  Read specific category information GET
    //   |-> (/categories/{categoryName})
    //  Update category information PUT
    //   |-> (/categories/{categoryName})
    //  Delete category DELETE (cascade)
    //   |-> (/categories/{categoryName})
    //  --- SubCategories ---
    //  Create subcategory POST
    //   |-> (/categories/{categoryName}/subcategories)
    //  Read all subcategories inside a category GET
    //   |-> (/categories/{categoryName}/subcategories)
    //  Read specific subcategory information GET
    //   |-> (/categories/{categoryName}/subcategories/{subCategoryName})
    //  Update subcategory information PUT
    //   |-> (/categories/{categoryName}/subcategories/{subCategoryName})
    //  Delete subcategory DELETE
    //   |-> (/categories/{categoryName}/subcategories/{subCategoryName})

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    ResponseEntity<?> testAccessWithCredentials();

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> testAccessWithAdminCredentials();

}
