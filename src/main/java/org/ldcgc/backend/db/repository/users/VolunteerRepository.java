package org.ldcgc.backend.db.repository.users;

import org.ldcgc.backend.db.model.users.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerRepository extends JpaRepository<Volunteer, Integer> {

}
