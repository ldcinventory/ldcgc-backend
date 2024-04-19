package org.ldcgc.backend.db.repository.category;

import org.ldcgc.backend.db.model.category.Responsibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResponsibilityRepository extends JpaRepository<Responsibility, Integer> {

    Optional<Responsibility> findByName(String name);
}
