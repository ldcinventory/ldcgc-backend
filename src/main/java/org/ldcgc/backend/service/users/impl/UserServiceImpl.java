package org.ldcgc.backend.service.users.impl;

import lombok.RequiredArgsConstructor;
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
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtils jwtUtils;

    public ResponseEntity<?> getMyUser(String token) {
        String publicKey = jwtUtils.getDecodedJwt(token).getHeader().getKeyID();

        Integer userId = tokenRepository.getUserIdFromJwtId(publicKey).orElseThrow(()
            -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND_TOKEN));

        return getUser(userId);
    }

    public ResponseEntity<?> updateMyUser(String token, UserDto user) {
        String publicKey = jwtUtils.getDecodedJwt(token).getHeader().getKeyID();

        Integer userId = tokenRepository.getUserIdFromJwtId(publicKey).orElseThrow(()
            -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND_TOKEN));

        User userEntity = getUserFromUserId(userId);

        if(!userEntity.getRole().equals(user.getRole()))
            throw new RequestException(HttpStatus.FORBIDDEN, Messages.Error.USER_PERMISSION_ROLE);

        return updateUser(userEntity, user);
    }

    public ResponseEntity<?> deleteMyUser(String token) {
        String publicKey = jwtUtils.getDecodedJwt(token).getHeader().getKeyID();

        Integer userId = tokenRepository.getUserIdFromJwtId(publicKey).orElseThrow(()
            -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND_TOKEN));

        userRepository.deleteById(userId);
        tokenRepository.deleteAllTokensFromUser(userId);

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.USER_DELETED);
    }

    public ResponseEntity<?> createUser(UserDto user) {
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

    public ResponseEntity<?> updateUser(Integer userId, UserDto user) {
        return updateUser(getUserFromUserId(userId), user);
    }

    private ResponseEntity<?> updateUser(User userEntity, UserDto user) {
        if(userEntity.getEmail().equals(user.getEmail()))
            throw new RequestException(HttpStatus.CONFLICT, Messages.Error.USER_ALREADY_EXIST);

        UserMapper.MAPPER.update(user, userEntity);
        userRepository.save(userEntity);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.USER_UPDATED, UserMapper.MAPPER.toDTO(userEntity));
    }

    public ResponseEntity<?> deleteUser(Integer userId) {
        if (!userRepository.existsById(userId))
            throw new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND);

        userRepository.deleteById(userId);

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.USER_DELETED);
    }

    private User getUserFromUserId(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));
    }

}
