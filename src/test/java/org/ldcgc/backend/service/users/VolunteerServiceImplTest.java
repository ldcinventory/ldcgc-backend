package org.ldcgc.backend.service.users;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.mock.MockedUserVolunteer;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.payload.mapper.users.VolunteerMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.impl.VolunteerServiceImpl;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.retrieving.Messages;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.ldcgc.backend.base.mock.MockedToken.generateNewStringToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest
class VolunteerServiceImplTest {

    private VolunteerService volunteerService;

    @Mock VolunteerRepository volunteerRepository;
    @Mock UserRepository userRepository;
    @Mock JwtUtils jwtUtils;
    @Mock GroupRepository groupRepository;

    @BeforeEach
    public void init() {
        volunteerService = new VolunteerServiceImpl(jwtUtils, volunteerRepository, userRepository, groupRepository);
    }

    private final User USER_WITHOUT_VOLUNTEER = UserMapper.MAPPER.toEntity(MockedUserVolunteer.getRandomMockedUserDtoWithoutVolunteer());
    private final User USER_WITH_VOLUNTEER = UserMapper.MAPPER.toEntity(MockedUserVolunteer.getRandomMockedUserDto());
    private final Volunteer VOLUNTEER = VolunteerMapper.MAPPER.toEntity(MockedUserVolunteer.getRandomVolunteer());
    private final Volunteer VOLUNTEER_2 = VolunteerMapper.MAPPER.toEntity(MockedUserVolunteer.getRandomVolunteer());

    //get my volunteer
    @Test
    public void whenGetMyVolunteer_returnVolunteerNotFound() throws ParseException {
        final String mockedToken = generateNewStringToken(UserMapper.MAPPER.toEntity(MockedUserVolunteer.getRandomMockedUserDto(ERole.ROLE_USER)));

        doReturn(USER_WITHOUT_VOLUNTEER.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.empty()).when(userRepository).findById(USER_WITHOUT_VOLUNTEER.getId());

        RequestException ex = assertThrows(RequestException.class, () -> volunteerService.getMyVolunteer(mockedToken));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_TOKEN_NOT_EXIST, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(any());

    }

    @Test
    public void whenGetMyVolunteer_returnMyVolunteer() throws ParseException {
        final String mockedToken = generateNewStringToken(UserMapper.MAPPER.toEntity(MockedUserVolunteer.getRandomMockedUserDto(ERole.ROLE_USER)));

        doReturn(VOLUNTEER.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(VOLUNTEER.getId());

        ResponseEntity<?> response = volunteerService.getMyVolunteer(mockedToken);
        Response.DTO responseBody = (Response.DTO) response.getBody();

        VolunteerDto volunteerDto = VolunteerMapper.MAPPER.toDto(USER_WITH_VOLUNTEER.getVolunteer());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getData());
        assertEquals(volunteerDto, responseBody.getData());

        verify(userRepository, atMostOnce()).findById(any());

    }

