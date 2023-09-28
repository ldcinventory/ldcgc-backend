package org.ldcgc.backend.payload.dto.location;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationLvl1Dto implements Serializable {

    Integer id;
    String name;
    String description;
    LocationDto location;
    List<LocationLvl2Dto> lvl2;

}
