package org.ldcgc.backend.service.users;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.mock.MockedUserVolunteer;
import org.ldcgc.backend.db.model.category.Category;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.category.CategoryRepository;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.payload.mapper.category.CategoryMapper;
import org.ldcgc.backend.payload.mapper.group.GroupMapper;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.payload.mapper.users.VolunteerMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.impl.UserServiceImpl;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.retrieving.Messages;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.ldcgc.backend.base.mock.MockedToken.generateNewStringToken;
import static org.ldcgc.backend.base.mock.MockedToken.generateNewToken;
import static org.ldcgc.backend.base.mock.MockedUserVolunteer.getRandomMockedUserDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest
class UserServiceImplTest {

    private UserService userService;
    @Mock private JwtUtils jwtUtils;
    @Mock private JWSHeader jwsHeader;
    @Mock private SignedJWT signedJWT;
    @Mock private UserRepository userRepository;
    @Mock private VolunteerRepository volunteerRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private GroupRepository groupRepository;
    @Mock private TokenRepository tokenRepository;
    @Mock private AccountService accountService;

    private String mockedToken;
    private SignedJWT mockedSignedJWT;

    @BeforeEach
    public void init() {
        userService = new UserServiceImpl(userRepository, volunteerRepository, categoryRepository, groupRepository, tokenRepository, accountService, jwtUtils);

    }
    private final UserDto NOT_FOUND_USER = UserDto.builder().id(-1).build();
    private final UserDto STANDARD_USER = UserDto.builder().id(0).role(ERole.ROLE_USER).email("user").password("user").build();
    private final UserDto MANAGER_USER = UserDto.builder().id(1).role(ERole.ROLE_MANAGER).email("manager").password("manager").build();
    private final UserDto ADMIN_USER = UserDto.builder().id(2).role(ERole.ROLE_ADMIN).email("admin").password("admin").build();

    // get my user
    @Test
    public void whenGetMyUser_returnUserNotFound() {
        configureToken();
        configureTokenRepositoryReturn(Optional.empty());

        RequestException ex = assertThrows(RequestException.class, () -> userService.getMyUser(mockedToken));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND_TOKEN, ex.getMessage());

