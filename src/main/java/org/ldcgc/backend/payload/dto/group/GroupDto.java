package org.ldcgc.backend.payload.dto.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import org.ldcgc.backend.payload.dto.location.LocationDto;

@Getter
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class GroupDto {

    private Integer id;
    private String name;
    private String description;
    private String urlImage;
    private String phoneNumber;
    @Setter
    private LocationDto location;

}
