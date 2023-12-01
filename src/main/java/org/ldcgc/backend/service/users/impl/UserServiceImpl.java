package org.ldcgc.backend.service.users.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.UserService;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.mock.UserMock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.USER_ALREADY_EXIST;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.USER_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.USER_NOT_FOUND_TOKEN;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.USER_CREATED;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.USER_DELETED;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.USER_LISTED;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.USER_UPDATED;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;
import static org.ldcgc.backend.util.retrieving.Message.getInfoMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtils jwtUtils;

    public ResponseEntity<?> getMyUser(String token) {
        String publicKey = jwtUtils.getDecodedJwt(token).getHeader().getKeyID();

        Integer userId = tokenRepository.getUserIdFromJwtId(publicKey).orElseThrow(()
            -> new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(USER_NOT_FOUND_TOKEN)));

        return getUser(userId);
    }

    public ResponseEntity<?> updateMyUser(String token, UserDto user) throws ParseException {

        Integer userId = jwtUtils.getUserIdFromStringToken(token);

        User userEntity = userRepository.findById(userId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(USER_NOT_FOUND)));

        UserMapper.MAPPER.update(user, userEntity);

        userRepository.saveAndFlush(userEntity);

        return Constructor.buildResponseMessage(HttpStatus.OK, getInfoMessage(USER_UPDATED));
    }

    public ResponseEntity<?> deleteMyUser(String token) throws ParseException {

        Integer userId = jwtUtils.getUserIdFromStringToken(token);

        userRepository.deleteById(userId);
        tokenRepository.deleteAllTokensFromUser(userId);

        return Constructor.buildResponseMessage(HttpStatus.OK, getInfoMessage(USER_DELETED));
    }

    public ResponseEntity<?> createUser(UserDto user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent())
            throw new RequestException(HttpStatus.CONFLICT, getErrorMessage(USER_ALREADY_EXIST));

        User userEntity = UserMapper.MAPPER.toEntity(user);
        userEntity = userRepository.save(userEntity);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, getInfoMessage(USER_CREATED), UserMapper.MAPPER.toDTO(userEntity));
    }

    public ResponseEntity<?> getUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(USER_NOT_FOUND)));

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
            String.format(getInfoMessage(USER_LISTED), pageUsers.getTotalElements()),
            userList.stream().map(UserMapper.MAPPER::toDTO).toList());

    }

    public ResponseEntity<?> updateUser(Integer userId, UserDto user) {
        if (!userRepository.existsById(userId))
            throw new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(USER_NOT_FOUND));

        return Constructor.buildResponseMessageObject(HttpStatus.OK, getInfoMessage(USER_UPDATED), UserMock.getMockedUser(userId));
    }

    public ResponseEntity<?> deleteUser(Integer userId) {
        if (!userRepository.existsById(userId))
            throw new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(USER_NOT_FOUND));

        userRepository.deleteById(userId);

        return Constructor.buildResponseMessage(HttpStatus.OK, getInfoMessage(USER_DELETED));
    }

}
