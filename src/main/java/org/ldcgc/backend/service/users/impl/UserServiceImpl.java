package org.ldcgc.backend.service.users.impl;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
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
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.other.PaginationDetails;
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.AccountService;
import org.ldcgc.backend.service.users.UserService;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.ParseException;
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

    public ResponseEntity<?> getMyUser(String token) {
        String publicKey = jwtUtils.getDecodedJwt(token).getHeader().getKeyID();

        Integer userId = tokenRepository.getUserIdFromJwtId(publicKey).orElseThrow(()
            -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND_TOKEN));

        return getUser(userId);
    }

    public ResponseEntity<?> updateMyUser(String token, UserDto userDto) throws ParseException, JOSEException {
        String publicKey = jwtUtils.getDecodedJwt(token).getHeader().getKeyID();

        Integer userId = tokenRepository.getUserIdFromJwtId(publicKey).orElseThrow(()
            -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND_TOKEN));

        User userEntity = getUserFromUserId(userId);

        return updateUser(userEntity, userEntity, userDto);
    }

    public ResponseEntity<?> deleteMyUser(String token) {
        String publicKey = jwtUtils.getDecodedJwt(token).getHeader().getKeyID();

        Integer userId = tokenRepository.getUserIdFromJwtId(publicKey).orElseThrow(()
            -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND_TOKEN));

        return deleteUser(userId);
    }

    public ResponseEntity<?> createUser(String token, UserDto user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent())
            throw new RequestException(HttpStatus.CONFLICT, Messages.Error.USER_ALREADY_EXIST);

        User userEntity = UserMapper.MAPPER.toEntity(user);
        userEntity = userRepository.saveAndFlush(userEntity);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.USER_CREATED, UserMapper.MAPPER.toDTO(userEntity));
    }

    public ResponseEntity<?> getUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

        return Constructor.buildResponseObject(HttpStatus.OK, UserMapper.MAPPER.toDTO(user));
    }

    public ResponseEntity<?> listUsers(Integer pageIndex, Integer size, String filterString, Integer userId) {
        if (userId != null) return getUser(userId);

        Pageable pageable = PageRequest.of(pageIndex, size);
        Page<UserDto> pagedUsers = StringUtils.isBlank(filterString) ?
            userRepository.findAll(pageable).map(UserMapper.MAPPER::toDTO) :
            userRepository.findAllFiltered(filterString, pageable).map(UserMapper.MAPPER::toDTO);

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
        if(Optional.ofNullable(userDto.getVolunteer()).map(VolunteerDto::getId).isPresent() &&
            // check if origin (entity) is null and dto is not
            (originVolunteerId.isEmpty() ||
                // check origin (entity) and dto are not the same
                !originVolunteerId.get().equals(userDto.getVolunteer().getId()))) {
            Volunteer volunteer = volunteerRepository.findById(userDto.getVolunteer().getId()).orElseThrow(
                () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));
            // check this builder assistant id is not assigned to another volunteer
            userRepository.findByVolunteer_Id(userDto.getVolunteer().getId()).ifPresent(checkUser -> {
                if(!checkUser.getId().equals(userEntity.getId()))
                    throw new RequestException(HttpStatus.FORBIDDEN, Messages.Error.USER_VOLUNTEER_ALREADY_ASSIGNED);
            });

            userEntity.setVolunteer(volunteer);
        }

        // responsibility
        // check if dto comes with responsibility
        if(Optional.ofNullable(userDto.getResponsibility()).map(ResponsibilityDto::getId).isPresent() &&
            // check if origin (entity) is null and dto is not
            (originResponsibilityId.isEmpty() ||
                // check origin (entity) and dto are not the same
                !originResponsibilityId.get().equals(userDto.getResponsibility().getId()))) {
            Responsibility responsibility = responsibilityRepository.findById(userDto.getResponsibility().getId()).orElseThrow(
                () -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.CATEGORY_NOT_FOUND, userDto.getResponsibility().getId())));
            userEntity.setResponsibility(responsibility);
        }

        // group
        // check if dto comes with group
        if(Optional.ofNullable(userDto.getGroup()).map(GroupDto::getId).isPresent() &&
            // check if origin (entity) is null and dto is not
            (originGroupId.isEmpty() ||
                // check origin (entity) and dto are not the same
                !originGroupId.get().equals(userDto.getGroup().getId()))) {
            Group group = groupRepository.findById(userDto.getGroup().getId()).orElseThrow(
                () -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.GROUP_NOT_FOUND, userDto.getGroup().getId())));
            userEntity.setGroup(group);
        }

        userRepository.saveAndFlush(userEntity);

        UserCredentialsDto credentials = UserCredentialsDto.builder()
            .email(userDto.getEmail()).password(userDto.getPassword()).build();

        // when modifying my user, return new token
        cleanLocalTokensFromUserId(userEntity.getId(), true);
        tokenRepository.deleteAllTokensFromUser(userEntity.getId());
        ResponseEntity<?> response = accountService.login(credentials);
        return Constructor.buildResponseMessageObjectHeader(HttpStatus.CREATED, Messages.Info.USER_UPDATED, response.getBody(), response.getHeaders());
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
        if(userFromToken.getEmail().equals(userEntity.getEmail())
            && !userEntity.getRole().equals(userDto.getRole()))
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

}
