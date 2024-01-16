package org.ldcgc.backend.db.repository.category;

import org.ldcgc.backend.db.model.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findAllByParent_Name(String categoryName);

    Optional<Category> findByName(String name);
    @Query("SELECT c.id FROM Category c WHERE c.name = :name")
    Integer findCategoryIdByName(@Param("name") String name);

    Optional<Category> getCategoryByName(String name);
}
