package org.ldcgc.backend.service.category;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    Integer getCategoryIdByName(String categoryName);
}
