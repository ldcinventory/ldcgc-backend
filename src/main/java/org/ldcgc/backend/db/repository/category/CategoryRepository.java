package org.ldcgc.backend.db.repository.category;

import org.ldcgc.backend.db.model.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("""
        select c
        from Category c
        where c.parent.id = (select c.id from Category c where c.name = :parentName)
        """)
    List<Category> findAllByParentName(String parentName);


}
