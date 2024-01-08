package org.ldcgc.backend.payload.dto.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.ldcgc.backend.payload.dto.location.LocationDto;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDto {

    Integer id;
    String name;
    String description;
    String urlImage;
    String phoneNumber;
    LocationDto location;

}
