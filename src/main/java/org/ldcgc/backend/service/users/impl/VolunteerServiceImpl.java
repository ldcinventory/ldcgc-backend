package org.ldcgc.backend.service.users.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.users.AbsenceRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.other.PaginationDetails;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.payload.mapper.users.VolunteerMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.VolunteerService;
import org.ldcgc.backend.util.common.EOrder;
import org.ldcgc.backend.util.common.EVolunteerStatus;
import org.ldcgc.backend.util.common.EWeekday;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Constructor;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.ldcgc.backend.util.process.Files.getContentFromCSV;

@Component
@RequiredArgsConstructor
@Transactional
public class VolunteerServiceImpl implements VolunteerService {

    private final JwtUtils jwtUtils;
    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final AbsenceRepository absenceRepository;

    public ResponseEntity<?> getMyVolunteer(String token) throws ParseException {
        Integer userId = jwtUtils.getUserIdFromStringToken(token);
        Volunteer volunteer = userRepository.findById(userId).map(User::getVolunteer).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_TOKEN_NOT_EXIST));

        return Constructor.buildResponseObject(HttpStatus.OK, VolunteerMapper.MAPPER.toDto(volunteer));
    }

    public ResponseEntity<?> getVolunteer(String builderAssistantId) {
        return Constructor.buildResponseObject(HttpStatus.OK,
            VolunteerMapper.MAPPER.toDto(getVolunteerFromDB(builderAssistantId)));
    }

    public ResponseEntity<?> createVolunteer(VolunteerDto volunteer) {
        if(volunteerRepository.findByBuilderAssistantId(volunteer.getBuilderAssistantId()).isPresent())
            throw new RequestException(HttpStatus.CONFLICT, String.format(Messages.Error.VOLUNTEER_ALREADY_EXIST, volunteer.getBuilderAssistantId()));

        Volunteer volunteerEntity = VolunteerMapper.MAPPER.toEntity(volunteer);

        volunteerEntity = volunteerRepository.saveAndFlush(volunteerEntity);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.VOLUNTEER_CREATED, VolunteerMapper.MAPPER.toDto(volunteerEntity));
    }

    public ResponseEntity<?> listVolunteers(String builderAssistantId, String filterString, EVolunteerStatus status, Integer pageIndex, Integer size, String sortField, EOrder order) {

        if (builderAssistantId != null)
            return Constructor.buildResponseMessageObject(
                HttpStatus.OK,
                String.format(Messages.Info.VOLUNTEER_FOUND, builderAssistantId),
                PaginationDetails.pagingOneObject(VolunteerMapper.MAPPER.toDto(getVolunteerFromDB(builderAssistantId))));

        Pageable pageable = PageRequest.of(pageIndex, size, order.equals(EOrder.DESC)
            ? Sort.by(sortField).descending()
            : Sort.by(sortField).ascending());
        Page<VolunteerDto> pagedVolunteers = StringUtils.isBlank(filterString) && status == null ?
            volunteerRepository.findAll(pageable).map(VolunteerMapper.MAPPER::toDto) :
            volunteerRepository.findAllFiltered(filterString, Optional.ofNullable(status).map(Enum::name).orElse(null), pageable).map(VolunteerMapper.MAPPER::toDto);

        if (pageIndex > pagedVolunteers.getTotalPages())
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.PAGE_INDEX_REQUESTED_EXCEEDED_TOTAL);

        return Constructor.buildResponseMessageObject(
            HttpStatus.OK,
            String.format(Messages.Info.VOLUNTEER_LISTED, pagedVolunteers.getTotalElements()),
            PaginationDetails.fromPaging(pageable, pagedVolunteers));
    }

    public ResponseEntity<?> updateVolunteer(String builderAssistantId, VolunteerDto volunteerDto) {
        Volunteer volunteerEntity = getVolunteerFromDB(builderAssistantId);

        boolean builderAssistantExists = volunteerRepository.existsByBuilderAssistantId(volunteerDto.getBuilderAssistantId());

        if(builderAssistantExists && !volunteerEntity.getBuilderAssistantId().equals(volunteerDto.getBuilderAssistantId()))
            throw new RequestException(HttpStatus.CONFLICT, Messages.Error.VOLUNTEER_ID_ALREADY_TAKEN);

        VolunteerMapper.MAPPER.update(volunteerEntity, volunteerDto);

        volunteerRepository.saveAndFlush(volunteerEntity);

        return Constructor.buildResponseMessageObject(HttpStatus.OK, Messages.Info.VOLUNTEER_UPDATED, VolunteerMapper.MAPPER.toDto(volunteerEntity));
    }

    public ResponseEntity<?> deleteVolunteer(String builderAssistantId, Boolean confirmDeletion) {
        User userLinked = userRepository.findByVolunteerBAId(builderAssistantId).orElse(null);

        if(userLinked != null && BooleanUtils.isNotTrue(confirmDeletion))
            return Constructor.buildResponseMessage(
                HttpStatus.MULTIPLE_CHOICES,
                String.format(Messages.Warning.VOLUNTEER_LINKED_TO_USER, builderAssistantId));
        else if(Optional.ofNullable(userLinked).map(User::getVolunteer).isPresent()) {
            userLinked.setVolunteer(null);
            userRepository.saveAndFlush(userLinked);
        }

        Volunteer volunteer = getVolunteerFromDB(builderAssistantId);
        volunteer.setStatus(EVolunteerStatus.DELETED);

        // unlink absences and delete them all
        absenceRepository.deleteAllByVolunteerId(volunteer.getId());
        volunteer.getAbsences().removeAll(volunteer.getAbsences());

        volunteer.setAvailability(null);
        volunteerRepository.saveAndFlush(volunteer);

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.VOLUNTEER_DELETED);
    }

    public ResponseEntity<?> uploadVolunteers(MultipartFile document) {
        AtomicInteger volunteers = new AtomicInteger();
        Integer groupId = Integer.valueOf(Optional.ofNullable(MDC.get("groupId")).orElseThrow(() ->
            new RequestException(HttpStatus.FORBIDDEN, Messages.Error.GROUP_NOT_FOUND_IN_TOKEN)));
        Group group = groupRepository.findById(groupId).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.GROUP_NOT_FOUND));

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
                .status(EVolunteerStatus.valueOf(vData.get(3)))
                .availability(availability)
                .group(group)
                .build();

            volunteerRepository.saveAndFlush(volunteer);
            volunteers.getAndIncrement();
        });

        return Constructor.buildResponseMessage(HttpStatus.CREATED, String.format(Messages.Info.CSV_VOLUNTEERS_CREATED, volunteers));
    }

    private Volunteer getVolunteerFromDB(String builderAssistantId) {
        return volunteerRepository.findByBuilderAssistantId(builderAssistantId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));
    }

    private Set<EWeekday> getAvailabilityFromCSVData(List<String> availabilityDays) {
        Set<EWeekday> availabilityDaysSet = new LinkedHashSet<>();
        for(int i = 0; i < availabilityDays.size(); i++)
            if(StringUtils.isNotBlank(availabilityDays.get(i)))
                availabilityDaysSet.add(EWeekday.values()[i]);

        return availabilityDaysSet;
    }

}
