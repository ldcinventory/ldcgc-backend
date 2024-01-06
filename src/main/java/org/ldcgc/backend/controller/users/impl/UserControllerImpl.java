package org.ldcgc.backend.controller.users.impl;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.users.UserController;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.service.users.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    public ResponseEntity<?> getMyUser(String token) {
        return userService.getMyUser(token);
    }

    public ResponseEntity<?> updateMyUser(String token, UserDto user) throws ParseException, JOSEException {
        return userService.updateMyUser(token, user);
    }

    public ResponseEntity<?> deleteMyUser(String token) {
        return userService.deleteMyUser(token);
    }

    public ResponseEntity<?> createUser(String token, UserDto user) {
        return userService.createUser(token, user);
    }

    public ResponseEntity<?> getUser(Integer userId) {
        return userService.getUser(userId);
    }

    public ResponseEntity<?> listUsers(Integer pageIndex, Integer size, String filterString, Integer userId) {
        return userService.listUsers(pageIndex, size, filterString, userId);
    }

    public ResponseEntity<?> updateUser(String token, Integer userId, UserDto user) throws ParseException, JOSEException {
        return userService.updateUser(token, userId, user);
    }

    public ResponseEntity<?> deleteUser(Integer userId) {
        return userService.deleteUser(userId);
    }
}
