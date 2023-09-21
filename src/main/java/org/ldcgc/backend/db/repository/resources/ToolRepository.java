package org.ldcgc.backend.db.repository.resources;

import org.ldcgc.backend.db.model.resources.Tool;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolRepository extends JpaRepository<Tool, Integer> {

}
