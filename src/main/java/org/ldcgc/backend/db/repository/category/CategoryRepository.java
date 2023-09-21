package org.ldcgc.backend.db.repository.category;

import org.ldcgc.backend.db.model.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
