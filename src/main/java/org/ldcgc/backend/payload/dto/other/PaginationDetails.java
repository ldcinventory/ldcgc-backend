package org.ldcgc.backend.payload.dto.other;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationDetails {

    private List<?> elements;
    private Map<?, ?> groupedElements;
    private int numElements;
    private int elementsPerPage;
    private int elementsThisPage;
    private int actualPage;
    private int actualPageFrom;
    private int actualPageTo;
    private int totalPages;

    public static PaginationDetails fromPaging(Pageable pageable, Page<?> page) {
        return genericPaginationDetails(pageable, page).toBuilder()
            .elements(page.getContent())
            .build();
    }

    public static PaginationDetails fromPagingGrouped(Pageable pageable, Page<?> page, Map<?, ?> groupedElements) {
        return genericPaginationDetails(pageable, page).toBuilder()
            .groupedElements(groupedElements)
            .build();
    }

    private static PaginationDetails genericPaginationDetails(Pageable pageable, Page<?> page) {
        PaginationDetails paginationDetails = PaginationDetails.builder()
            .numElements((int) page.getTotalElements())
            .elementsPerPage(pageable.getPageSize())
            .actualPage(pageable.getPageNumber())
            .actualPageFrom(pageable.getPageNumber() * pageable.getPageSize() + 1)
            .actualPageTo(Math.min(((pageable.getPageNumber() + 1) * pageable.getPageSize()), (int) page.getTotalElements()))
            .totalPages(page.getTotalPages())
            .build();

        return paginationDetails.toBuilder()
            .elementsThisPage(paginationDetails.getActualPageTo() - paginationDetails.getActualPageFrom() + 1)
            .build();
    }

}
