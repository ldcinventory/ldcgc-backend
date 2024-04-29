package org.ldcgc.backend.payload.dto.history;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolRegisterDto {

    private Integer id;
    @NotNull private String toolBarcode;
    private String toolName;
    private String[] toolUrlImages;
    private String volunteerName;
    private String volunteerLastName;
    @NotNull private String volunteerBuilderAssistantId;
    @NotNull private LocalDateTime registerFrom;
    private LocalDateTime registerTo;

}
