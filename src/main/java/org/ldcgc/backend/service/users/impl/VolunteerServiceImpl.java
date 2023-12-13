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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.VOLUNTEER_ALREADY_EXIST;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.VOLUNTEER_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.VOLUNTEER_TOKEN_NOT_EXIST;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.USER_LISTED;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.VOLUNTEER_CREATED;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.VOLUNTEER_DELETED;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.VOLUNTEER_UPDATED;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;
import static org.ldcgc.backend.util.retrieving.Message.getInfoMessage;

@Component
@RequiredArgsConstructor
@Transactional
public class VolunteerServiceImpl implements VolunteerService {

    private final JwtUtils jwtUtils;
    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;

    public ResponseEntity<?> createVolunteer(VolunteerDto volunteer) {

        if(volunteerRepository.findByBuilderAssistantId(volunteer.getBuilderAssistantId()).isPresent())
            throw new RequestException(HttpStatus.CONFLICT, String.format(getErrorMessage(VOLUNTEER_ALREADY_EXIST), volunteer.getBuilderAssistantId()));

        Volunteer volunteerEntity = VolunteerMapper.MAPPER.toEntity(volunteer);

        if(volunteer.getAvailability() != null)
            volunteerEntity.getAvailability().setVolunteer(volunteerEntity);

        volunteerEntity = volunteerRepository.save(volunteerEntity);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, getInfoMessage(VOLUNTEER_CREATED), VolunteerMapper.MAPPER.toDTO(volunteerEntity));
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
            String.format(getInfoMessage(USER_LISTED), pageUsers.getTotalElements()),
            userList.stream().map(VolunteerMapper.MAPPER::toDTO).toList());
    }

    public ResponseEntity<?> getMyVolunteer(String token) throws ParseException {

        Integer userId = jwtUtils.getUserIdFromStringToken(token);
        Volunteer volunteer = userRepository.findById(userId).map(User::getVolunteer).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(VOLUNTEER_TOKEN_NOT_EXIST)));

        return Constructor.buildResponseObject(HttpStatus.OK, VolunteerMapper.MAPPER.toDTO(volunteer));
    }

    public ResponseEntity<?> getVolunteer(String volunteerId) {

        Volunteer volunteerEntity = volunteerRepository.findByBuilderAssistantId(volunteerId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(VOLUNTEER_NOT_FOUND)));

        return Constructor.buildResponseObject(HttpStatus.OK, VolunteerMapper.MAPPER.toDTO(volunteerEntity));
    }

    public ResponseEntity<?> updateVolunteer(String volunteerId, VolunteerDto volunteer) {

        Volunteer volunteerEntity = volunteerRepository.findByBuilderAssistantId(volunteerId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(VOLUNTEER_NOT_FOUND)));

        VolunteerMapper.MAPPER.update(volunteerEntity, volunteer);

        if(volunteer.getAvailability() != null)
            volunteerEntity.getAvailability().setVolunteer(volunteerEntity);

        volunteerRepository.save(volunteerEntity);

        return Constructor.buildResponseMessageObject(HttpStatus.OK, getInfoMessage(VOLUNTEER_UPDATED), VolunteerMapper.MAPPER.toDTO(volunteerEntity));
    }

    public ResponseEntity<?> deleteVolunteer(String volunteerId) {
        Volunteer volunteer = volunteerRepository.findByBuilderAssistantId(volunteerId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(VOLUNTEER_NOT_FOUND)));

        volunteerRepository.delete(volunteer);

        return Constructor.buildResponseMessage(HttpStatus.OK, getInfoMessage(VOLUNTEER_DELETED));
    }

}
