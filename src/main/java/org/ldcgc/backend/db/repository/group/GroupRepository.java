package org.ldcgc.backend.db.repository.group;

import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.location.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Integer> {

    Optional<Group> getGroupByName(String name);

    @Query("SELECT g FROM Group g ORDER BY random() LIMIT 1")
    Group getRandomGroup();

}
