package org.ldcgc.backend.service.history.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.model.history.ToolRegister;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.history.ToolRegisterRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.history.ToolRegisterDto;
import org.ldcgc.backend.payload.dto.other.PaginationDetails;
import org.ldcgc.backend.payload.mapper.history.tool.ToolRegisterMapper;
import org.ldcgc.backend.service.history.ToolRegisterService;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.ldcgc.backend.util.common.ERegisterStatus;
import org.ldcgc.backend.util.common.EStatus;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ToolRegisterServiceImpl implements ToolRegisterService {

    private final ToolRegisterRepository repository;
    private final VolunteerRepository volunteerRepository;
    private final ToolRepository toolRepository;
    private final ToolService toolService;

    public ResponseEntity<?> createToolRegister(ToolRegisterDto toolRegisterDto) {
        String builderAssistantId = toolRegisterDto.getVolunteerBuilderAssistantId();
        List<Volunteer> volunteers = volunteerRepository.findAllByBuilderAssistantId(builderAssistantId);
        if (volunteers.isEmpty())
            throw new RequestException(HttpStatus.NOT_FOUND, Messages.Error.TOOL_REGISTER_VOLUNTEER_NOT_FOUND);
        else if (volunteers.size() > 1)
            throw new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.TOOL_REGISTER_TOO_MANY_VOLUNTEERS, builderAssistantId));

        Tool tool = toolRepository.findFirstByBarcode(toolRegisterDto.getToolBarcode())
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.TOOL_REGISTER_TOOL_NOT_FOUND));

        if (!tool.getStatus().equals(EStatus.AVAILABLE))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOOL_REGISTER_TOOL_NOT_AVAILABLE);

        ToolRegister register = repository.saveAndFlush(ToolRegisterMapper.MAPPER.toMo(toolRegisterDto));
        toolService.updateToolStatus(register.getTool(), EStatus.NOT_AVAILABLE);

        return Constructor.buildResponseMessageObject(HttpStatus.OK, Messages.Info.TOOL_REGISTER_CREATED, ToolRegisterMapper.MAPPER.toDto(register));
    }

    public ResponseEntity<?> getAllRegisters(Integer pageIndex, Integer size, String sortString, Boolean descOrder, ERegisterStatus status, String volunteer, String tool) {
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(Boolean.TRUE.equals(descOrder) ? Sort.Direction.DESC : Sort.Direction.ASC, sortString));

        Page<ToolRegisterDto> pagedToolRegisters = repository.findAllFiltered(Optional.ofNullable(status).map(ERegisterStatus::getName).orElse(null), volunteer, tool, pageable)
                .map(ToolRegisterMapper.MAPPER::toDto);

        if (pageIndex > pagedToolRegisters.getTotalPages())
            throw new RequestException(HttpStatus.BAD_REQUEST, org.ldcgc.backend.util.constants.Messages.Error.PAGE_INDEX_REQUESTED_EXCEEDED_TOTAL);

        return Constructor.buildResponseMessageObject(
                HttpStatus.OK,
                Messages.Info.TOOL_REGISTER_LISTED.formatted(pagedToolRegisters.getTotalElements()),
                PaginationDetails.fromPaging(pageable, pagedToolRegisters));
    }

    public ResponseEntity<?> updateRegister(Integer registerId, ToolRegisterDto registerDto) {
        ToolRegister register = repository.findById(registerId)
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.TOOL_REGISTER_NOT_FOUND.formatted(registerId)));

        if (!register.getTool().getBarcode().equals(registerDto.getToolBarcode()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOOL_REGISTER_INCORRECT_BARCODE.formatted(registerDto.getToolBarcode()));
        if (!register.getVolunteer().getBuilderAssistantId().equals(registerDto.getVolunteerBuilderAssistantId()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOOL_REGISTER_INCORRECT_BUILDER_ASSISTANT_ID.formatted(registerDto.getVolunteerBuilderAssistantId()));

        ToolRegisterMapper.MAPPER.update(registerDto, register);
        register = repository.saveAndFlush(register);

        if (Objects.nonNull(register.getRegisterFrom()))
            toolService.updateToolStatus(register.getTool(), EStatus.AVAILABLE);

        return Constructor.buildResponseMessageObject(
                HttpStatus.OK,
                Messages.Info.TOOL_REGISTER_UPDATED,
                registerDto
        );
    }

    public ResponseEntity<?> getRegister(Integer registerId) {
        ToolRegister register = repository.findById(registerId)
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.TOOL_REGISTER_NOT_FOUND.formatted(registerId)));

        return Constructor.buildResponseMessageObject(
                HttpStatus.OK,
                Messages.Info.TOOL_REGISTER_FOUND,
                register
        );
    }

    public ResponseEntity<?> deleteRegister(Integer registerId) {
        ToolRegister register = repository.findById(registerId)
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.TOOL_REGISTER_NOT_FOUND.formatted(registerId)));

        repository.delete(register);
        toolService.updateToolStatus(register.getTool(), EStatus.AVAILABLE);

        return Constructor.buildResponseMessage(
                HttpStatus.OK,
                Messages.Info.TOOL_REGISTER_DELETED
        );
    }

    public ResponseEntity<?> createToolRegisters(List<ToolRegisterDto> toolRegistersDto) {
        List<String> barcodes = toolRegistersDto.stream()
                .map(ToolRegisterDto::getToolBarcode)
                .distinct()
                .toList();
        if (barcodes.size() != toolRegistersDto.size())
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOOL_REGISTER_REPEATED_TOOLS);

        List<Tool> tools = toolRepository.findAllByBarcodeIn(barcodes);
        if (tools.size() != barcodes.size())
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOOL_NOT_FOUND_BARCODE
                    .formatted(barcodes.stream().filter(b -> tools.stream().noneMatch(t -> t.getBarcode().equals(b))).findFirst().orElse(StringUtils.EMPTY)));

        if (tools.stream().anyMatch(tool -> !tool.getStatus().equals(EStatus.AVAILABLE)))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOOL_REGISTER_TOOL_NOT_AVAILABLE);

        List<String> builderAssistantIds = toolRegistersDto.stream()
                .map(ToolRegisterDto::getVolunteerBuilderAssistantId)
                .distinct()
                .toList();
        List<Volunteer> volunteers = volunteerRepository.findAllByBuilderAssistantIdIn(builderAssistantIds);
        if (builderAssistantIds.size() != volunteers.size())
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.VOLUNTEER_NOT_FOUND_BA_ID
                    .formatted(builderAssistantIds.stream().filter(b -> volunteers.stream().noneMatch(t -> t.getBuilderAssistantId().equals(b))).findFirst().orElse(StringUtils.EMPTY)));

        List<ToolRegister> registers = toolRegistersDto.stream()
                .map(ToolRegisterMapper.MAPPER::toMo)
                .map(toolRegister -> addTool(toolRegister, tools))
                .map(toolRegister -> addVolunteer(toolRegister, volunteers))
                .toList();

        repository.saveAllAndFlush(registers);
        tools.forEach(tool -> tool.setStatus(EStatus.NOT_AVAILABLE));
        toolRepository.saveAllAndFlush(tools);

        return Constructor.buildResponseObject(HttpStatus.OK, registers);
    }

    private static ToolRegister addTool(ToolRegister toolRegister, List<Tool> tools) {
        return toolRegister.toBuilder()
                .tool(tools.stream()
                        .filter(tool -> tool.getBarcode().equals(toolRegister.getTool().getBarcode()))
                        .findFirst()
                        .orElseThrow(() -> new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOOL_NOT_FOUND_BARCODE
                                .formatted(toolRegister.getTool().getBarcode()))
                        ))
                .build();
    }
    private static ToolRegister addVolunteer(ToolRegister toolRegister, List<Volunteer> volunteers) {
        return toolRegister.toBuilder()
                .volunteer(volunteers.stream()
                        .filter(volunteer -> volunteer.getBuilderAssistantId().equals(toolRegister.getVolunteer().getBuilderAssistantId()))
                        .findFirst()
                        .orElseThrow(() -> new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.VOLUNTEER_BAID_NOT_FOUND
                                .formatted(toolRegister.getVolunteer().getBuilderAssistantId()))
                        ))
                .build();
    }
}
