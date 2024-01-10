package org.ldcgc.backend.db.repository.users;

import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.ldcgc.backend.db.model.users.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AbsenceRepository extends JpaRepository<Absence, Integer>, JpaSpecificationExecutor<Absence> {

    @Transactional
    void deleteById(@NotNull Integer id);

}
