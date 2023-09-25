package org.ldcgc.backend.service.users.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.service.users.UserService;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.mock.UserMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.ldcgc.backend.util.retrieving.Messages.getInfoMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // TODO implement user repository instead mock
    private final UserRepository userRepository;

    public ResponseEntity<?> getMyUser(String token) {
        return Constructor.buildResponseObject(HttpStatus.OK, UserMock.getMockedUser());
    }

    public ResponseEntity<?> updateMyUser(String token, UserDto user) {
        return Constructor.buildResponseMessageObject(HttpStatus.OK, getInfoMessage("USER_UPDATED"), UserMock.getMockedUser(user));
    }

    public ResponseEntity<?> deleteMyUser(String token) {
        return Constructor.buildResponseMessage(HttpStatus.OK, getInfoMessage("USER_DELETED"));
    }

    public ResponseEntity<?> createUser(UserDto user) {
        return Constructor.buildResponseMessageObject(HttpStatus.OK, getInfoMessage("USER_CREATED"), UserMock.getMockedUser(user));
    }

    public ResponseEntity<?> getUser(Integer userId) {
        return Constructor.buildResponseObject(HttpStatus.OK, UserMock.getMockedUser(userId));
    }

    public ResponseEntity<?> listUsers(Integer pageIndex, Integer sizeIndex, String filterString, Integer userId) {
        if(userId != null)
            return Constructor.buildResponseMessageObject(HttpStatus.OK, String.format(getInfoMessage("USER_LISTED"), 1), UserMock.getMockedUser(userId));

        if(StringUtils.isNotEmpty(filterString)) {
            List<UserDto> users = UserMock.getListOfMockedUsers(100).stream()
                .filter(user -> StringUtils.containsIgnoreCase(user.getVolunteer().getName(), filterString) ||
                    StringUtils.containsIgnoreCase(user.getVolunteer().getLastName(), filterString) ||
                    StringUtils.containsIgnoreCase(user.getEmail(), filterString))
                .toList();
            return Constructor.buildResponseMessageObject(HttpStatus.OK, String.format(getInfoMessage("USER_LISTED"), users.size()), users);
        }


        return Constructor.buildResponseMessageObject(HttpStatus.OK, String.format(getInfoMessage("USER_LISTED"), sizeIndex), UserMock.getListOfMockedUsers(sizeIndex));
    }

    public ResponseEntity<?> updateUser(Integer userId, UserDto user) {
        return Constructor.buildResponseMessageObject(HttpStatus.OK, getInfoMessage("USER_UPDATED"), UserMock.getMockedUser(userId));
    }

    public ResponseEntity<?> deleteUser(Integer userId) {
        return Constructor.buildResponseMessage(HttpStatus.OK, getInfoMessage("USER_DELETED"));
    }
}
