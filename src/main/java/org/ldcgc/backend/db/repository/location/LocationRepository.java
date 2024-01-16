package org.ldcgc.backend.db.repository.location;

import org.ldcgc.backend.db.model.category.Category;
import org.ldcgc.backend.db.model.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    List<Location> findAllByLevel(Integer level);

    Optional<Location> getLocationByName(String name);

}
