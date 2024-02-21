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
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.history.tool.ToolRegisterMapper;
import org.ldcgc.backend.service.history.ToolRegisterService;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.ldcgc.backend.service.users.VolunteerService;
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
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ToolRegisterServiceImpl implements ToolRegisterService {

    private final ToolRegisterRepository repository;
    private final VolunteerRepository volunteerRepository;
    private final ToolRepository toolRepository;

    public ResponseEntity<?> createToolRegister(ToolRegisterDto toolRegisterDto) {
        String builderAssistantId = toolRegisterDto.getVolunteer().getBuilderAssistantId();
        List<Volunteer> volunteers = volunteerRepository.findAllByBuilderAssistantId(builderAssistantId);
        if (volunteers.isEmpty())
            throw new RequestException(HttpStatus.NOT_FOUND, Messages.Error.TOOL_REGISTER_VOLUNTEER_NOT_FOUND);
        else if (volunteers.size() > 1)
            throw new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.TOOL_REGISTER_TOO_MANY_VOLUNTEERS, builderAssistantId));
        else if (!Objects.equals(volunteers.getFirst().getId(), toolRegisterDto.getVolunteer().getId()))
            throw new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.TOOL_REGISTER_INCORRECT_BUILDER_ASSISTANT_ID, builderAssistantId));

        Tool tool = toolRepository.findById(toolRegisterDto.getTool().getId())
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.TOOL_REGISTER_TOOL_NOT_FOUND));

        if (!tool.getStatus().equals(EStatus.AVAILABLE))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOOL_REGISTER_TOOL_NOT_AVAILABLE);

        ToolRegister register = repository.save(ToolRegisterMapper.MAPPER.toMo(toolRegisterDto));
        return Constructor.buildResponseMessageObject(HttpStatus.OK, Messages.Info.TOOL_REGISTER_CREATED, ToolRegisterMapper.MAPPER.toDto(register));
    }

    public ResponseEntity<?> getAllRegisters(Integer pageIndex, Integer size, String sortString, String filterString) {
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(sortString));

        Page<ToolRegister> page = Optional.ofNullable(filterString)
                .filter(fs -> !fs.isEmpty())
                .map(fs -> repository.findAllFiltered(fs, pageable))
                .orElse(repository.findAll(pageable));

        return Constructor.buildResponseMessageObject(
                HttpStatus.OK,
                Messages.Info.TOOL_REGISTER_LISTED.formatted(page.getTotalElements()),
                page);
    }

    public ResponseEntity<?> updateRegister(Integer registerId, ToolRegisterDto registerDto) {
        ToolRegister register = repository.findById(registerId)
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.TOOL_REGISTER_NOT_FOUND.formatted(registerId)));

        ToolRegisterMapper.MAPPER.update(registerDto, register);

        repository.save(register);
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

        return Constructor.buildResponseMessage(
                HttpStatus.OK,
                Messages.Info.TOOL_REGISTER_DELETED
        );
    }
}
