package org.ldcgc.backend.db.repository.users;

import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.ldcgc.backend.db.model.users.Absence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Integer>, JpaSpecificationExecutor<Absence> {

    @Transactional
    void deleteById(@NotNull Integer id);

    @Query("""
        SELECT a FROM Absence a JOIN a.volunteer v WHERE
            (CAST(:dateFrom as date) IS NULL OR a.dateFrom >= :dateFrom) AND
            (CAST(:dateTo as date) IS NULL OR a.dateTo <= :dateTo) AND
            (:builderAssistantIds IS NULL OR v.builderAssistantId in (:builderAssistantIds))
            """)
    Page<Absence> findAllFiltered(LocalDate dateFrom, LocalDate dateTo, List<String> builderAssistantIds, Pageable pageable);

}
