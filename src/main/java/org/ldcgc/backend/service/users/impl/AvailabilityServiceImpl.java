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

        return getAvailability(volunteer.getBuilderAssistantId());

    }

    public ResponseEntity<?> updateMyAvailability(String token, List<EWeekday> availability) {
        Volunteer volunteer = getVolunteerFromToken(token);

        return updateAvailability(volunteer.getBuilderAssistantId(), availability);

    }

    public ResponseEntity<?> clearMyAvailability(String token) {
        Volunteer volunteer = getVolunteerFromToken(token);

        return clearAvailability(volunteer.getBuilderAssistantId());

    }

    private Volunteer getVolunteerFromToken(String token) {
        try {
            User user = userRepository.findById(jwtUtils.getUserIdFromStringToken(token)).orElseThrow(
                () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

            if(Optional.ofNullable(user.getVolunteer()).isEmpty())
                new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_DOESNT_HAVE_VOLUNTEER);

            if(Optional.ofNullable(user.getVolunteer()).map(Volunteer::getBuilderAssistantId).isEmpty())
                throw new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_WITHOUT_BA_ID);

            return user.getVolunteer();

        } catch (ParseException ignore) { }

        return null;
    }

    public ResponseEntity<?> getAvailability(String builderAssistantId) {
        Volunteer volunteer = getAvailabilityFromBAId(builderAssistantId);

        return Constructor.buildResponseMessageObject(HttpStatus.OK, Messages.Info.AVAILABILITY_UPDATED, VolunteerMapper.MAPPER.toDto(volunteer).getAvailability());

    }

    public ResponseEntity<?> updateAvailability(String builderAssistantId, List<EWeekday> availability) {
        Volunteer volunteer = getAvailabilityFromBAId(builderAssistantId);
        volunteer.setAvailability(new LinkedHashSet<>(availability));
        volunteer = volunteerRepository.saveAndFlush(volunteer);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.AVAILABILITY_UPDATED, VolunteerMapper.MAPPER.toDto(volunteer).getAvailability());
    }

    public ResponseEntity<?> clearAvailability(String builderAssistantId) {
        Volunteer volunteer = getAvailabilityFromBAId(builderAssistantId);
        volunteer.setAvailability(null);
        volunteer = volunteerRepository.saveAndFlush(volunteer);

        return Constructor.buildResponseMessageObject(HttpStatus.OK, Messages.Info.AVAILABILITY_CLEARED, VolunteerMapper.MAPPER.toDto(volunteer).getAvailability());
    }

    private Volunteer getAvailabilityFromBAId(String builderAssistantId) {
        return volunteerRepository.findByBuilderAssistantId(builderAssistantId).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));
    }

}
