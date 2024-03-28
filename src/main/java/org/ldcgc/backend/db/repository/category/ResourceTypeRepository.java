package org.ldcgc.backend.db.repository.category;

import org.ldcgc.backend.db.model.category.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceTypeRepository extends JpaRepository<ResourceType, Integer> {

}
