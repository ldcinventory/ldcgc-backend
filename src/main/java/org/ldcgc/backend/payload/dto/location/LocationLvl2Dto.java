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
public class LocationLvl2Dto implements Serializable {

    Integer id;
    String name;
    String description;
    LocationLvl1Dto locationLvl1;

    @JsonCreator
    public LocationLvl2Dto(@JsonProperty("id") Integer id,
                           @JsonProperty("name") String name,
                           @JsonProperty("description") String description,
                           @JsonProperty("locationLvl1") LocationLvl1Dto locationLvl1) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.locationLvl1 = locationLvl1;
    }
}
