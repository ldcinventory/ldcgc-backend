package org.ldcgc.backend.payload.dto.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import org.ldcgc.backend.payload.dto.location.LocationDto;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Getter
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class GroupDto {

    private Integer id;
    @Schema(requiredMode = REQUIRED)
    private String name;
    private String description;
    private String urlImage;
    private String phoneNumber;
    @Schema(requiredMode = REQUIRED)
    @Setter private LocationDto location;

}
