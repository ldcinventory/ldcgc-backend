package org.ldcgc.backend.payload.dto.other;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NonPaged {

    private List<?> elements;
    private Map<?, ?> groupedElements;

    public static NonPaged of(List<?> elements) {
        return NonPaged.builder().elements(elements).build();
    }

}
