package org.ldcgc.backend.db.repository.location;

import org.ldcgc.backend.db.model.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer> {

}
