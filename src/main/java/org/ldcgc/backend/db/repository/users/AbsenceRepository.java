package org.ldcgc.backend.db.repository.users;

import org.ldcgc.backend.db.model.users.Absence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsenceRepository extends JpaRepository<Absence, Integer> {

}
