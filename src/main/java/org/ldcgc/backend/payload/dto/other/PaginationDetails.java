package org.ldcgc.backend.payload.dto.other;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Getter
@Builder(toBuilder = true)
public class PaginationDetails {

    private int numElements;
    private int elementsPerPage;
    private int elementsThisPage;
    private int actualPage;
    private int actualPageFrom;
    private int actualPageTo;
    private int totalPages;

    public static PaginationDetails fromPaging(Pageable pageable, Page<?> page) {
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
