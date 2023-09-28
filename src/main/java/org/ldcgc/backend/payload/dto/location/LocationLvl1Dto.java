package org.ldcgc.backend.payload.dto.location;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonCreator
    public LocationLvl1Dto(@JsonProperty("id") Integer id,
                       @JsonProperty("name") String name,
                       @JsonProperty("description") String description,
                       @JsonProperty("location") LocationDto location,
                       @JsonProperty("lvl1") List<LocationLvl2Dto> lvl2) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.lvl2 = lvl2;
    }
}
