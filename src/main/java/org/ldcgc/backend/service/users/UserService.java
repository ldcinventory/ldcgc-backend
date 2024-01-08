package org.ldcgc.backend.service.users;

import com.nimbusds.jose.JOSEException;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface UserService {

    ResponseEntity<?> getMyUser(String token);

    ResponseEntity<?> updateMyUser(String token, UserDto user) throws ParseException, JOSEException;

    ResponseEntity<?> deleteMyUser(String token);

    ResponseEntity<?> createUser(String token, UserDto user);

    ResponseEntity<?> getUser(Integer userId);

    ResponseEntity<?> listUsers(Integer pageIndex, Integer size, String filterString, Integer userId);

    ResponseEntity<?> updateUser(String token, Integer userId, UserDto user) throws ParseException, JOSEException;

    ResponseEntity<?> deleteUser(Integer userId);

}
