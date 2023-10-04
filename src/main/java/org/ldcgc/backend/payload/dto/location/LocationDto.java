package org.ldcgc.backend.payload.dto.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Value
@Builder
@JsonInclude(NON_NULL)
public class LocationDto implements Serializable {

    Integer id;
    String name;
    String description;
    String url;
    @JsonInclude(NON_NULL)
    LocationDto parent;
    @JsonInclude(NON_EMPTY)
    List<LocationDto> locations;

}
