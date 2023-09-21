package org.ldcgc.backend.db.repository.history;

import org.ldcgc.backend.db.model.history.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer> {

}
