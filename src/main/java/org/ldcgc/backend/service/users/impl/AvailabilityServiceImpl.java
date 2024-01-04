package org.ldcgc.backend.service.users.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.users.Availability;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.users.AvailabilityRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.users.AvailabilityDto;
import org.ldcgc.backend.payload.mapper.users.AvailabilityMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.AvailabilityService;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.text.ParseException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final VolunteerRepository volunteerRepository;
    private final AvailabilityRepository availabilityRepository;

    public ResponseEntity<?> getMyAvailability(String token) {
        Volunteer volunteer = getVolunteerFromToken(token);

        return Constructor.buildResponseObject(HttpStatus.OK, AvailabilityMapper.MAPPER.toDto(volunteer.getAvailability()));

    }

    public ResponseEntity<?> updateMyAvailability(String token, AvailabilityDto availabilityDto) {
        Volunteer volunteer = getVolunteerFromToken(token);
        Availability availability = AvailabilityMapper.MAPPER.toEntity(availabilityDto);
        availability.setVolunteer(volunteer);

        volunteer = volunteerRepository.saveAndFlush(volunteer);

        return Constructor.buildResponseObject(HttpStatus.CREATED, AvailabilityMapper.MAPPER.toDto(volunteer.getAvailability()));

    }

    public ResponseEntity<?> clearMyAvailability(String token) {
        Volunteer volunteer = getVolunteerFromToken(token);
        volunteer.getAvailability().setAvailabilityDays(null);
        volunteer = volunteerRepository.saveAndFlush(volunteer);

        return Constructor.buildResponseObject(HttpStatus.OK, AvailabilityMapper.MAPPER.toDto(volunteer.getAvailability()));

    }

    private Volunteer getVolunteerFromToken(String token) {
        try {
            User user = userRepository.findById(jwtUtils.getUserIdFromStringToken(token)).orElseThrow(
                () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

            Volunteer volunteer = Optional.ofNullable(user.getVolunteer()).orElseThrow(
                () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_DOESNT_HAVE_VOLUNTEER));

            return volunteer;

        } catch (ParseException ignore) { }

        return null;
    }

    public ResponseEntity<?> getAvailability(String builderAssistantId) {
        Availability availability = getAvailabilityFromBAId(builderAssistantId);

        return Constructor.buildResponseObject(HttpStatus.OK, AvailabilityMapper.MAPPER.toDto(availability));

    }

    public ResponseEntity<?> updateAvailability(String builderAssistantId, AvailabilityDto availabilityDto) {
        Availability availability = getAvailabilityFromBAId(builderAssistantId);

        AvailabilityMapper.MAPPER.update(availability, availabilityDto);
        availability = availabilityRepository.saveAndFlush(availability);

        return Constructor.buildResponseObject(HttpStatus.CREATED, AvailabilityMapper.MAPPER.toDto(availability));
    }

    public ResponseEntity<?> clearAvailability(String builderAssistantId) {
        Availability availability = getAvailabilityFromBAId(builderAssistantId);

        availability.setAvailabilityDays(null);
        availability = availabilityRepository.saveAndFlush(availability);

        return Constructor.buildResponseObject(HttpStatus.OK, AvailabilityMapper.MAPPER.toDto(availability));
    }

    private Availability getAvailabilityFromBAId(String builderAssistantId) {
        return availabilityRepository.findByVolunteer_BuilderAssistantId(builderAssistantId).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.AVAILABILITY_NOT_FOUND));
    }

}
