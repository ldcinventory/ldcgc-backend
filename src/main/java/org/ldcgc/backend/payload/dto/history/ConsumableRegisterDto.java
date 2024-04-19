package org.ldcgc.backend.payload.dto.history;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsumableRegisterDto {

    private Integer id;
    @NotNull private String consumableBarcode;
    @NotNull private String volunteerBAId;
    @NotNull private String volunteerName;
    @NotNull private String volunteerLastName;
    private LocalDateTime registerFrom;
    private LocalDateTime registerTo;
    @NotNull private Float stockAmountRequest;
    private Float stockAmountReturn;
    private String consumableName;
    private String consumableStockType;
    private Boolean closedRegister;
    private boolean processingStockChanges;

}
