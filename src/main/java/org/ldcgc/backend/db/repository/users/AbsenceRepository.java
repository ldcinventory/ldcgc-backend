package org.ldcgc.backend.db.repository.users;

import org.ldcgc.backend.db.model.users.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface AbsenceRepository extends
    JpaRepository<Absence, Integer>,
    CrudRepository<Absence, Integer>,
    JpaSpecificationExecutor<Absence> {

}