    //get volunteer
    @Test
    public void whenGetVolunteer_returnVolunteerNotFound() {
        String builderAssistantId = VOLUNTEER.getBuilderAssistantId();

        doReturn(Optional.empty()).when(volunteerRepository).findByBuilderAssistantId(builderAssistantId);

        RequestException ex = assertThrows(RequestException.class, () -> volunteerService.getVolunteer(builderAssistantId));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_NOT_FOUND, ex.getMessage());

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(any());

    }

    @Test
    public void whenGetVolunteer_returnVolunteer() {
        VolunteerDto volunteerDto = VolunteerMapper.MAPPER.toDto(VOLUNTEER);
        String builderAssistantId = VOLUNTEER.getBuilderAssistantId();

        doReturn(Optional.of(VOLUNTEER)).when(volunteerRepository).findByBuilderAssistantId(builderAssistantId);

        ResponseEntity<?> response = volunteerService.getVolunteer(builderAssistantId);
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getData());
        assertEquals(volunteerDto, responseBody.getData());

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(any());

    }

    //create volunteer
    @Test
    public void whenCreateVolunteer_returnVolunteerAlreadyExists() {
        VolunteerDto volunteerDto = VolunteerMapper.MAPPER.toDto(VOLUNTEER);
        String builderAssistantId = volunteerDto.getBuilderAssistantId();

        doReturn(Optional.of(VOLUNTEER)).when(volunteerRepository).findByBuilderAssistantId(builderAssistantId);

        RequestException ex = assertThrows(RequestException.class, () -> volunteerService.createVolunteer(volunteerDto));

        assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
        assertEquals(String.format(Messages.Error.VOLUNTEER_ALREADY_EXIST, builderAssistantId), ex.getMessage());

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(any());
    }

    @Test
    public void whenCreateVolunteer_returnVolunteerCreated() {
        VolunteerDto volunteerDto = VolunteerMapper.MAPPER.toDto(VOLUNTEER);
        String builderAssistantId = volunteerDto.getBuilderAssistantId();

        doReturn(Optional.empty()).when(volunteerRepository).findByBuilderAssistantId(builderAssistantId);
        doReturn(VOLUNTEER).when(volunteerRepository).save(any(Volunteer.class));

        ResponseEntity<?> response = volunteerService.createVolunteer(volunteerDto);
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(responseBody.getMessage());
        assertEquals(Messages.Info.VOLUNTEER_CREATED, responseBody.getMessage());
        assertEquals(volunteerDto, responseBody.getData());

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(any());
        verify(volunteerRepository, atMostOnce()).save(any());
    }

    //list volunteer
    @Test
    public void whenListVolunteerFilteredByBuilderAssistantId_returnOneVolunteer() {
        final String builderAssistantId = VOLUNTEER.getBuilderAssistantId();
        final VolunteerDto volunteerExpected = VolunteerMapper.MAPPER.toDto(VOLUNTEER);

        doReturn(Optional.of(VOLUNTEER)).when(volunteerRepository).findByBuilderAssistantId(builderAssistantId);

        ResponseEntity<?> response = volunteerService.listVolunteers(null, null, null, builderAssistantId);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(responseBody.getData());
        assertEquals(responseBody.getData(), volunteerExpected);

        verify(userRepository, atMostOnce()).findById(any());
    }

    @Test
    public void whenListVolunteerNonFiltered_returnMultipleVolunteers() {
        final List<VolunteerDto> volunteers = MockedUserVolunteer.getListOfMockedVolunteers(5);
        final List<Volunteer> volunteersEntities = volunteers.stream().map(VolunteerMapper.MAPPER::toEntity).toList();

        Page<Volunteer> userPage = new PageImpl<>(volunteersEntities);

        doReturn(userPage).when(volunteerRepository).findAll(any(Pageable.class));

        ResponseEntity<?> response = volunteerService.listVolunteers(0, 5, null, null);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getMessage());
        assertEquals(String.format(Messages.Info.VOLUNTEER_LISTED, 5), responseBody.getMessage());
        assertEquals(volunteers, responseBody.getData());

        verify(userRepository, atMostOnce()).findAll(any(Pageable.class));
    }

    @Test
    public void whenListVolunteerFiltered_returnMultipleVolunteers() {
        final List<VolunteerDto> volunteers = MockedUserVolunteer.getListOfMockedVolunteers(5);
        final List<Volunteer> volunteersEntities = volunteers.stream().map(VolunteerMapper.MAPPER::toEntity).toList();

        Page<Volunteer> userPage = new PageImpl<>(volunteersEntities);

        doReturn(userPage).when(volunteerRepository).findAllFiltered(anyString(), any(Pageable.class));

        ResponseEntity<?> response = volunteerService.listVolunteers(0, 5, "x", null);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getMessage());
        assertEquals(String.format(Messages.Info.VOLUNTEER_LISTED, 5), responseBody.getMessage());
        assertEquals(volunteers, responseBody.getData());

        verify(userRepository, atMostOnce()).findAll(any(Pageable.class));
    }

    //update volunteer
    @Test
    public void whenUpdateVolunteer_returnVolunteerNotFound() {
        String volunteerId = VOLUNTEER.getBuilderAssistantId();
        VolunteerDto volunteerDto = VolunteerMapper.MAPPER.toDto(VOLUNTEER);
        String builderAssistantId = volunteerDto.getBuilderAssistantId();

        doReturn(Optional.empty()).when(volunteerRepository).findByBuilderAssistantId(builderAssistantId);

        RequestException ex = assertThrows(RequestException.class, () -> volunteerService.updateVolunteer(volunteerId, volunteerDto));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_NOT_FOUND, ex.getMessage());

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(any());
    }

    @Test
    public void whenUpdateVolunteer_returnVolunteerBuilderAssistanIdTaken() {
        VolunteerDto volunteerDto = VolunteerMapper.MAPPER.toDto(VOLUNTEER);

        String volunteerId_2 = VOLUNTEER.getBuilderAssistantId() + "0";
        final Volunteer VOLUNTEER_2 = VolunteerMapper.MAPPER.toEntity(volunteerDto).toBuilder().builderAssistantId(volunteerId_2).build();

        doReturn(Optional.of(VOLUNTEER_2)).when(volunteerRepository).findByBuilderAssistantId(volunteerId_2);
        //doReturn(Optional.of(VOLUNTEER)).when(volunteerRepository).findByBuilderAssistantId(volunteerDto.getBuilderAssistantId());
        doReturn(true).when(volunteerRepository).existsByBuilderAssistantId(volunteerDto.getBuilderAssistantId());

        RequestException ex = assertThrows(RequestException.class, () -> volunteerService.updateVolunteer(volunteerId_2, volunteerDto));

        assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_ID_ALREADY_TAKEN, ex.getMessage());

        verify(volunteerRepository, times(1)).findByBuilderAssistantId(any());
    }

    @Test
    public void whenUpdateVolunteer_returnVolunteerUpdated() {
        // original volunteer
        VolunteerDto volunteerDto = VolunteerMapper.MAPPER.toDto(VOLUNTEER);
        // volunteer new details
        String builderAssistantId = VOLUNTEER_2.getBuilderAssistantId();

        doReturn(Optional.of(VOLUNTEER_2)).when(volunteerRepository).findByBuilderAssistantId(builderAssistantId);
        doReturn(Optional.empty()).when(volunteerRepository).findByBuilderAssistantId(volunteerDto.getBuilderAssistantId());
        doReturn(VOLUNTEER_2).when(volunteerRepository).save(any(Volunteer.class));

        ResponseEntity<?> response = volunteerService.updateVolunteer(builderAssistantId, volunteerDto);
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getMessage());
        assertEquals(Messages.Info.VOLUNTEER_UPDATED, responseBody.getMessage());
        assertEquals(volunteerDto, responseBody.getData());

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(any());
        verify(volunteerRepository, atMostOnce()).save(any());
    }

    //delete volunteer
    @Test
    public void whenDeleteVolunteer_returnVolunteerNotFound() {
        String builderAssistantId = MockedUserVolunteer.getRandomBuilderAssistantId();
        doReturn(Optional.empty()).when(volunteerRepository).findByBuilderAssistantId(builderAssistantId);

        RequestException ex = assertThrows(RequestException.class, () -> volunteerService.deleteVolunteer(builderAssistantId));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_NOT_FOUND, ex.getMessage());

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(any());
    }

    @Test
    public void whenDeleteVolunteer_returnVolunteerDeleted() {
        String builderAssistantId = VOLUNTEER.getBuilderAssistantId();
        doReturn(Optional.of(VOLUNTEER)).when(volunteerRepository).findByBuilderAssistantId(builderAssistantId);

        ResponseEntity<?> response = volunteerService.deleteVolunteer(builderAssistantId);
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getMessage());
        assertEquals(Messages.Info.VOLUNTEER_DELETED, responseBody.getMessage());

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(any());
        verify(volunteerRepository, atMostOnce()).delete(any(Volunteer.class));
    }

    @Test
    public void whenUploadVolunteers_returnGroupNotFound() {
        MockMultipartFile document = new MockMultipartFile("document", "volunteers.csv", "text/csv", "50280100,Daniel,Albert,true,,x,,x,x,,,x".getBytes());

        doReturn(Optional.empty()).when(groupRepository).findById(8);

        RequestException ex = assertThrows(RequestException.class, () -> volunteerService.uploadVolunteers(8, document));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.GROUP_NOT_FOUND, ex.getMessage());

        verify(groupRepository, atMostOnce()).findById(anyInt());
    }

    @Test
    public void whenUploadVolunteers_returnCSVVolunteersCreated() {

        MultipartFile document = new MockMultipartFile("document", "volunteers.csv", "text/csv", "builderAssistantId,name,lastName,active,mon,tue,wed,thu,fri,sat,sun,hol\n50280100,Daniel,Albert,true,,x,,x,x,,,x".getBytes());

        Group group = Group.builder().id(8).build();
        doReturn(Optional.of(group)).when(groupRepository).findById(8);
        doReturn(Optional.empty()).when(volunteerRepository).findByBuilderAssistantId(anyString());

        ResponseEntity<?> response = volunteerService.uploadVolunteers(8, document);
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(responseBody.getMessage());
        assertEquals(String.format(Messages.Info.CSV_VOLUNTEERS_CREATED, 1), responseBody.getMessage());

        verify(groupRepository, atMostOnce()).findById(anyInt());
        verify(volunteerRepository, atMostOnce()).saveAndFlush(any(Volunteer.class));

    }

}
