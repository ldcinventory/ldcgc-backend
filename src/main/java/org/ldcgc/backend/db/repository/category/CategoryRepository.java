package org.ldcgc.backend.db.repository.category;

import org.ldcgc.backend.db.model.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findAllByParent_Name(String categoryName);


    @Query("SELECT c.id FROM Category c WHERE c.name = :name")
    Integer findCategoryIdByName(@Param("name") String name);
}
