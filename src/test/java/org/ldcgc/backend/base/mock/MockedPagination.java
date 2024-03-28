package org.ldcgc.backend.base.mock;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class MockedPagination {

    public static <T> Page<T> mockPagination(List<T> pagedElements) {
        return mockPagination(0, 25, null, pagedElements);
    }

    public static <T> Page<T> mockPagination(Integer pageIndex, Integer size, String sortField, List<T> pagedElements) {
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(ObjectUtils.defaultIfNull(sortField, "id")).ascending());
        return new PageImpl<>(pagedElements, pageable, pagedElements.size());
    }
}
