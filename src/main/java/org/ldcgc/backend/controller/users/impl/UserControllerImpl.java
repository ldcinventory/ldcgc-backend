package org.ldcgc.backend.controller.users.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.users.UserController;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.service.users.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    public ResponseEntity<?> getMyUser(String token) {
        return userService.getMyUser(token);
    }

    public ResponseEntity<?> updateMyUser(String token, UserDto user) {
        return userService.updateMyUser(token, user);
    }

    public ResponseEntity<?> deleteMyUser(String token) {
        return userService.deleteMyUser(token);
    }

    public ResponseEntity<?> createUser(UserDto user) {
        return userService.createUser(user);
    }

    public ResponseEntity<?> getUser(Integer userId) {
        return userService.getUser(userId);
    }

    public ResponseEntity<?> listUsers(Integer pageIndex, Integer sizeIndex, String filterString, Integer userId) {
        return userService.listUsers(pageIndex, sizeIndex, filterString, userId);
    }

    public ResponseEntity<?> updateUser(Integer userId, UserDto user) {
        return userService.updateUser(userId, user);
    }

    public ResponseEntity<?> deleteUser(Integer userId) {
        return userService.deleteUser(userId);
    }
}