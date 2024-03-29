package org.ldcgc.backend.payload.dto.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Builder
@JsonInclude(NON_NULL)
public class LocationDto {

    private Integer id;
    private String name;
    private String description;
    private String url;
    @JsonInclude(NON_NULL)
    private LocationDto parent;
    @JsonInclude(NON_EMPTY)
    private List<LocationDto> locations;
}
