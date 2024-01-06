package org.ldcgc.backend.service.users.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.payload.mapper.users.VolunteerMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.VolunteerService;
import org.ldcgc.backend.util.common.EWeekday;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.ldcgc.backend.util.retrieving.Files.getContentFromCSV;

@Component
@RequiredArgsConstructor
@Transactional
public class VolunteerServiceImpl implements VolunteerService {

    private final JwtUtils jwtUtils;
    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public ResponseEntity<?> getMyVolunteer(String token) throws ParseException {

        Integer userId = jwtUtils.getUserIdFromStringToken(token);
        Volunteer volunteer = userRepository.findById(userId).map(User::getVolunteer).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_TOKEN_NOT_EXIST));

        return Constructor.buildResponseObject(HttpStatus.OK, VolunteerMapper.MAPPER.toDto(volunteer));
    }

    public ResponseEntity<?> getVolunteer(String volunteerId) {

        Volunteer volunteerEntity = volunteerRepository.findByBuilderAssistantId(volunteerId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));

        return Constructor.buildResponseObject(HttpStatus.OK, VolunteerMapper.MAPPER.toDto(volunteerEntity));
    }

    public ResponseEntity<?> createVolunteer(VolunteerDto volunteer) {

        if(volunteerRepository.findByBuilderAssistantId(volunteer.getBuilderAssistantId()).isPresent())
            throw new RequestException(HttpStatus.CONFLICT, String.format(Messages.Error.VOLUNTEER_ALREADY_EXIST, volunteer.getBuilderAssistantId()));

        Volunteer volunteerEntity = VolunteerMapper.MAPPER.toEntity(volunteer);

        volunteerEntity = volunteerRepository.save(volunteerEntity);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.VOLUNTEER_CREATED, VolunteerMapper.MAPPER.toDto(volunteerEntity));
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
            String.format(Messages.Info.USER_LISTED, pageUsers.getTotalElements()),
            userList.stream().map(VolunteerMapper.MAPPER::toDto).toList());
    }

    public ResponseEntity<?> updateVolunteer(String volunteerId, VolunteerDto volunteer) {

        Volunteer volunteerEntity = volunteerRepository.findByBuilderAssistantId(volunteerId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));

        if(volunteerEntity.getBuilderAssistantId().equals(volunteer.getBuilderAssistantId()))
            throw new RequestException(HttpStatus.CONFLICT, Messages.Error.VOLUNTEER_ID_ALREADY_TAKEN);

        VolunteerMapper.MAPPER.update(volunteerEntity, volunteer);

        volunteerRepository.save(volunteerEntity);

        return Constructor.buildResponseMessageObject(HttpStatus.OK, Messages.Info.VOLUNTEER_UPDATED, VolunteerMapper.MAPPER.toDto(volunteerEntity));
    }

    public ResponseEntity<?> deleteVolunteer(String volunteerId) {
        Volunteer volunteer = volunteerRepository.findByBuilderAssistantId(volunteerId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));

        volunteerRepository.delete(volunteer);

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.VOLUNTEER_DELETED);
    }

    public ResponseEntity<?> uploadVolunteers(Integer groupId, MultipartFile document) {
        AtomicInteger volunteers = new AtomicInteger();
        Group group = groupRepository.findById(groupId).orElseThrow(
            () -> new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.GROUP_NOT_FOUND));

        List<List<String>> volunteersData = getContentFromCSV(document.getResource(), ',', true);
        volunteersData.forEach(vData -> {
            if (volunteerRepository.findByBuilderAssistantId(vData.get(0)).isPresent())
                return;
            // vData[4] Monday - vData[10] Sunday, vData[11] Holiday
            Set<EWeekday> availability = getAvailabilityFromCSVData(List.of(vData.get(4), vData.get(5), vData.get(6), vData.get(7), vData.get(8), vData.get(9), vData.get(10), vData.get(11)));
            Volunteer volunteer = Volunteer.builder()
                .builderAssistantId(vData.get(0))
                .name(vData.get(1))
                .lastName(vData.get(2))
                .isActive(Boolean.parseBoolean(vData.get(3)))
                .availability(availability)
                .group(group)
                .build();

            volunteerRepository.saveAndFlush(volunteer);
            volunteers.getAndIncrement();
        });

        return Constructor.buildResponseMessage(HttpStatus.CREATED, String.format(Messages.Info.CSV_VOLUNTEERS_CREATED, volunteers));
    }

    private Set<EWeekday> getAvailabilityFromCSVData(List<String> availabilityDays) {
        Set<EWeekday> availabilityDaysSet = new LinkedHashSet<>();
        for(int i = 0; i < availabilityDays.size(); i++)
            if(StringUtils.isNotBlank(availabilityDays.get(i)))
                availabilityDaysSet.add(EWeekday.values()[i]);

        return availabilityDaysSet;
    }

}
