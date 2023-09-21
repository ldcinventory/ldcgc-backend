package org.ldcgc.backend.db.repository.resources;

import org.ldcgc.backend.db.model.resources.Consumable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumableRepository extends JpaRepository<Consumable, Integer> {

}
