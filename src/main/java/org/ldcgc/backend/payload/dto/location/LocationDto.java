package org.ldcgc.backend.payload.dto.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.ldcgc.backend.util.common.ELocationType;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Getter
@Builder
@JsonInclude(NON_NULL)
public class LocationDto {

    private Integer id;
    @Schema(requiredMode = REQUIRED)
    private String name;
    private String description;
    private String url;
    private ELocationType locationType;
    @Schema(requiredMode = REQUIRED)
    private Integer level;
    private Integer parentLocationId;
    @JsonInclude(NON_EMPTY) @Setter
    private List<LocationDto> locations;
    @Schema(requiredMode = REQUIRED)
    private Integer groupId;
    @Schema(requiredMode = REQUIRED) @Setter
    private Boolean storesResources;

}
