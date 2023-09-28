package org.ldcgc.backend.payload.dto.group;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.ldcgc.backend.payload.dto.category.SubCategoryDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.location.LocationLvl2Dto;

import java.io.Serializable;
import java.util.List;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDto implements Serializable {

    Integer id;
    String name;
    String description;
    String urlImage;
    String phoneNumber;
    LocationDto location;

}
