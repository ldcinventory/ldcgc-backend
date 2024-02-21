package org.ldcgc.backend.db.repository.history;

import org.ldcgc.backend.db.model.history.ToolRegister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ToolRegisterRepository extends JpaRepository<ToolRegister, Integer> {

    @Query("""
            SELECT r FROM ToolRegister r
            WHERE (LOWER(:filterString) LIKE ('opened') AND r.inRegistration IS NULL)
            OR (LOWER(:filterString) LIKE ('closed') AND r.inRegistration IS NOT NULL)
            """)
    Page<ToolRegister> findAllFiltered(String filterString, Pageable pageable);
}
