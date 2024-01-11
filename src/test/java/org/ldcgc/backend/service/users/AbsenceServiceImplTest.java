package org.ldcgc.backend.service.users;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.db.repository.users.AbsenceRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.impl.AbsenceServiceImpl;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class AbsenceServiceImplTest {

    private AbsenceService absenceService;

    @Mock JwtUtils jwtUtils;
    @Mock UserRepository userRepository;
    @Mock VolunteerRepository volunteerRepository;
    @Mock AbsenceRepository absenceRepository;

    @BeforeEach
    public void init() {
        absenceService = new AbsenceServiceImpl(jwtUtils, userRepository, volunteerRepository, absenceRepository);
    }

    // me
    @Test
    public void whenGetMyAbsence_returnUserNotFound() {

    }

    @Test
    public void whenGetMyAbsence_returnUserDoesntHaveVolunteer() {

    }

    @Test
    public void whenGetMyAbsence_returnTokenNotParseable() {

    }

    @Test
    public void whenGetMyAbsence_returnAbsenceVolunteerNotFound() {

    }

    @Test
    public void whenGetMyAbsence_returnAbsence() {

    }

    @Test
    public void whenListAbsences_returnUserNotFound() {

    }

    @Test
    public void whenListAbsences_returnUserDoesntHaveVolunteer() {

    }

    @Test
    public void whenListAbsences_returnTokenNotParseable() {

    }

    @Test
    public void whenListAbsencesUnfiltered_returnUserAbsences() {

    }

    @Test
    public void whenListAbsencesFilteredByDate_returnUserAbsencesFiltered() {

    }

    @Test
    public void whenCreateMyAbsence_returnUserNotFound() {

    }

    @Test
    public void whenCreateMyAbsence_returnUserDoesntHaveVolunteer() {

    }

    @Test
    public void whenCreateMyAbsence_returnTokenNotParseable() {

    }

    @Test
    public void whenCreateMyAbsence_returnAbsenceCreated() {

    }

    @Test
    public void whenUpdateMyAbsence_returnUserNotFound() {

    }

    @Test
    public void whenUpdateMyAbsence_returnUserDoesntHaveVolunteer() {

    }

    @Test
    public void whenUpdateMyAbsence_returnTokenNotParseable() {

    }

    @Test
    public void whenDeleteMyAbsence_returnUserNotFound() {

    }

    @Test
    public void whenDeleteMyAbsence_returnUserDoesntHaveVolunteer() {

    }

    @Test
    public void whenDeleteMyAbsence_returnTokenNotParseable() {

    }

    // managed
    @Test
    public void whenGetAbsence_returnAbsenceNotFound() {

    }

    @Test
    public void whenGetAbsence_returnAbsence() {

    }

    @Test
    public void whenListAbsencesUnfiltered_returnAbsencesList() {

    }

    @Test
    public void whenListAbsencesFilteredByDate_returnAbsencesList() {

    }

    @Test
    public void whenListAbsencesFilteredByBAIds_returnAbsencesList() {

    }

    @Test
    public void whenListAbsencesFilteredByDateAndBAIds_returnAbsencesList() {

    }

    @Test
    public void whenCreateAbsence_returnVolunteerNotInformed() {

    }

    @Test
    public void whenCreateAbsence_returnVolunteerNotFound() {

    }

    @Test
    public void whenCreateAbsence_returnAbsenceCreated() {

    }

    @Test
    public void whenUpdateAbsence_returnAbsenceNotFound() {

    }

    @Test
    public void whenUpdateAbsence_returnAbsenceUpdated() {

    }

    @Test
    public void whenDeleteAbsence_returnAbsenceNotFound() {

    }

    @Test
    public void whenDeleteAbsence_returnAbsenceVolunteerLinkedNotFound() {

    }

    @Test
    public void whenDeleteAbsence_returnAbsenceDeleted() {

    }

}
