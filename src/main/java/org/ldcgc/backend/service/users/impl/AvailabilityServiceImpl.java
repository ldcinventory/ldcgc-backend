package org.ldcgc.backend.service.users.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.mapper.users.VolunteerMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.AvailabilityService;
import org.ldcgc.backend.util.common.EWeekday;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AvailabilityServiceImpl implements AvailabilityService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final VolunteerRepository volunteerRepository;

    public ResponseEntity<?> getMyAvailability(String token) {
        Volunteer volunteer = getVolunteerFromToken(token);

        return Constructor.buildResponseObject(HttpStatus.OK, VolunteerMapper.MAPPER.toDto(volunteer).getAvailability());

    }

    public ResponseEntity<?> updateMyAvailability(String token, List<EWeekday> availability) {
        Volunteer volunteer = getVolunteerFromToken(token);
        volunteer.setAvailability(new LinkedHashSet<>(availability));
        volunteer = volunteerRepository.saveAndFlush(volunteer);

        return Constructor.buildResponseObject(HttpStatus.CREATED, VolunteerMapper.MAPPER.toDto(volunteer).getAvailability());

    }

    public ResponseEntity<?> clearMyAvailability(String token) {
        Volunteer volunteer = getVolunteerFromToken(token);
        volunteer.setAvailability(null);
        volunteer = volunteerRepository.saveAndFlush(volunteer);

        return Constructor.buildResponseObject(HttpStatus.OK, VolunteerMapper.MAPPER.toDto(volunteer).getAvailability());

    }

    private Volunteer getVolunteerFromToken(String token) {
        try {
            User user = userRepository.findById(jwtUtils.getUserIdFromStringToken(token)).orElseThrow(
                () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

            return Optional.ofNullable(user.getVolunteer()).orElseThrow(
                () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_DOESNT_HAVE_VOLUNTEER));

        } catch (ParseException ignore) { }

        return null;
    }

    public ResponseEntity<?> getAvailability(String builderAssistantId) {
        Volunteer volunteer = getAvailabilityFromBAId(builderAssistantId);

        return Constructor.buildResponseObject(HttpStatus.OK, VolunteerMapper.MAPPER.toDto(volunteer).getAvailability());

    }

    public ResponseEntity<?> updateAvailability(String builderAssistantId, List<EWeekday> availability) {
        Volunteer volunteer = getAvailabilityFromBAId(builderAssistantId);
        volunteer.setAvailability(new LinkedHashSet<>(availability));
        volunteer = volunteerRepository.saveAndFlush(volunteer);

        return Constructor.buildResponseObject(HttpStatus.CREATED, VolunteerMapper.MAPPER.toDto(volunteer).getAvailability());
    }

    public ResponseEntity<?> clearAvailability(String builderAssistantId) {
        Volunteer volunteer = getAvailabilityFromBAId(builderAssistantId);
        volunteer.setAvailability(null);
        volunteer = volunteerRepository.saveAndFlush(volunteer);

        return Constructor.buildResponseObject(HttpStatus.OK, VolunteerMapper.MAPPER.toDto(volunteer).getAvailability());
    }

    private Volunteer getAvailabilityFromBAId(String builderAssistantId) {
        return volunteerRepository.findByBuilderAssistantId(builderAssistantId).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));
    }

}