        verify(tokenRepository, atMostOnce()).getUserIdFromJwtId(any());

    }

    @Test
    public void whenGetMyUser_returnUser() {
        configureToken();

        final UserDto user = STANDARD_USER;
        final UserDto userExpected = STANDARD_USER.toBuilder().password(null).build();
        configureTokenRepositoryReturn(Optional.of(user.getId()));

        doReturn(Optional.of(UserMapper.MAPPER.toEntity(user))).when(userRepository).findById(user.getId());

        ResponseEntity<?> response = userService.getMyUser(mockedToken);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userExpected, responseBody.getData());

        verify(tokenRepository, atMostOnce()).getUserIdFromJwtId(any());
        verify(userRepository, atMostOnce()).findById(any());

    }

    // update my user
    @Test
    public void whenUpdateMy_returnUserUserNotFound() {
        configureToken();
        configureTokenRepositoryReturn(Optional.empty());

        RequestException ex = assertThrows(RequestException.class, () -> userService.updateMyUser(mockedToken, NOT_FOUND_USER));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND_TOKEN, ex.getMessage());

        verify(tokenRepository, atMostOnce()).getUserIdFromJwtId(any());

    }

    @Test
    public void whenUpdateMyUser_returnMyUserUpdated() throws ParseException, JOSEException {
        configureToken();

        final UserDto user = STANDARD_USER;
        configureTokenRepositoryReturn(Optional.of(user.getId()));

        doReturn(Optional.of(UserMapper.MAPPER.toEntity(user))).when(userRepository).findById(user.getId());

        UserCredentialsDto credentials = UserCredentialsDto.builder()
            .email(user.getEmail()).password(user.getPassword()).build();

        HttpHeaders headers = new HttpHeaders();
        final String headerPayLoad = String.format("%s.%s", mockedSignedJWT.getParsedParts()[0], mockedSignedJWT.getParsedParts()[1]);
        headers.add("x-header-payload-token", headerPayLoad);
        final String signature = mockedSignedJWT.getParsedParts()[2].toString();
        headers.add("x-signature-token", signature);

        doReturn(Constructor.buildResponseObjectHeader(HttpStatus.OK, user, headers)).when(accountService).login(credentials);

        ResponseEntity<?> response = userService.updateMyUser(mockedToken, user);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertTrue(Objects.nonNull(responseBody));

        Response.DTO responseData = (Response.DTO) responseBody.getData();
        assertTrue(Objects.nonNull(responseData));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(Messages.Info.USER_UPDATED, responseBody.getMessage());
        assertEquals(2, response.getHeaders().size());
        assertTrue(response.getHeaders().containsKey("x-header-payload-token"));
        assertEquals(headerPayLoad, response.getHeaders().get("x-header-payload-token").get(0));
        assertTrue(response.getHeaders().containsKey("x-signature-token"), signature);
        assertEquals(signature, response.getHeaders().get("x-signature-token").get(0));
        assertEquals(user, responseData.getData());

        verify(tokenRepository, atMostOnce()).getUserIdFromJwtId(any());
        verify(userRepository, atMostOnce()).findById(any());
        verify(userRepository, atMostOnce()).save(any());
        verify(tokenRepository, atMostOnce()).deleteAllTokensFromUser(any());

    }

    // delete my user
    @Test
    public void whenDeleteMyUser_returnUserNotFound() {
        configureToken();
        configureTokenRepositoryReturn(Optional.empty());

        RequestException ex = assertThrows(RequestException.class, () -> userService.deleteMyUser(mockedToken));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND_TOKEN, ex.getMessage());

        verify(tokenRepository, atMostOnce()).getUserIdFromJwtId(any());

    }

    @Test
    public void whenDeleteMyUser_returnOK() {
        configureToken();

        final UserDto user = STANDARD_USER;
        configureTokenRepositoryReturn(Optional.of(user.getId()));

        doReturn(true).when(userRepository).existsById(user.getId());

        ResponseEntity<?> response = userService.deleteMyUser(mockedToken);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Messages.Info.USER_DELETED, responseBody.getMessage());

        verify(tokenRepository, atMostOnce()).getUserIdFromJwtId(any());
        verify(userRepository, atMostOnce()).existsById(any());
        verify(userRepository, atMostOnce()).deleteById(any());
        verify(tokenRepository, atMostOnce()).deleteAllTokensFromUser(any());

    }

    private void configureToken() {
        try {
            mockedToken = generateNewStringToken(UserMapper.MAPPER.toEntity(getRandomMockedUserDto(ERole.ROLE_USER)));
            mockedSignedJWT = generateNewToken(UserMapper.MAPPER.toEntity(getRandomMockedUserDto(ERole.ROLE_USER)));
        } catch (ParseException | JOSEException e) {
            log.error("Error generating mockedSignedJWT");
            throw new RuntimeException(e.getMessage());
        }

        doReturn(signedJWT).when(jwtUtils).getDecodedJwt(mockedToken);
        doReturn(jwsHeader).when(signedJWT).getHeader();
        doReturn(mockedSignedJWT.getHeader().getKeyID()).when(jwsHeader).getKeyID();
    }

    private void configureTokenRepositoryReturn(Optional<?> optional) {
        doReturn(optional).when(tokenRepository).getUserIdFromJwtId(mockedSignedJWT.getHeader().getKeyID());
    }

    // create user
    @Test
    public void whenCreateUser_returnUserAlreadyTaken() {
        final UserDto user = MANAGER_USER;

        doReturn(Optional.of(UserMapper.MAPPER.toEntity(user))).when(userRepository).findByEmail(user.getEmail());

        RequestException ex = assertThrows(RequestException.class, () -> userService.createUser(mockedToken, user));

        assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_ALREADY_EXIST, ex.getMessage());

        verify(userRepository, atMostOnce()).findByEmail(any());

    }

    @Test
    public void whenCreateUser_returnUserCreated() {
        final UserDto user = MANAGER_USER;
        final UserDto userExpected = MANAGER_USER.toBuilder().password(null).build();
        final User userEntity = UserMapper.MAPPER.toEntity(user);

        doReturn(Optional.empty()).when(userRepository).findByEmail(user.getEmail());
        doReturn(userEntity).when(userRepository).save(userEntity);

        ResponseEntity<?> response = userService.createUser(mockedToken, user);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(Messages.Info.USER_CREATED, responseBody.getMessage());
        assertEquals(userExpected, responseBody.getData());

        verify(userRepository, atMostOnce()).findByEmail(any());
        verify(userRepository, atMostOnce()).save(any());
    }

    // get user
    @Test
    public void whenGetUser_returnUserNotFound() {
        final UserDto user = MANAGER_USER;

        doReturn(Optional.empty()).when(userRepository).findById(user.getId());

        RequestException ex = assertThrows(RequestException.class, () -> userService.getUser(user.getId()));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(any());
    }

    @Test
    public void whenGetUser_returnUser() {
        final UserDto user = MANAGER_USER;
        final UserDto userExpected = MANAGER_USER.toBuilder().password(null).build();
        final User userEntity = UserMapper.MAPPER.toEntity(user);

        doReturn(Optional.of(userEntity)).when(userRepository).findById(user.getId());

        ResponseEntity<?> response = userService.getUser(user.getId());
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userExpected, responseBody.getData());

        verify(userRepository, atMostOnce()).findById(any());
    }

    // list users
    @Test
    public void whenListUsersFilteredByUserId_returnOneUser() {
        final UserDto user = MANAGER_USER;
        final UserDto userExpected = MANAGER_USER.toBuilder().password(null).build();
        final User userEntity = UserMapper.MAPPER.toEntity(user);

        doReturn(Optional.of(userEntity)).when(userRepository).findById(user.getId());

        ResponseEntity<?> response = userService.listUsers(null, null, null, user.getId());
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userExpected, responseBody.getData());

        verify(userRepository, atMostOnce()).findById(any());
    }

    @Test
    public void whenListUsersUnfiltered_returnMultipleUsers() {
        final List<UserDto> users = MockedUserVolunteer.getListOfMockedUsers(5);
        final List<User> userEntities = users.stream().map(UserMapper.MAPPER::toEntity).toList();
        final List<UserDto> usersExpected = users.stream().map(u ->
            u.toBuilder()
                .password(null)
                .volunteer(u.getVolunteer().toBuilder().absences(null).build())
            .build()).toList();

        Page<User> userPage = new PageImpl<>(userEntities);

        doReturn(userPage).when(userRepository).findAll(any(Pageable.class));

        ResponseEntity<?> response = userService.listUsers(0, 5, null, null);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(String.format(Messages.Info.USER_LISTED, 5), responseBody.getMessage());
        assertEquals(usersExpected, responseBody.getData());

        verify(userRepository, atMostOnce()).findAll(any(Pageable.class));
    }

    @Test
    public void whenListUsersFiltered_returnMultipleUsers() {
        final List<UserDto> users = MockedUserVolunteer.getListOfMockedUsers(5);
        final List<User> userEntities = users.stream().map(UserMapper.MAPPER::toEntity).toList();
        final List<UserDto> usersExpected = users.stream().map(u ->
            u.toBuilder()
                .password(null)
                .volunteer(u.getVolunteer().toBuilder().absences(null).build())
            .build()).toList();

        Page<User> userPage = new PageImpl<>(userEntities);

        doReturn(userPage).when(userRepository).findAllFiltered(anyString(), any(Pageable.class));

        ResponseEntity<?> response = userService.listUsers(0, 5, "x", null);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(String.format(Messages.Info.USER_LISTED, 5), responseBody.getMessage());
        assertEquals(usersExpected, responseBody.getData());

        verify(userRepository, atMostOnce()).findAll(any(Pageable.class));
    }

    // update user
    @Test
    public void whenUpdateUserAndFindUserFromToken_returnUserNotFound() throws ParseException {
        // user entity which will be updated
        final Integer userId = NOT_FOUND_USER.getId();
        // user details to update the entity
        final UserDto userDtoUpdating = ADMIN_USER;

        doReturn(userId).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.empty()).when(userRepository).findById(userId);

        RequestException ex = assertThrows(RequestException.class, () -> userService.updateUser(mockedToken, userId, userDtoUpdating));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(any());
    }

    @Test
    public void whenUpdateUserAndFindUserFromUserId_returnUserNotFound() throws ParseException {
        // user which calls endpoint
        final User userToken = UserMapper.MAPPER.toEntity(MANAGER_USER);
        // user entity which will be updated
        final Integer userIdFromToken = MANAGER_USER.getId();
        // user details to update the entity
        final Integer userId = NOT_FOUND_USER.getId();
        final UserDto userDtoUpdating = NOT_FOUND_USER;

        doReturn(userIdFromToken).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userToken)).when(userRepository).findById(userIdFromToken);
        doReturn(Optional.empty()).when(userRepository).findById(userId);

        RequestException ex = assertThrows(RequestException.class, () -> userService.updateUser(mockedToken, userId, userDtoUpdating));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, times(2)).findById(any());
    }

    @Test
    public void whenUpdateUser_validateUserAlreadyExists() throws ParseException {
        // user which calls endpoint
        final User userToken = UserMapper.MAPPER.toEntity(MANAGER_USER);
        final Integer userIdFromToken = MANAGER_USER.getId();
        // user entity which will be updated
        final Integer userId = MANAGER_USER.getId();
        final User userEntityUpdating = UserMapper.MAPPER.toEntity(MANAGER_USER);
        final User checkUserUpdating = UserMapper.MAPPER.toEntity(ADMIN_USER);
        // user details to update the entity
        final UserDto userDtoUpdating = MANAGER_USER;

        doReturn(userIdFromToken).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userToken)).when(userRepository).findById(userIdFromToken);
        doReturn(Optional.of(userEntityUpdating)).when(userRepository).findById(userId);
        doReturn(Optional.of(checkUserUpdating)).when(userRepository).findByEmail(userDtoUpdating.getEmail());

        RequestException ex = assertThrows(RequestException.class, () -> userService.updateUser(mockedToken, userId, userDtoUpdating));

        assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_ALREADY_EXIST, ex.getMessage());

        verify(userRepository, times(2)).findById(any());
        verify(userRepository, atMostOnce()).findByEmail(any());
    }

    @Test
    public void whenUpdateUser_validateChangeSelfRole() throws ParseException {
        // user which calls endpoint
        final User userToken = UserMapper.MAPPER.toEntity(MANAGER_USER);
        final Integer userIdFromToken = MANAGER_USER.getId();
        // user entity which will be updated
        final Integer userId = MANAGER_USER.getId();
        final User userEntityUpdating = UserMapper.MAPPER.toEntity(MANAGER_USER);
        final User checkUserUpdating = UserMapper.MAPPER.toEntity(MANAGER_USER);
        // user details to update the entity
        final UserDto userDtoUpdating = MANAGER_USER.toBuilder().role(ERole.ROLE_ADMIN).build();

        doReturn(userIdFromToken).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userToken)).when(userRepository).findById(userIdFromToken);
        doReturn(Optional.of(userEntityUpdating)).when(userRepository).findById(userId);
        doReturn(Optional.of(checkUserUpdating)).when(userRepository).findByEmail(userDtoUpdating.getEmail());

        RequestException ex = assertThrows(RequestException.class, () -> userService.updateUser(mockedToken, userId, userDtoUpdating));

        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_PERMISSION_ROLE, ex.getMessage());

        verify(userRepository, times(2)).findById(any());
        verify(userRepository, atMostOnce()).findByEmail(any());
    }

    @Test
    public void whenUpdateUser_validateManagerCreatingAdmin() throws ParseException {
        // user which calls endpoint
        final User userToken = UserMapper.MAPPER.toEntity(MANAGER_USER);
        final Integer userIdFromToken = MANAGER_USER.getId();
        // user entity which will be updated
        final Integer userId = ADMIN_USER.getId();
        final User userEntityUpdating = UserMapper.MAPPER.toEntity(ADMIN_USER);
        final User checkUserUpdating = UserMapper.MAPPER.toEntity(ADMIN_USER);
        // user details to update the entity
        final UserDto userDtoUpdating = ADMIN_USER.toBuilder().role(ERole.ROLE_ADMIN).build();

        doReturn(userIdFromToken).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userToken)).when(userRepository).findById(userIdFromToken);
        doReturn(Optional.of(userEntityUpdating)).when(userRepository).findById(userId);
        doReturn(Optional.of(checkUserUpdating)).when(userRepository).findByEmail(userDtoUpdating.getEmail());

        RequestException ex = assertThrows(RequestException.class, () -> userService.updateUser(mockedToken, userId, userDtoUpdating));

        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_PERMISSION_OTHER, ex.getMessage());

        verify(userRepository, times(2)).findById(any());
        verify(userRepository, atMostOnce()).findByEmail(any());
    }

    @Test
    public void whenUpdateUser_validateManagerElevatingToAdmin() throws ParseException {
        // user entity which will be updated
        // user which calls endpoint
        final User userToken = UserMapper.MAPPER.toEntity(MANAGER_USER);
        final Integer userIdFromToken = MANAGER_USER.getId();
        // user entity which will be updated
        final Integer userId = STANDARD_USER.getId();
        final User userEntityUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER);
        final User checkUserUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER);
        // user details to update the entity
        final UserDto userDtoUpdating = STANDARD_USER.toBuilder().role(ERole.ROLE_ADMIN).build();

        doReturn(userIdFromToken).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userToken)).when(userRepository).findById(userIdFromToken);
        doReturn(Optional.of(userEntityUpdating)).when(userRepository).findById(userId);
        doReturn(Optional.of(checkUserUpdating)).when(userRepository).findByEmail(userDtoUpdating.getEmail());

        RequestException ex = assertThrows(RequestException.class, () -> userService.updateUser(mockedToken, userId, userDtoUpdating));

        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_PERMISSION_ROLE_OTHER, ex.getMessage());

        verify(userRepository, times(2)).findById(any());
        verify(userRepository, atMostOnce()).findByEmail(any());
    }

    @Test
    public void whenUpdateUserWithVolunteer_returnVolunteerNotFound() throws ParseException {
        // user entity which will be updated
        final User userToken = UserMapper.MAPPER.toEntity(MANAGER_USER);
        final Integer userIdFromToken = MANAGER_USER.getId();
        // user entity which will be updated
        final Integer userId = STANDARD_USER.getId();
        final User userEntityUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER);
        final User checkUserUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER);
        final VolunteerDto volunteerDto = VolunteerDto.builder().id(0).build();
        // user details to update the entity
        final UserDto userDtoUpdating = STANDARD_USER.toBuilder().role(ERole.ROLE_MANAGER).volunteer(volunteerDto).build();

        doReturn(userIdFromToken).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userToken)).when(userRepository).findById(userIdFromToken);
        doReturn(Optional.of(userEntityUpdating)).when(userRepository).findById(userId);
        doReturn(Optional.of(checkUserUpdating)).when(userRepository).findByEmail(userDtoUpdating.getEmail());
        doReturn(Optional.empty()).when(volunteerRepository).findById(userDtoUpdating.getId());

        RequestException ex = assertThrows(RequestException.class, () -> userService.updateUser(mockedToken, userId, userDtoUpdating));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_NOT_FOUND, ex.getMessage());

        verify(userRepository, times(2)).findById(any());
        verify(userRepository, atMostOnce()).findByEmail(any());
        verify(volunteerRepository, atMostOnce()).findById(any());

    }

    @Test
    public void whenUpdateUserWithVolunteer_returnVolunteerAssigned() throws ParseException {
        // user entity which will be updated
        final User userToken = UserMapper.MAPPER.toEntity(MANAGER_USER);
        final Integer userIdFromToken = MANAGER_USER.getId();
        // user entity which will be updated
        final Integer userId = STANDARD_USER.getId();
        final User userEntityUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER);
        final User checkUserUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER);
        // volunteer
        final VolunteerDto volunteerDto = VolunteerDto.builder().id(0).build();
        final Volunteer volunteer = VolunteerMapper.MAPPER.toEntity(volunteerDto);
        final User checkUserVolunteer = UserMapper.MAPPER.toEntity(MANAGER_USER).toBuilder().volunteer(volunteer).build();
        // user details to update the entity
        final UserDto userDtoUpdating = STANDARD_USER.toBuilder().role(ERole.ROLE_MANAGER).volunteer(volunteerDto).build();

        doReturn(userIdFromToken).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userToken)).when(userRepository).findById(userIdFromToken);
        doReturn(Optional.of(userEntityUpdating)).when(userRepository).findById(userId);
        doReturn(Optional.of(checkUserUpdating)).when(userRepository).findByEmail(userDtoUpdating.getEmail());
        doReturn(Optional.of(volunteer)).when(volunteerRepository).findById(userDtoUpdating.getVolunteer().getId());
        doReturn(Optional.of(checkUserVolunteer)).when(userRepository).findByVolunteer_Id(userDtoUpdating.getVolunteer().getId());

        RequestException ex = assertThrows(RequestException.class, () -> userService.updateUser(mockedToken, userId, userDtoUpdating));

        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_VOLUNTEER_ALREADY_ASSIGNED, ex.getMessage());

        verify(userRepository, times(2)).findById(any());
        verify(userRepository, atMostOnce()).findByEmail(any());
        verify(volunteerRepository, atMostOnce()).findById(any());
        verify(userRepository, atMostOnce()).findByVolunteer_Id(any());

    }

    @Test
    public void whenUpdateUserWithoutPreviousResponsibility_returnCategoryNotFound() throws ParseException {
        // user which calls endpoint
        final User userToken = UserMapper.MAPPER.toEntity(MANAGER_USER);
        final Integer userIdFromToken = MANAGER_USER.getId();
        // user entity which will be updated
        final Integer userId = STANDARD_USER.getId();
        final User userEntityUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER);
        final User checkUserUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER);
        // responsibility
        final CategoryDto responsibilityDto = CategoryDto.builder().id(0).build();
        // user details to update the entity
        final UserDto userDtoUpdating = STANDARD_USER.toBuilder().role(ERole.ROLE_MANAGER).responsibility(responsibilityDto).build();

        doReturn(userIdFromToken).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userToken)).when(userRepository).findById(userIdFromToken);
        doReturn(Optional.of(userEntityUpdating)).when(userRepository).findById(userId);
        doReturn(Optional.of(checkUserUpdating)).when(userRepository).findByEmail(userDtoUpdating.getEmail());

        // responsibility
        doReturn(Optional.empty()).when(categoryRepository).findById(userDtoUpdating.getResponsibility().getId());

        RequestException ex = assertThrows(RequestException.class, () -> userService.updateUser(mockedToken, userId, userDtoUpdating));

        assertEquals(ex.getHttpStatus(), HttpStatus.NOT_FOUND);
        assertEquals(ex.getMessage(), String.format(Messages.Error.CATEGORY_NOT_FOUND, userDtoUpdating.getResponsibility().getId()));

        verify(userRepository, times(2)).findById(any());
        verify(userRepository, atMostOnce()).findByEmail(any());
        verify(volunteerRepository, atMostOnce()).findById(any());
        verify(userRepository, atMostOnce()).findByVolunteer_Id(any());
        verify(categoryRepository, atMostOnce()).findById(any());

    }

    @Test
    public void whenUpdateUserWithDifferentResponsibility_returnCategoryNotFound() throws ParseException {
        // user which calls endpoint
        final User userToken = UserMapper.MAPPER.toEntity(MANAGER_USER);
        final Integer userIdFromToken = MANAGER_USER.getId();
        // responsibility
        final CategoryDto responsibilityDtoOrigin = CategoryDto.builder().id(0).build();
        final CategoryDto responsibilityDtoUpdating = CategoryDto.builder().id(1).build();
        // user entity which will be updated
        final Integer userId = STANDARD_USER.getId();
        final User userEntityUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER.toBuilder().responsibility(responsibilityDtoOrigin).build());
        final User checkUserUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER);
        // user details to update the entity
        final UserDto userDtoUpdating = STANDARD_USER.toBuilder().role(ERole.ROLE_MANAGER).responsibility(responsibilityDtoUpdating).build();

        doReturn(userIdFromToken).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userToken)).when(userRepository).findById(userIdFromToken);
        doReturn(Optional.of(userEntityUpdating)).when(userRepository).findById(userId);
        doReturn(Optional.of(checkUserUpdating)).when(userRepository).findByEmail(userDtoUpdating.getEmail());

        // responsibility
        doReturn(Optional.empty()).when(categoryRepository).findById(userDtoUpdating.getResponsibility().getId());

        RequestException ex = assertThrows(RequestException.class, () -> userService.updateUser(mockedToken, userId, userDtoUpdating));

        assertEquals(ex.getHttpStatus(), HttpStatus.NOT_FOUND);
        assertEquals(ex.getMessage(), String.format(Messages.Error.CATEGORY_NOT_FOUND, userDtoUpdating.getResponsibility().getId()));

        verify(userRepository, times(2)).findById(any());
        verify(userRepository, atMostOnce()).findByEmail(any());
        verify(volunteerRepository, atMostOnce()).findById(any());
        verify(userRepository, atMostOnce()).findByVolunteer_Id(any());
        verify(categoryRepository, atMostOnce()).findById(any());

    }

    @Test
    public void whenUpdateUserWithoutPreviousGroup_returnGroupNotFound() throws ParseException {
        // user which calls endpoint
        final User userToken = UserMapper.MAPPER.toEntity(MANAGER_USER);
        final Integer userIdFromToken = MANAGER_USER.getId();
        // user entity which will be updated
        final Integer userId = STANDARD_USER.getId();
        final User userEntityUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER);
        final User checkUserUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER);
        // group
        final GroupDto groupDto = GroupDto.builder().id(0).build();
        // user details to update the entity
        final UserDto userDtoUpdating = STANDARD_USER.toBuilder().role(ERole.ROLE_MANAGER).group(groupDto).build();

        doReturn(userIdFromToken).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userToken)).when(userRepository).findById(userIdFromToken);
        doReturn(Optional.of(userEntityUpdating)).when(userRepository).findById(userId);
        doReturn(Optional.of(checkUserUpdating)).when(userRepository).findByEmail(userDtoUpdating.getEmail());

        // responsibility
        doReturn(Optional.empty()).when(groupRepository).findById(userDtoUpdating.getGroup().getId());

        RequestException ex = assertThrows(RequestException.class, () -> userService.updateUser(mockedToken, userId, userDtoUpdating));

        assertEquals(ex.getHttpStatus(), HttpStatus.NOT_FOUND);
        assertEquals(ex.getMessage(), String.format(Messages.Error.GROUP_NOT_FOUND, userDtoUpdating.getGroup().getId()));

        verify(userRepository, times(2)).findById(any());
        verify(userRepository, atMostOnce()).findByEmail(any());
        verify(volunteerRepository, atMostOnce()).findById(any());
        verify(userRepository, atMostOnce()).findByVolunteer_Id(any());
        verify(categoryRepository, atMostOnce()).findById(any());
    }

    @Test
    public void whenUpdateUserWithDifferentGroup_returnGroupNotFound() throws ParseException {
        // user which calls endpoint
        final User userToken = UserMapper.MAPPER.toEntity(MANAGER_USER);
        final Integer userIdFromToken = MANAGER_USER.getId();
        // group
        final GroupDto groupDtoOrigin = GroupDto.builder().id(0).build();
        final GroupDto groupDtoUpdating = GroupDto.builder().id(1).build();
        // user entity which will be updated
        final Integer userId = STANDARD_USER.getId();
        final User userEntityUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER.toBuilder().group(groupDtoOrigin).build());
        final User checkUserUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER);
        // user details to update the entity
        final UserDto userDtoUpdating = STANDARD_USER.toBuilder().role(ERole.ROLE_MANAGER).group(groupDtoUpdating).build();

        doReturn(userIdFromToken).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userToken)).when(userRepository).findById(userIdFromToken);
        doReturn(Optional.of(userEntityUpdating)).when(userRepository).findById(userId);
        doReturn(Optional.of(checkUserUpdating)).when(userRepository).findByEmail(userDtoUpdating.getEmail());

        // responsibility
        doReturn(Optional.empty()).when(groupRepository).findById(userDtoUpdating.getGroup().getId());

        RequestException ex = assertThrows(RequestException.class, () -> userService.updateUser(mockedToken, userId, userDtoUpdating));

        assertEquals(ex.getHttpStatus(), HttpStatus.NOT_FOUND);
        assertEquals(ex.getMessage(), String.format(Messages.Error.GROUP_NOT_FOUND, userDtoUpdating.getGroup().getId()));

        verify(userRepository, times(2)).findById(any());
        verify(userRepository, atMostOnce()).findByEmail(any());
        verify(volunteerRepository, atMostOnce()).findById(any());
        verify(userRepository, atMostOnce()).findByVolunteer_Id(any());
        verify(categoryRepository, atMostOnce()).findById(any());
    }

    @Test
    public void whenUpdateUser_returnUpdatedUser() throws ParseException, JOSEException {
        // user which calls endpoint
        final User userToken = UserMapper.MAPPER.toEntity(MANAGER_USER);
        final Integer userIdFromToken = MANAGER_USER.getId();
        // volunteer origin
        final VolunteerDto volunteerDtoOrigin = VolunteerDto.builder().id(0).build();
        // user entity which will be updated
        final Integer userId = STANDARD_USER.getId();
        final User userEntityUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER.toBuilder().volunteer(volunteerDtoOrigin).build());
        final User checkUserUpdating = UserMapper.MAPPER.toEntity(STANDARD_USER);
        // volunteer + responsibility + group
        final VolunteerDto volunteerDtoUpdating = VolunteerDto.builder().id(1).build();
        final Volunteer volunteer = VolunteerMapper.MAPPER.toEntity(volunteerDtoUpdating);
        final CategoryDto responsibilityDto = CategoryDto.builder().id(0).build();
        final Category responsibility = CategoryMapper.MAPPER.toMo(responsibilityDto);
        final GroupDto groupDto = GroupDto.builder().id(0).build();
        final Group group = GroupMapper.MAPPER.toMo(groupDto);
        // user details to update the entity
        final UserDto userDtoUpdating = STANDARD_USER.toBuilder().role(ERole.ROLE_MANAGER).volunteer(volunteerDtoUpdating).responsibility(responsibilityDto).group(groupDto).build();
        final UserDto userDtoExpected = userDtoUpdating.toBuilder().password(null).build();

        doReturn(userIdFromToken).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userToken)).when(userRepository).findById(userIdFromToken);
        doReturn(Optional.of(userEntityUpdating)).when(userRepository).findById(userId);
        doReturn(Optional.of(checkUserUpdating)).when(userRepository).findByEmail(userDtoUpdating.getEmail());

        // volunteers
        doReturn(Optional.of(volunteer)).when(volunteerRepository).findById(userDtoUpdating.getVolunteer().getId());
        doReturn(Optional.empty()).when(userRepository).findByVolunteer_Id(userDtoUpdating.getVolunteer().getId());

        // responsibility
        doReturn(Optional.of(responsibility)).when(categoryRepository).findById(userDtoUpdating.getResponsibility().getId());

        // group
        doReturn(Optional.of(group)).when(groupRepository).findById(userDtoUpdating.getGroup().getId());

        mockedSignedJWT = generateNewToken(UserMapper.MAPPER.toEntity(getRandomMockedUserDto(ERole.ROLE_USER)));

        HttpHeaders headers = new HttpHeaders();
        final String headerPayLoad = String.format("%s.%s", mockedSignedJWT.getParsedParts()[0], mockedSignedJWT.getParsedParts()[1]);
        headers.add("x-header-payload-token", headerPayLoad);
        final String signature = mockedSignedJWT.getParsedParts()[2].toString();
        headers.add("x-signature-token", signature);

        UserCredentialsDto credentials = UserCredentialsDto.builder()
            .email(userDtoUpdating.getEmail()).password(userDtoUpdating.getPassword()).build();

        doReturn(Constructor.buildResponseObjectHeader(HttpStatus.OK, userDtoExpected, headers)).when(accountService).login(credentials);

        ResponseEntity<?> response = userService.updateUser(mockedToken, userId, userDtoUpdating);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        Response.DTO responseData = (Response.DTO) responseBody.getData();
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(responseBody.getMessage(), Messages.Info.USER_UPDATED);
        assertEquals(response.getHeaders().size(), 2);
        assertTrue(response.getHeaders().containsKey("x-header-payload-token"));
        assertEquals(response.getHeaders().get("x-header-payload-token").get(0), headerPayLoad);
        assertTrue(response.getHeaders().containsKey("x-signature-token"), signature);
        assertEquals(response.getHeaders().get("x-signature-token").get(0), signature);
        assertEquals(responseData.getData(), userDtoExpected);

        verify(userRepository, times(2)).findById(any());
        verify(userRepository, atMostOnce()).findByEmail(any());
        verify(volunteerRepository, atMostOnce()).findById(any());
        verify(categoryRepository, atMostOnce()).findById(any());
        verify(groupRepository, atMostOnce()).findById(any());
        verify(userRepository, atMostOnce()).save(any());
        verify(tokenRepository, atMostOnce()).deleteAllTokensFromUser(any());
        verify(tokenRepository, atMostOnce()).save(any());

    }

    // delete user
    @Test
    public void whenDeleteUser_returnUserNotFound() {
        final UserDto user = MANAGER_USER;

        doReturn(false).when(userRepository).existsById(user.getId());

        RequestException ex = assertThrows(RequestException.class, () -> userService.deleteUser(user.getId()));

        assertEquals(ex.getHttpStatus(), HttpStatus.NOT_FOUND);
        assertEquals(ex.getMessage(), Messages.Error.USER_NOT_FOUND);

        verify(userRepository, atMostOnce()).existsById(any());
    }

    @Test
    public void whenDeleteUser_returnOK() {
        final UserDto user = MANAGER_USER;

        doReturn(true).when(userRepository).existsById(user.getId());

        ResponseEntity<?> response = userService.deleteUser(user.getId());
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody.getMessage(), Messages.Info.USER_DELETED);

        verify(userRepository, atMostOnce()).existsById(any());
        verify(userRepository, atMostOnce()).deleteById(any());
        verify(tokenRepository, atMostOnce()).deleteAllTokensFromUser(any());
    }

}
