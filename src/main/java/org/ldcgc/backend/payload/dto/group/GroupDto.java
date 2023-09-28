package org.ldcgc.backend.payload.dto.group;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.payload.dto.category.SubCategoryDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.location.LocationLvl2Dto;

import java.io.Serializable;
import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDto implements Serializable {

    Integer id;
    String name;
    String description;
    String urlImage;
    String phoneNumber;
    LocationDto location;

    @JsonCreator
    public GroupDto(@JsonProperty("id") Integer id,
                       @JsonProperty("name") String name,
                       @JsonProperty("description") String description,
                       @JsonProperty("urlImage") String  urlImage,
                       @JsonProperty("phoneNumber") String phoneNumber,
                       @JsonProperty("location") LocationDto location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.urlImage = urlImage;
        this.phoneNumber = phoneNumber;
        this.location = location;
    }

}
