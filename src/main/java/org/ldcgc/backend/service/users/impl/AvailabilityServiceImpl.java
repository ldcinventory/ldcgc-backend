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
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Constructor;
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
        return getAvailability(volunteer);
    }

    public ResponseEntity<?> updateMyAvailability(String token, List<EWeekday> availability) {
        Volunteer volunteer = getVolunteerFromToken(token);
        return updateAvailability(volunteer, availability);
    }

    public ResponseEntity<?> clearMyAvailability(String token) {
        Volunteer volunteer = getVolunteerFromToken(token);
        return clearAvailability(volunteer);
    }

    private Volunteer getVolunteerFromToken(String token) {
        try {
            User user = userRepository.findById(jwtUtils.getUserIdFromStringToken(token)).orElseThrow(
                () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

            if(Optional.ofNullable(user.getVolunteer()).isEmpty())
                throw new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_DOESNT_HAVE_VOLUNTEER);

            if(Optional.of(user.getVolunteer()).map(Volunteer::getBuilderAssistantId).isEmpty())
                throw new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_WITHOUT_BA_ID);

            return user.getVolunteer();

        } catch (ParseException ignore) {
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOKEN_NOT_PARSEABLE);
        }
    }

    public ResponseEntity<?> getAvailability(String builderAssistantId) {
        return getAvailability(getVolunteerFromBAId(builderAssistantId));
    }

    public ResponseEntity<?> updateAvailability(String builderAssistantId, List<EWeekday> availability) {
        return updateAvailability(getVolunteerFromBAId(builderAssistantId), availability);
    }

    public ResponseEntity<?> clearAvailability(String builderAssistantId) {
        return clearAvailability(getVolunteerFromBAId(builderAssistantId));
    }

    private Volunteer getVolunteerFromBAId(String builderAssistantId) {
        return volunteerRepository.findByBuilderAssistantId(builderAssistantId).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));
    }

    private ResponseEntity<?> getAvailability(Volunteer volunteer) {
        return Constructor.buildResponseObject(HttpStatus.OK, VolunteerMapper.MAPPER.toDto(volunteer).getAvailability());
    }

    private ResponseEntity<?> updateAvailability(Volunteer volunteer, List<EWeekday> availability) {
        volunteer.setAvailability(new LinkedHashSet<>(availability));
        volunteer = volunteerRepository.saveAndFlush(volunteer);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.AVAILABILITY_UPDATED, VolunteerMapper.MAPPER.toDto(volunteer).getAvailability());
    }

    private ResponseEntity<?> clearAvailability(Volunteer volunteer) {
        volunteer.setAvailability(null);
        volunteer = volunteerRepository.saveAndFlush(volunteer);

        return Constructor.buildResponseMessageObject(HttpStatus.OK, Messages.Info.AVAILABILITY_CLEARED, VolunteerMapper.MAPPER.toDto(volunteer).getAvailability());
    }

}
