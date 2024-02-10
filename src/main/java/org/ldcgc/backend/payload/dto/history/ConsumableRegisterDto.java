package org.ldcgc.backend.payload.dto.history;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsumableRegisterDto {

    @NotNull private Integer id;
    @NotNull private String consumableBardcode;
    @NotNull private String volunteerBAId;
    @NotNull private LocalDateTime registrationIn;
    private LocalDateTime registrationOut;
    @NotNull private Float stockAmountIn;
    private Float stockAmountOut;
    private ConsumableDto consumable;
    private VolunteerDto volunteer;

}
