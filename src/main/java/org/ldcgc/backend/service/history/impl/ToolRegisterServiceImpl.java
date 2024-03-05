package org.ldcgc.backend.service.history.impl;

import lombok.RequiredArgsConstructor;
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
import org.ldcgc.backend.util.common.EStatus;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

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

        ToolRegister register = repository.save(ToolRegisterMapper.MAPPER.toMo(toolRegisterDto));
        toolService.updateToolStatus(register.getTool(), EStatus.NOT_AVAILABLE);

        return Constructor.buildResponseMessageObject(HttpStatus.OK, Messages.Info.TOOL_REGISTER_CREATED, ToolRegisterMapper.MAPPER.toDto(register));
    }

    public ResponseEntity<?> getAllRegisters(Integer pageIndex, Integer size, String sortString, Boolean descOrder, String status, String volunteer, String tool) {
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(Boolean.TRUE.equals(descOrder) ? Sort.Direction.DESC : Sort.Direction.ASC, sortString));

        Page<ToolRegisterDto> pagedToolRegisters = repository.findAllFiltered(status, volunteer, tool, pageable)
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

        if(!register.getTool().getBarcode().equals(registerDto.getToolBarcode()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOOL_REGISTER_INCORRECT_BARCODE.formatted(registerDto.getToolBarcode()));
        if(!register.getVolunteer().getBuilderAssistantId().equals(registerDto.getVolunteerBuilderAssistantId()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOOL_REGISTER_INCORRECT_BUILDER_ASSISTANT_ID.formatted(registerDto.getVolunteerBuilderAssistantId()));

        ToolRegisterMapper.MAPPER.update(registerDto, register);

        if(Objects.nonNull(register.getRegisterFrom()))
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
}
