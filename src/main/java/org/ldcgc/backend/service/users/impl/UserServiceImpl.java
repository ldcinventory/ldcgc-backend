package org.ldcgc.backend.service.users.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.model.category.Responsibility;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.category.ResponsibilityRepository;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.ResponsibilityDto;
import org.ldcgc.backend.payload.dto.other.PaginationDetails;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.AccountService;
import org.ldcgc.backend.service.users.UserService;
import org.ldcgc.backend.util.common.EOrder;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.common.EVStatus;
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

import java.text.ParseException;
import java.util.Objects;
import java.util.Optional;

import static org.ldcgc.backend.security.jwt.JwtUtils.cleanLocalTokensFromUserId;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VolunteerRepository volunteerRepository;
    private final ResponsibilityRepository responsibilityRepository;
    private final GroupRepository groupRepository;
    private final TokenRepository tokenRepository;
    private final AccountService accountService;
    private final JwtUtils jwtUtils;

    public ResponseEntity<?> getMyUser(String token) throws ParseException {
        Integer userId = validateUserEnabledAndGetUserId(token);

        return getUser(userId);
    }

    public ResponseEntity<?> updateMyUser(String token, UserDto userDto) throws ParseException, JOSEException {
        Integer userId = validateUserEnabledAndGetUserId(token);

        User userEntity = getUserFromUserId(userId);

        return updateUser(userEntity, userEntity, userDto);
    }

    public ResponseEntity<?> deleteMyUser(String token) throws ParseException {
        Integer userId = validateUserEnabledAndGetUserId(token);

        return deleteUser(userId);
    }

    private Integer validateUserEnabledAndGetUserId(String token) throws ParseException {
        SignedJWT signedJwt = jwtUtils.getDecodedJwt(token);
        Integer userId = jwtUtils.getUserIdFromJwtToken(signedJwt);

        if (Boolean.FALSE.equals(userRepository.userIsEnabled(userId))) {
            tokenRepository.deleteAllTokensFromUser(userId);
            cleanLocalTokensFromUserId(userId, true);
            throw new RequestException(HttpStatus.UNAUTHORIZED, Messages.Error.USER_NOT_ENABLED);
        }
        String publicKey = jwtUtils.getDecodedJwt(token).getHeader().getKeyID();

        return tokenRepository.getUserIdFromJwtId(publicKey).orElseThrow(()
            -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND_TOKEN));
    }

    public ResponseEntity<?> createUser(String token, UserDto userDto) {
        if(userRepository.findByEmail(userDto.getEmail()).isPresent())
            throw new RequestException(HttpStatus.CONFLICT, Messages.Error.USER_ALREADY_EXIST);

        User userEntity = UserMapper.MAPPER.toEntity(userDto);

        setVolunteer(userEntity, userDto);

        setCurrentGroup(userEntity);

        userEntity = userRepository.saveAndFlush(userEntity);

        String warningIfVolunteerNotEnabled = userEntity.getVolunteer().getStatus().equals(EVStatus.ACTIVE)
            ? ""
            : ". " + String.format(Messages.Warning.USER_LINKED_VOLUNTEER_NOT_ACTIVE, userEntity.getVolunteer().getBuilderAssistantId());

        return Constructor.buildResponseMessageObject(
            HttpStatus.CREATED,
            Messages.Info.USER_CREATED + warningIfVolunteerNotEnabled,
            UserMapper.MAPPER.toDTO(userEntity));
    }

    public ResponseEntity<?> getUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

        return Constructor.buildResponseObject(HttpStatus.OK, UserMapper.MAPPER.toDTO(user));
    }

    public ResponseEntity<?> listUsers(String filterString, Integer userId, Boolean enabled, Integer pageIndex, Integer size, String sortField, EOrder order) {
        if (userId != null) return getUser(userId);

        Pageable pageable = PageRequest.of(pageIndex, size, order.equals(EOrder.DESC)
            ? Sort.by(sortField).descending()
            : Sort.by(sortField).ascending());
        Page<UserDto> pagedUsers = StringUtils.isBlank(filterString) && enabled == null ?
            userRepository.findAll(pageable).map(UserMapper.MAPPER::toDTO) :
            userRepository.findAllFiltered(filterString, enabled, pageable).map(UserMapper.MAPPER::toDTO);

        return Constructor.buildResponseMessageObject(
            HttpStatus.OK,
            String.format(Messages.Info.USER_LISTED, pagedUsers.getTotalElements()),
            PaginationDetails.fromPaging(pageable, pagedUsers));

    }

    public ResponseEntity<?> updateUser(String token, Integer userId, UserDto userDto) throws ParseException, JOSEException {
        User userFromToken = userRepository.findById(jwtUtils.getUserIdFromStringToken(token))
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

        return updateUser(userFromToken, getUserFromUserId(userId), userDto);
    }

    public ResponseEntity<?> linkUserToVolunteer(Integer userId, String builderAssistantId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

        if(!user.isEnabled())
            throw new RequestException(HttpStatus.FORBIDDEN, Messages.Error.USER_PROHIBITED);

        if(user.getVolunteer() != null && user.getVolunteer().getBuilderAssistantId().equals(builderAssistantId))
            return Constructor.buildResponseMessageObject(HttpStatus.OK, Messages.Info.NO_CHANGES_PROCESSED, UserMapper.MAPPER.toDTO(user));

        Volunteer volunteer = volunteerRepository.findByBuilderAssistantId(builderAssistantId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));

        if(userRepository.findByVolunteerBAId(builderAssistantId).isPresent())
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.VOLUNTEER_ALREADY_LINKED);

        user.setVolunteer(volunteer);
        user = userRepository.saveAndFlush(user);

        return Constructor.buildResponseMessageObject(
            HttpStatus.CREATED,
            String.format(volunteer.getStatus().equals(EVStatus.ACTIVE)
                ? Messages.Info.USER_LINKED
                : Messages.Warning.USER_LINKED_VOLUNTEER_NOT_ACTIVE, builderAssistantId),
            UserMapper.MAPPER.toDTO(user));
    }

    public ResponseEntity<?> unlinkUserToVolunteer(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

        if(user.getVolunteer() == null)
            return Constructor.buildResponseMessageObject(HttpStatus.OK, Messages.Info.NO_CHANGES_PROCESSED, UserMapper.MAPPER.toDTO(user));

        String builderAssistantId = user.getVolunteer().getBuilderAssistantId();

        user.setVolunteer(null);
        user = userRepository.saveAndFlush(user);

        return Constructor.buildResponseMessageObject(
            HttpStatus.OK,
            String.format(Messages.Info.USER_UNLINKED, builderAssistantId),
            UserMapper.MAPPER.toDTO(user));
    }

    private ResponseEntity<?> updateUser(User userFromToken, User userEntity, UserDto userDto) throws ParseException, JOSEException {
        validateUpdatingParameters(userFromToken, userEntity, userDto);

        // origin objects
        final Optional<Integer> originVolunteerId = Optional.ofNullable(userEntity.getVolunteer()).map(Volunteer::getId);
        final Optional<Integer> originResponsibilityId = Optional.ofNullable(userEntity.getResponsibility()).map(Responsibility::getId);
        final Optional<Integer> originGroupId = Optional.ofNullable(userEntity.getGroup()).map(Group::getId);

        // map the whole User with password encoded
        UserMapper.MAPPER.update(userDto, userEntity);

        // volunteers
        // check if dto comes with volunteer
        String warningIfVolunteerNotEnabled = "";
        if(Optional.ofNullable(userDto.getVolunteer()).map(VolunteerDto::getId).isPresent() &&
            // check if origin (entity) is null and dto is not
            (originVolunteerId.isEmpty() ||
                // check origin (entity) and dto are not the same
                !originVolunteerId.get().equals(userDto.getVolunteer().getId()))) {
            // check this builder assistant id is not assigned to another volunteer
            User finalUserEntity = userEntity;
            userRepository.findByVolunteer_Id(userDto.getVolunteer().getId()).ifPresent(checkUser -> {
                if(!checkUser.getId().equals(finalUserEntity.getId()))
                    throw new RequestException(HttpStatus.FORBIDDEN, Messages.Error.USER_VOLUNTEER_ALREADY_ASSIGNED);
            });

            setVolunteer(userEntity, userDto);

            if(!userEntity.getVolunteer().getStatus().equals(EVStatus.ACTIVE))
                warningIfVolunteerNotEnabled = ". " + String.format(Messages.Warning.USER_LINKED_VOLUNTEER_NOT_ACTIVE, userEntity.getVolunteer().getBuilderAssistantId());
        }

        // responsibility
        // check if dto comes with responsibility
        if(Optional.ofNullable(userDto.getResponsibility()).map(ResponsibilityDto::getId).isPresent() &&
            // check if origin (entity) is null and dto is not
            (originResponsibilityId.isEmpty() ||
                // check origin (entity) and dto are not the same
                !originResponsibilityId.get().equals(userDto.getResponsibility().getId()))) {
            Responsibility responsibility = responsibilityRepository.findById(userDto.getResponsibility().getId()).orElseThrow(
                () -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.RESOURCE_TYPE_NOT_FOUND, userDto.getResponsibility().getId())));
            setResponsibility(userEntity, userDto.getResponsibility().getId());
        }

        // group
        setCurrentGroup(userEntity);

        userEntity = userRepository.saveAndFlush(userEntity);

        // when modifying my user, return new token
        if(userFromToken.getId().equals(userEntity.getId())) {
            cleanLocalTokensFromUserId(userEntity.getId(), true);
            tokenRepository.deleteAllTokensFromUser(userEntity.getId());
            ResponseEntity<?> response = accountService.login(userEntity);
            Response.DTO responseBody = (Response.DTO) response.getBody();
            return Constructor.buildResponseMessageObjectHeader(
                HttpStatus.CREATED,
                Messages.Info.USER_UPDATED + warningIfVolunteerNotEnabled,
                Objects.requireNonNull(responseBody).getData(),
                response.getHeaders());
        }

        // when modifying other user, it's not necessary to re-login
        return Constructor.buildResponseMessageObject(
            HttpStatus.CREATED,
            Messages.Info.USER_UPDATED + warningIfVolunteerNotEnabled,
            UserMapper.MAPPER.toDTO(userEntity));
    }

    public ResponseEntity<?> deleteUser(Integer userId) {
        if (!userRepository.existsById(userId))
            throw new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND);

        userRepository.deleteById(userId);
        cleanLocalTokensFromUserId(userId, true);
        tokenRepository.deleteAllTokensFromUser(userId);

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.USER_DELETED);
    }

    private User getUserFromUserId(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));
    }

    private void validateUpdatingParameters(User userFromToken, User userEntity, UserDto userDto) {
        // check if the email to update is owned by other user
        userRepository.findByEmail(userDto.getEmail()).ifPresent(checkUser -> {
            if(!checkUser.getId().equals(userEntity.getId()))
                throw new RequestException(HttpStatus.CONFLICT, Messages.Error.USER_ALREADY_EXIST);
        });

        // when updating self user and change self role
        // -> do not allow to change role
        // TODO allow change if new status is active, but *never* for null user
        if(userFromToken.getEmail().equals(userEntity.getEmail())
            && userDto.getRole() != null && !userEntity.getRole().equals(userDto.getRole()))
            throw new RequestException(HttpStatus.FORBIDDEN, Messages.Error.USER_PERMISSION_ROLE);

        // when updating an admin user being manager
        // -> do not allow to change an admin user or become someone admin
        if(userFromToken.getRole().equals(ERole.ROLE_MANAGER)) {
            if(userEntity.getRole().equals(ERole.ROLE_ADMIN))
                throw new RequestException(HttpStatus.FORBIDDEN, Messages.Error.USER_PERMISSION_OTHER);
            if(userDto.getRole().equals(ERole.ROLE_ADMIN))
                throw new RequestException(HttpStatus.FORBIDDEN, Messages.Error.USER_PERMISSION_ROLE_OTHER);
        }

    }

    private void setCurrentGroup(User userEntity) {
        Integer groupId = Integer.valueOf(ObjectUtils.defaultIfNull(MDC.get("groupId"), "-1"));
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.GROUP_NOT_FOUND, groupId)));
        userEntity.setGroup(group);
    }

    private void setResponsibility(User userEntity, Integer responsibilityId) {
        Responsibility responsibility = responsibilityRepository.findById(responsibilityId).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.RESOURCE_TYPE_NOT_FOUND, responsibilityId)));
        userEntity.setResponsibility(responsibility);
    }

    private void setVolunteer(User userEntity, UserDto userDto) {
        if(Optional.ofNullable(userDto.getVolunteer()).map(VolunteerDto::getId).isPresent()){
            Volunteer volunteer = volunteerRepository.findById(userDto.getVolunteer().getId())
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));

            User checkUser = userRepository.findByVolunteer_Id(userDto.getVolunteer().getId()).orElse(null);
            if(checkUser != null && !checkUser.getId().equals(userEntity.getId()))
                throw new RequestException(HttpStatus.CONFLICT, Messages.Error.VOLUNTEER_ALREADY_LINKED);

            userEntity.setVolunteer(volunteer);

            return;
        }

        if(Optional.ofNullable(userDto.getVolunteer()).map(VolunteerDto::getBuilderAssistantId).isPresent()){
            Volunteer volunteer = volunteerRepository.findByBuilderAssistantId(userDto.getVolunteer().getBuilderAssistantId())
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));

            User checkUser = userRepository.findByVolunteer_Id(userDto.getVolunteer().getId()).orElse(null);
            if(checkUser != null && !checkUser.getId().equals(userEntity.getId()))
                throw new RequestException(HttpStatus.CONFLICT, Messages.Error.VOLUNTEER_ALREADY_LINKED);

            userEntity.setVolunteer(volunteer);
        }
    }

}
