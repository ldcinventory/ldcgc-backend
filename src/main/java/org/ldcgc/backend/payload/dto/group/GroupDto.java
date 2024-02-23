package org.ldcgc.backend.payload.dto.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.ldcgc.backend.payload.dto.location.LocationDto;

@Getter
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDto {

    private Integer id;
    private String name;
    private String description;
    private String urlImage;
    private String phoneNumber;
    private LocationDto location;

}
