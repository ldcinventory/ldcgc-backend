package org.ldcgc.backend.service.users.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.users.EulaDto;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.EulaService;
import org.ldcgc.backend.util.common.EEULAStatus;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

import static org.ldcgc.backend.security.jwt.JwtUtils.cleanLocalTokensFromUserId;
import static org.ldcgc.backend.util.common.ERole.ROLE_ADMIN;
import static org.ldcgc.backend.util.common.ERole.ROLE_MANAGER;
import static org.ldcgc.backend.util.common.ERole.ROLE_USER;

@Component
@RequiredArgsConstructor
public class EulaServiceImpl implements EulaService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtils jwtUtils;

    @Value("${eula-standard}") private String EULA_STANDARD;
    @Value("${eula-managers}") private String EULA_MANAGERS;

    private final String EVERY_USER = "every user";
    private final String MANAGERS = "managers and admins";

    public ResponseEntity<?> getEULA(String token) throws ParseException {
        User user = userRepository.findById(jwtUtils.getUserIdFromStringToken(token))
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

        // any user must accept standard EULA
        if(user.getAcceptedEULA() == null)
            return getEULAStandard();

        if(user.getRole().equalsAny(ROLE_MANAGER, ROLE_ADMIN) && user.getAcceptedEULAManager() == null)
            return getEULAManager();

        return Constructor.buildResponseMessage(HttpStatus.CONFLICT, String.format(Messages.Info.EULA_ALREADY_ACCEPTED, user.getRole().equals(ROLE_USER) ? EVERY_USER : MANAGERS));
    }

    private ResponseEntity<?> getEULAStandard() {
        EulaDto eulaDto = EulaDto.builder()
            .actionsAvailable(List.of(EEULAStatus.ACCEPT, EEULAStatus.PENDING, EEULAStatus.REJECT))
            .url(EULA_STANDARD)
            .build();

        return Constructor.buildResponseMessageObject(HttpStatus.OK, String.format(Messages.App.EULA_SELECT_ACTION, EVERY_USER), eulaDto);
    }

    private ResponseEntity<?> getEULAManager() {
        EulaDto eulaDto = EulaDto.builder()
            .actionsAvailable(List.of(EEULAStatus.ACCEPT, EEULAStatus.PENDING, EEULAStatus.REJECT))
            .url(EULA_MANAGERS)
            .build();

        return Constructor.buildResponseMessageObject(HttpStatus.OK, String.format(Messages.App.EULA_SELECT_ACTION, MANAGERS), eulaDto);
    }

    public ResponseEntity<?> putEULA(String token, EEULAStatus action) throws ParseException {
        User user = userRepository.findById(jwtUtils.getUserIdFromStringToken(token))
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

        if(user.getRole().equals(ROLE_USER) && user.getAcceptedEULA() != null)
            return Constructor.buildResponseMessage(HttpStatus.CONFLICT, String.format(Messages.Info.EULA_ALREADY_ACCEPTED, EVERY_USER));

        if(user.getRole().equalsAny(ROLE_MANAGER, ROLE_ADMIN) && user.getAcceptedEULAManager() != null)
            return Constructor.buildResponseMessage(HttpStatus.CONFLICT, String.format(Messages.Info.EULA_ALREADY_ACCEPTED, MANAGERS));

        final String userRole = user.getAcceptedEULA() == null ? EVERY_USER : MANAGERS ;

        switch(action) {
            case ACCEPT -> {
                if(user.getAcceptedEULA() == null)
                    user.setAcceptedEULA(LocalDateTime.now());
                else if (user.getAcceptedEULAManager() == null)
                    user.setAcceptedEULAManager(LocalDateTime.now());

                userRepository.saveAndFlush(user);
                return Constructor.buildResponseMessage(HttpStatus.OK, String.format(Messages.Info.EULA_ACCEPTED, userRole));
            }

            case PENDING -> {
                return Constructor.buildResponseMessage(HttpStatus.OK, String.format(Messages.Info.EULA_PENDING, userRole));
            }

            case REJECT -> {
                String rejectionMessage;
                cleanLocalTokensFromUserId(user.getId(), true);
                tokenRepository.deleteAllTokensFromUser(user.getId());
                if(user.getAcceptedEULA() == null) {
                    // if standard user doesn't accept EULA, delete
                    rejectionMessage = Messages.Info.EULA_DELETE_USER;
                    userRepository.delete(user);
                } else {
                    // if manager/admin doesn't accept EULA, downgrade to standard user
                    rejectionMessage = Messages.Info.EULA_DOWNGRADE_USER;
                    user.setRole(ROLE_USER);
                    userRepository.saveAndFlush(user);
                }

                return Constructor.buildResponseMessage(HttpStatus.OK, String.format(Messages.Info.EULA_REJECTED, userRole, rejectionMessage));
            }
        }

        return Constructor.buildResponseMessage(HttpStatus.BAD_REQUEST, Messages.Error.EULA_ACTION_INVALID);
    }
}
