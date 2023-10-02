package org.ldcgc.backend.payload.dto.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationDto implements Serializable {

    Integer id;
    String name;
    String description;
    String url;
    List<LocationLvl1Dto> lvl1;

}
