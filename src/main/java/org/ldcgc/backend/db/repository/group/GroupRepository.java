package org.ldcgc.backend.db.repository.group;

import org.ldcgc.backend.db.model.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Integer> {
}
