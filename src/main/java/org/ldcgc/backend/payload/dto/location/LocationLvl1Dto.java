package org.ldcgc.backend.payload.dto.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationLvl1Dto implements Serializable {

    Integer id;
    String name;
    String description;
    LocationDto location;
    List<LocationLvl2Dto> lvl2;

}