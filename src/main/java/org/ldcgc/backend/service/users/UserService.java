package org.ldcgc.backend.service.users;

import com.nimbusds.jose.JOSEException;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.shared.enums.EOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface UserService {

    ResponseEntity<?> getMyUser(String token) throws ParseException;

    ResponseEntity<?> updateMyUser(String token, UserDto user) throws ParseException, JOSEException;

    ResponseEntity<?> deleteMyUser(String token) throws ParseException;

    ResponseEntity<?> createUser(String token, UserDto user);

    ResponseEntity<?> getUser(Integer userId);

    ResponseEntity<?> listUsers(String filterString, Integer userId, Boolean enabled, Integer pageIndex, Integer size, String sortField, EOrder order);

    ResponseEntity<?> updateUser(String token, Integer userId, UserDto user) throws ParseException, JOSEException;

    ResponseEntity<?> linkUserToVolunteer(Integer userId, String builderAssistantId);

    ResponseEntity<?> unlinkUserToVolunteer(Integer userId);

    ResponseEntity<?> deleteUser(Integer userId);

}
