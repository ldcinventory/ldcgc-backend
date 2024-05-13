package org.ldcgc.backend.db.repository.location;

import org.ldcgc.backend.db.model.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    Optional<Location> getLocationByName(String name);

    List<Location> findAllByGroupId(Integer groupId);

    @Query("SELECT l FROM Location l ORDER BY random() LIMIT 1")
    Location getRandomLocation();

    boolean existsByName(String name);

}
