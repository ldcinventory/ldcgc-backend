package org.ldcgc.backend.db.repository.category;

import org.ldcgc.backend.db.model.category.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {

    boolean existsByName(String name);

    Optional<Brand> findByName(String name);

    @Query(value = """
            SELECT b.* FROM brands b
            WHERE
              (
                COALESCE(:name, '') = '' OR
                unaccent(b.name) ILIKE unaccent(CONCAT('%', :name, '%'))
              )
              AND (
                CASE
                  WHEN :locked IS NOT NULL THEN b.locked = :locked
                  ELSE TRUE
                END
              )
            """, nativeQuery = true)
    List<Brand> findAllFiltered(String name, Boolean locked);

}
