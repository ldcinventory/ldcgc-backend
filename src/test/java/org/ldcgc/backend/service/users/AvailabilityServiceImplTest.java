package org.ldcgc.backend.service.users;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.impl.AvailabilityServiceImpl;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class AvailabilityServiceImplTest {

    AvailabilityService availabilityService;

    @Mock VolunteerRepository volunteerRepository;
    @Mock UserRepository userRepository;
    @Mock JwtUtils jwtUtils;

    @BeforeEach
    public void init() {
        availabilityService = new AvailabilityServiceImpl(jwtUtils, userRepository, volunteerRepository);
    }

    // me

    @Test
    public void whenGetMyAvailability_returnUserNotFound() {

    }

    @Test
    public void whenGetMyAvailability_returnUserDoesntHaveVolunteer() {

    }

    @Test
    public void whenGetMyAvailability_returnVolunteerWithoutBAId() {

    }

    @Test
    public void whenGetMyAvailability_returnAvailability() {

    }

    @Test
    public void whenUpdateMyAvailability_returnUserNotFound() {

    }

    @Test
    public void whenUpdateMyAvailability_returnUserDoesntHaveVolunteer() {

    }

    @Test
    public void whenUpdateMyAvailability_returnVolunteerWithoutBAId() {

    }

    @Test
    public void whenUpdateMyAvailability_returnAvailabilityUpdated() {

    }

    @Test
    public void whenClearMyAvailability_returnUserNotFound() {

    }

    @Test
    public void whenClearMyAvailability_returnUserDoesntHaveVolunteer() {

    }

    @Test
    public void whenClearMyAvailability_returnVolunteerWithoutBAId() {

    }

    @Test
    public void whenClearMyAvailability_returnAvailabilityCleared() {

    }

    // managed

    @Test
    public void whenGetAvailability_returnVolunteerNotFound() {

    }

    @Test
    public void whenGetAvailability_returnAvailability() {

    }

    @Test
    public void whenUpdateAvailability_returnVolunteerNotFound() {

    }

    @Test
    public void whenUpdateAvailability_returnAvailabilityUpdated() {

    }

    @Test
    public void whenClearAvailability_returnVolunteerNotFound() {

    }

    @Test
    public void whenClearAvailability_returnAvailabilityCleared() {

    }

}
