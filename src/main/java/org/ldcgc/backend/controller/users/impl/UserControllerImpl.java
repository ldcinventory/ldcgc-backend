package org.ldcgc.backend.controller.users.impl;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.users.UserController;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.service.users.UserService;
import org.ldcgc.backend.shared.enums.EOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    public ResponseEntity<?> getMyUser(String token) throws ParseException {
        return userService.getMyUser(token);
    }

    public ResponseEntity<?> updateMyUser(String token, UserDto userDto) throws ParseException, JOSEException {
        return userService.updateMyUser(token, userDto);
    }

    public ResponseEntity<?> deleteMyUser(String token) throws ParseException {
        return userService.deleteMyUser(token);
    }

    public ResponseEntity<?> createUser(String token, UserDto userDto) {
        return userService.createUser(token, userDto);
    }

    public ResponseEntity<?> getUser(Integer userId) {
        return userService.getUser(userId);
    }

    public ResponseEntity<?> listUsers(String filterString, Integer userId, Boolean enabled, Integer pageIndex, Integer size, String sortField, EOrder order) {
        return userService.listUsers(filterString, userId, enabled, pageIndex, size, sortField, order);
    }

    public ResponseEntity<?> updateUser(String token, Integer userId, UserDto user) throws ParseException, JOSEException {
        return userService.updateUser(token, userId, user);
    }

    public ResponseEntity<?> linkUserToVolunteer(Integer userId, String builderAssistantId) {
        return userService.linkUserToVolunteer(userId, builderAssistantId);
    }

    public ResponseEntity<?> unlinkUserToVolunteer(Integer userId) {
        return userService.unlinkUserToVolunteer(userId);
    }

    public ResponseEntity<?> deleteUser(Integer userId) {
        return userService.deleteUser(userId);
    }
}
