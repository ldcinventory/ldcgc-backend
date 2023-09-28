package org.ldcgc.backend.payload.dto.location;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.payload.dto.category.CategoryDto;

import java.io.Serializable;
import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationDto implements Serializable {

    Integer id;
    String name;
    String description;
    String url;
    List<LocationLvl1Dto> lvl1;

    @JsonCreator
    public LocationDto(@JsonProperty("id") Integer id,
                          @JsonProperty("name") String name,
                          @JsonProperty("description") String description,
                          @JsonProperty("url") String url,
                          @JsonProperty("lvl1") List<LocationLvl1Dto> lvl1) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.lvl1 = lvl1;
    }
}
