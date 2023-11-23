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
import org.ldcgc.backend.service.users.UserService;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.mock.UserMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public ResponseEntity<?> getMyUser(String token) {

        String publicKey = token;

        Integer userId = 1; //tokenRepository.getUserIdFromPublicKey(publicKey).orElse(1);

        /*
        Integer userId = tokenRepository.getUserIdFromPublicKey(publicKey).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, getErrorMessage(USER_NOT_FOUND_TOKEN))
        );

         */
        // TODO
        //JwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey("secret".getBytes(StandardCharsets.UTF_8)).build();
        //JwtClaimsSet claims = jwtDecoder.decode(token);

        return getUser(userId);
    }

    public ResponseEntity<?> updateMyUser(String token, UserDto user) {
        // TODO
        return updateUser(1, user);
    }

    public ResponseEntity<?> deleteMyUser(String token) {
        // TODO
        return deleteUser(1);
    }

    public ResponseEntity<?> createUser(UserDto user) {
        User userEntity = UserMapper.MAPPER.toEntity(user);
        userEntity = userRepository.save(userEntity);

        return Constructor.buildResponseMessageObject(HttpStatus.OK, getInfoMessage(USER_CREATED), UserMapper.MAPPER.toDTO(userEntity));
    }

    public ResponseEntity<?> getUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(USER_NOT_FOUND)));

        return Constructor.buildResponseObject(HttpStatus.OK, UserMapper.MAPPER.toDTO(user));
    }

    public ResponseEntity<?> listUsers(Integer pageIndex, Integer sizeIndex, String filterString, Integer userId) {
        if (userId != null)
            return Constructor.buildResponseMessageObject(HttpStatus.OK, String.format(getInfoMessage(USER_LISTED), 1), UserMock.getMockedUser(userId));

        if (StringUtils.isNotEmpty(filterString)) {
            List<UserDto> users = UserMock.getListOfMockedUsers(100).stream()
                .filter(user -> StringUtils.containsIgnoreCase(user.getVolunteer().getName(), filterString) ||
                    StringUtils.containsIgnoreCase(user.getVolunteer().getLastName(), filterString) ||
                    StringUtils.containsIgnoreCase(user.getEmail(), filterString))
                .toList();
            return Constructor.buildResponseMessageObject(HttpStatus.OK, String.format(getInfoMessage(USER_LISTED), users.size()), users);
        }


        return Constructor.buildResponseMessageObject(HttpStatus.OK, String.format(getInfoMessage(USER_LISTED), sizeIndex), UserMock.getListOfMockedUsers(sizeIndex));
    }

    public ResponseEntity<?> updateUser(Integer userId, UserDto user) {
        if (!userRepository.existsById(userId))
            throw new RequestException(HttpStatus.BAD_REQUEST, getErrorMessage(USER_NOT_FOUND_TOKEN));

        return Constructor.buildResponseMessageObject(HttpStatus.OK, getInfoMessage(USER_UPDATED), UserMock.getMockedUser(userId));
    }

    public ResponseEntity<?> deleteUser(Integer userId) {
        if (!userRepository.existsById(userId))
            throw new RequestException(HttpStatus.BAD_REQUEST, getErrorMessage(USER_NOT_FOUND_TOKEN));

        userRepository.deleteById(userId);

        return Constructor.buildResponseMessage(HttpStatus.OK, getInfoMessage(USER_DELETED));
    }

    public Integer getUserIdFromToken(String token) {
        return null;
    }

}
