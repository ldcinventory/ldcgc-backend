package org.ldcgc.backend.service.users.impl;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.AccountService;
import org.ldcgc.backend.service.users.UserService;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VolunteerRepository volunteerRepository;
    private final CategoryRepository categoryRepository;
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
        userEntity = userRepository.save(userEntity);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.USER_CREATED, UserMapper.MAPPER.toDTO(userEntity));
    }

    public ResponseEntity<?> getUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

        return Constructor.buildResponseObject(HttpStatus.OK, UserMapper.MAPPER.toDTO(user));
    }

    public ResponseEntity<?> listUsers(Integer pageIndex, Integer size, String filterString, Integer userId) {
        if (userId != null) return getUser(userId);

        Pageable paging = PageRequest.of(pageIndex, size);
        Page<User> pageUsers = StringUtils.isBlank(filterString) ?
            userRepository.findAll(paging) :
            userRepository.findAllFiltered(filterString, paging);

        List<User> userList = pageUsers.getContent();

        return Constructor.buildResponseMessageObject(
            HttpStatus.OK,
            String.format(Messages.Info.USER_LISTED, pageUsers.getTotalElements()),
            userList.stream().map(UserMapper.MAPPER::toDTO).toList());

    }

    public ResponseEntity<?> updateUser(String token, Integer userId, UserDto userDto) throws ParseException, JOSEException {
        User userFromToken = userRepository.findById(jwtUtils.getUserIdFromStringToken(token))
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

        return updateUser(userFromToken, getUserFromUserId(userId), userDto);
    }

    private ResponseEntity<?> updateUser(User userFromToken, User userEntity, UserDto userDto) throws ParseException, JOSEException {
        validateUpdatingParameters(userFromToken, userEntity, userDto);

        // origin objects
        final Integer originVolunteerId = Optional.ofNullable(userEntity.getVolunteer()).map(Volunteer::getId).orElse(null);
        final Integer originResponsibilityId = Optional.ofNullable(userEntity.getResponsibility()).map(Category::getId).orElse(null);
        final Integer originGroupId = Optional.ofNullable(userEntity.getGroup()).map(Group::getId).orElse(null);

        // map the whole User with password encoded
        UserMapper.MAPPER.update(userDto, userEntity);

        // volunteers
        // check if dto comes with volunteer
        if(Optional.ofNullable(userDto.getVolunteer()).map(VolunteerDto::getId).isPresent() &&
            // check if origin (entity) is null and dto is not
            (originVolunteerId == null ||
                // check origin (entity) and dto are not the same
                !originVolunteerId.equals(userDto.getVolunteer().getId()))) {
            Volunteer volunteer = volunteerRepository.findById(userDto.getVolunteer().getId()).orElse(null);
            if(volunteer == null)
                throw new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND);
            User checkUserVolunteer = userRepository.findByVolunteer_Id(userDto.getVolunteer().getId()).orElse(null);
            if(checkUserVolunteer != null && !checkUserVolunteer.getId().equals(userEntity.getId()))
                throw new RequestException(HttpStatus.FORBIDDEN, Messages.Error.USER_VOLUNTEER_ALREADY_ASSIGNED);
            userEntity.setVolunteer(volunteer);
        }

        // responsibility
        // check if dto comes with responsibility
        if(Optional.ofNullable(userDto.getResponsibility()).map(CategoryDto::getId).isPresent() &&
            // check if origin (entity) is null and dto is not
            (originResponsibilityId == null ||
                // check origin (entity) and dto are not the same
                !originResponsibilityId.equals(userDto.getResponsibility().getId()))) {
            Category category = categoryRepository.findById(userDto.getResponsibility().getId()).orElse(null);
            if(category == null)
                throw new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.CATEGORY_NOT_FOUND, userDto.getResponsibility().getId()));
            userEntity.setResponsibility(category);
        }

        // group
        // check if dto comes with group
        if(Optional.ofNullable(userDto.getGroup()).map(GroupDto::getId).isPresent() &&
            // check if origin (entity) is null and dto is not
            (originGroupId == null ||
                // check origin (entity) and dto are not the same
                !originGroupId.equals(userDto.getGroup().getId()))) {
            Group group = groupRepository.findById(userDto.getGroup().getId()).orElse(null);
            if(group == null)
                throw new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.GROUP_NOT_FOUND, userDto.getGroup().getId()));
            userEntity.setGroup(group);
        }

        userRepository.save(userEntity);

        UserCredentialsDto credentials = UserCredentialsDto.builder()
            .email(userDto.getEmail()).password(userDto.getPassword()).build();

        // when modifying my user, return new token
        tokenRepository.deleteAllTokensFromUser(userEntity.getId());
        ResponseEntity<?> response = accountService.login(credentials);
        return Constructor.buildResponseMessageObjectHeader(HttpStatus.CREATED, Messages.Info.USER_UPDATED, response.getBody(), response.getHeaders());
    }

    public ResponseEntity<?> deleteUser(Integer userId) {
        if (!userRepository.existsById(userId))
            throw new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND);

        userRepository.deleteById(userId);
        tokenRepository.deleteAllTokensFromUser(userId);

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.USER_DELETED);
    }

    private User getUserFromUserId(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));
    }

    private void validateUpdatingParameters(User userFromToken, User userEntity, UserDto userDto) {
        // check if the email to update is owned by other user
        User checkUser = userRepository.findByEmail(userDto.getEmail()).orElse(null);
        if(checkUser != null && !checkUser.getId().equals(userEntity.getId()))
            throw new RequestException(HttpStatus.CONFLICT, Messages.Error.USER_ALREADY_EXIST);

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
