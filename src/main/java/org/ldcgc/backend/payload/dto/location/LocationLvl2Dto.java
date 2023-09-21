package org.ldcgc.backend.payload.dto.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationLvl2Dto implements Serializable {

    Integer id;
    String name;
    String description;
    LocationLvl1Dto locationLvl1;

}
