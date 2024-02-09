package org.ldcgc.backend.payload.dto.history;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsumableRegisterDto {

    private Integer id;
    private LocalDateTime registrationIn;
    private LocalDateTime registrationOut;
    private Integer stockAmountIn;
    private Integer stockAmountOut;
    private ConsumableDto tool;
    private VolunteerDto volunteer;

}
