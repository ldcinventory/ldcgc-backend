package org.ldcgc.backend.service.users.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.payload.mapper.users.VolunteerMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.VolunteerService;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class VolunteerServiceImpl implements VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public ResponseEntity<?> getMyVolunteer(String token) throws ParseException {

        Integer userId = jwtUtils.getUserIdFromStringToken(token);
        Volunteer volunteer = userRepository.findById(userId).map(User::getVolunteer).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_TOKEN_NOT_EXIST));

        return Constructor.buildResponseObject(HttpStatus.OK, VolunteerMapper.MAPPER.toDTO(volunteer));
    }

    public ResponseEntity<?> getVolunteer(String builderAssistantId) {

        Volunteer volunteerEntity = volunteerRepository.findByBuilderAssistantId(builderAssistantId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));

        return Constructor.buildResponseObject(HttpStatus.OK, VolunteerMapper.MAPPER.toDTO(volunteerEntity));
    }

    public ResponseEntity<?> listVolunteers(Integer pageIndex, Integer size, String filterString, String builderAssistantId) {

        if (builderAssistantId != null) return getVolunteer(builderAssistantId);

        Pageable paging = PageRequest.of(pageIndex, size);
        Page<Volunteer> pageUsers = StringUtils.isBlank(filterString) ?
            volunteerRepository.findAll(paging) :
            volunteerRepository.findAllFiltered(filterString, paging);

        List<Volunteer> userList = pageUsers.getContent();

        return Constructor.buildResponseMessageObject(
            HttpStatus.OK,
            String.format(Messages.Info.VOLUNTEER_LISTED, pageUsers.getTotalElements()),
            userList.stream().map(VolunteerMapper.MAPPER::toDTO).toList());

    }

    public ResponseEntity<?> createVolunteer(VolunteerDto volunteerDto) {

        if(volunteerRepository.findByBuilderAssistantId(volunteerDto.getBuilderAssistantId()).isPresent())
            throw new RequestException(HttpStatus.CONFLICT, String.format(Messages.Error.VOLUNTEER_ALREADY_EXIST, volunteerDto.getBuilderAssistantId()));

        Volunteer volunteerEntity = VolunteerMapper.MAPPER.toEntity(volunteerDto);

        if(volunteerDto.getAvailability() != null)
            volunteerEntity.getAvailability().setVolunteer(volunteerEntity);

        volunteerEntity = volunteerRepository.save(volunteerEntity);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.VOLUNTEER_CREATED, VolunteerMapper.MAPPER.toDTO(volunteerEntity));
    }

    public ResponseEntity<?> updateVolunteer(String builderAssistantId, VolunteerDto volunteerDto) {

        Volunteer volunteerEntity = volunteerRepository.findByBuilderAssistantId(builderAssistantId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));

        // check builderAssistantId from payload doesn't exist (new BA for this volunteer)
        // or is the same as in the volunteer (new details for this volunteer)
        if(volunteerDto.getBuilderAssistantId() != null && !volunteerEntity.getId().equals(volunteerDto.getId()))
            throw new RequestException(HttpStatus.CONFLICT, Messages.Error.VOLUNTEER_ID_ALREADY_TAKEN);

        VolunteerMapper.MAPPER.update(volunteerEntity, volunteerDto);

        if(volunteerDto.getAvailability() != null)
            volunteerEntity.getAvailability().setVolunteer(volunteerEntity);

        volunteerEntity = volunteerRepository.save(volunteerEntity);

        return Constructor.buildResponseMessageObject(HttpStatus.OK, Messages.Info.VOLUNTEER_UPDATED, VolunteerMapper.MAPPER.toDTO(volunteerEntity));
    }

    public ResponseEntity<?> deleteVolunteer(String builderAssistantId) {
        Volunteer volunteer = volunteerRepository.findByBuilderAssistantId(builderAssistantId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));

        volunteerRepository.delete(volunteer);

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.VOLUNTEER_DELETED);
    }

}
