package org.ldcgc.backend.db.repository.resources;

import org.ldcgc.backend.db.model.resources.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Integer> {

}
