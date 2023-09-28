package org.ldcgc.backend.payload.dto.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.ldcgc.backend.payload.dto.location.LocationDto;

import java.io.Serializable;

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
