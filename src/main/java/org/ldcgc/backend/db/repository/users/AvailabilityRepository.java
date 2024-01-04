package org.ldcgc.backend.db.repository.users;

import org.ldcgc.backend.db.model.users.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {

    Optional<Availability> findByVolunteer_BuilderAssistantId(String builderAssistantId);

}
