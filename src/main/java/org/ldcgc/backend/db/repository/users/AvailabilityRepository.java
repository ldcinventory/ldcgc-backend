package org.ldcgc.backend.db.repository.users;

import org.ldcgc.backend.db.model.users.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {

}
