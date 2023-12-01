package org.ldcgc.backend.service.users;

import org.ldcgc.backend.payload.dto.users.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface UserService {

    ResponseEntity<?> getMyUser(String token);

    ResponseEntity<?> updateMyUser(String token, UserDto user) throws ParseException;

    ResponseEntity<?> deleteMyUser(String token) throws ParseException;

    ResponseEntity<?> createUser(UserDto user);

    ResponseEntity<?> getUser(Integer userId);

    ResponseEntity<?> listUsers(Integer pageIndex, Integer size, String filterString, Integer userId);

    ResponseEntity<?> updateUser(Integer userId, UserDto user);

    ResponseEntity<?> deleteUser(Integer userId);

}
