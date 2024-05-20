package org.ldcgc.backend.payload.dto.history;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolRegisterDto {

    private Integer id;
    @Schema(requiredMode = REQUIRED)
    @NotNull private String toolBarcode;
    private String toolName;
    private String[] toolUrlImages;
    private String volunteerName;
    private String volunteerLastName;
    @Schema(requiredMode = REQUIRED)
    @NotNull private String volunteerBuilderAssistantId;
    @Schema(requiredMode = REQUIRED)
    @NotNull private LocalDateTime registerFrom;
    private LocalDateTime registerTo;

}
