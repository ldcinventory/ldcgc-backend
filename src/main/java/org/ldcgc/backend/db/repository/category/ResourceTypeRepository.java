package org.ldcgc.backend.db.repository.category;

import org.ldcgc.backend.db.model.category.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResourceTypeRepository extends JpaRepository<ResourceType, Integer> {

    Boolean existsByName(String name);

    Optional<ResourceType> findByName(String name);

    @Query(value = """
            SELECT rt.* FROM "resource-types" rt
            WHERE
              (
                COALESCE(:name, '') = '' OR
                unaccent(rt.name) ILIKE unaccent(CONCAT('%', :name, '%'))
              )
              AND (
                CASE
                  WHEN :locked IS NOT NULL THEN rt.locked = :locked
                  ELSE TRUE
                END
              )
            """, nativeQuery = true)
    List<ResourceType> findAllFiltered(String name, Boolean locked);
}
