package org.ldcgc.backend.base.mock;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.payload.dto.category.ResponsibilityDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.users.AbsenceDto;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.payload.mapper.category.ResponsibilityMapper;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.app.security.user.UserDetailsImpl;
import org.ldcgc.backend.shared.enums.EUserRole;
import org.ldcgc.backend.shared.enums.EVolunteerStatus;
import org.ldcgc.backend.shared.enums.EWeekday;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.ldcgc.backend.base.mock.MockedResources.getRandomEnum;
import static org.springframework.security.crypto.argon2.Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8;

@TestConfiguration
@RequiredArgsConstructor
public class MockedUserVolunteer {

    private static final List<String> responsibilities = List.of("Coordinador GC", "Auxiliar GC", "Maestro de obra", "Oficial de primera", "Oficial de segunda", "Oficial de tercera", "Voluntario");

    private static final PasswordEncoder passwordEncoder = defaultsForSpringSecurity_v5_8();

    public static UserDto getMockedUserDto() {
        return UserDto.builder()
            .id(getRandomId())
            .email(new Faker().internet().emailAddress())
            .password(getRandomPassword(true))
            .responsibility(getRandomResponsibility())
            .group(GroupDto.builder()
                .id(0)
                .name(new Faker().funnyName().name())
                .description(new Faker().lorem().sentence())
                .phoneNumber(RandomStringUtils.randomNumeric(9))
                .urlImage("https://img.freepik.com/free-vector/engineering-construction-illustration_23-2148886139.jpg")
                .location(LocationDto.builder()
                    .id(0)
                    .name(new Faker().country().capital())
                    .description(new Faker().australia().locations())
                    .url("https://maps.app.goo.gl/" + RandomStringUtils.randomAlphanumeric(10))
                    .build())
                .build())
            .role(getRandomEnum(EUserRole.class))
            .volunteer(VolunteerDto.builder()
                .id(0)
                .name(new Faker().name().firstName())
                .lastName(new Faker().name().lastName())
                .availability(getRandomAvailabilityForMocked())
                .builderAssistantId(RandomStringUtils.randomAlphanumeric(8))
                .status(getRandomEnum(EVolunteerStatus.class))
                .build())
            .build();
    }

    public static User getMockedUserFromMockedUserDetailsImpl(UserDetailsImpl userDetails) {
        return User.builder()
            .id(userDetails.getId())
            .email(userDetails.getUsername())
            .password(userDetails.getPassword())
            .responsibility(ResponsibilityMapper.MAPPER.toEntity(getRandomResponsibility()))
            .group(Group.builder()
                .id(0)
                .name(new Faker().funnyName().name())
                .description(new Faker().lorem().sentence())
                .phoneNumber(RandomStringUtils.randomNumeric(9))
                .urlImage("https://img.freepik.com/free-vector/engineering-construction-illustration_23-2148886139.jpg")
                .location(Location.builder()
                    .id(0)
                    .name(new Faker().country().capital())
                    .description(new Faker().australia().locations())
                    .url("https://maps.app.goo.gl/" + RandomStringUtils.randomAlphanumeric(10))
                    .build())
                .build())
            .role(userDetails.getRolesFromAuthorities()[0])
            .volunteer(Volunteer.builder()
                .id(0)
                .name(new Faker().name().firstName())
                .lastName(new Faker().name().lastName())
                .availability(new HashSet<>(getRandomAvailabilityForMocked()))
                .builderAssistantId(RandomStringUtils.randomAlphanumeric(8))
                .status(getRandomEnum(EVolunteerStatus.class))
                .build())
            .build();
    }

    public static UserDetailsImpl getMockedUserDetailsImpl(EUserRole userRole, boolean acceptEULAs) {
        return getMockedUserDetailsImpl(userRole, acceptEULAs, acceptEULAs);
    }

    public static UserDetailsImpl getMockedUserDetailsImpl(EUserRole userRole, boolean acceptStandardEULA, boolean accceptManagerEULA) {
        String role = userRole == null ? getRandomEnum(EUserRole.class).getRoleName().toUpperCase() : userRole.getRoleName().toUpperCase();

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Objects.requireNonNull(userRole).name()));

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
            .username(new Faker().internet().emailAddress())
            .password(getRandomPassword(true))
            .roles(role)
            .authorities(authorities)
            .build();

        LocalDateTime acceptedEULAMoment = LocalDateTime.now();

        return new UserDetailsImpl(userDetails, getRandomId(),
            acceptStandardEULA ? acceptedEULAMoment : null,
            accceptManagerEULA ? acceptedEULAMoment : null);
    }

    private static ResponsibilityDto getRandomResponsibility() {
        return ResponsibilityDto.builder()
            .id(getRandomId())
            .name(getRandomElementFromList(responsibilities))
            .locked(new Random().nextBoolean())
            .build();
    }

    public static UserDto getMockedUserDto(Integer userId) {
        return getMockedUserDto().toBuilder()
            .id(userId)
            .build();
    }

    public static UserDto getMockedUserDto(UserDto userDto) {
        return userDto.toBuilder()
            .id(getRandomId())
            .password(null)
            .build();
    }

    public static UserDto getRandomMockedUserDto() {
        VolunteerDto volunteerFromMocked = getMockedUserDto().getVolunteer();

        return getMockedUserDto().toBuilder()
            .id(getRandomId())
            .password(getRandomPassword(true))
            .email(new Faker().internet().emailAddress())
            .volunteer(volunteerFromMocked.toBuilder()
                .name(new Faker().name().firstName())
                .lastName(String.format("%s %s", new Faker().name().lastName(), new Faker().name().lastName()))
                .builderAssistantId(getRandomBuilderAssistantId())
                .availability(getRandomAvailabilityForMocked())
                .absences(getRandomAbsences())
                .build())
            .build();
    }

    public static UserDto getRandomMockedUserDto(EUserRole EUserRole) {
        return getRandomMockedUserDto().toBuilder().role(EUserRole).build();
    }

    public static UserDto getRandomMockedUserDtoWithoutVolunteer() {
        return getRandomMockedUserDto().toBuilder().volunteer(null).build();
    }

    public static UserDto getRandomMockedUpdatingUserDto(EUserRole role) {
        return getMockedUserDto().toBuilder()
            .id(getRandomId())
            .email(new Faker().internet().emailAddress())
            .password(getRandomPassword(true))
            .role(role)
            .responsibility(getRandomResponsibility())
            .group(GroupDto.builder().id(0).build())
            .volunteer(VolunteerDto.builder().id(0).build())
            .build();
    }

    public static UserDto getRandomMockedUserDtoLogin() {
        UserDto userDto = getRandomMockedUserDto();
        return UserDto.builder()
            .email(userDto.getEmail())
            .password(getRandomPassword(true))
            .build();
    }

    public static User getRandomMockedUser() {
        return UserMapper.MAPPER.toEntity(getRandomMockedUserDto());
    }

    public static User getRandomMockedUser(EUserRole role) {
        return UserMapper.MAPPER.toEntity(getRandomMockedUserDto().toBuilder().role(role).build());
    }

    public static List<UserDto> getListOfMockedUsers(Integer listSize) {
        return IntStream.range(0, listSize).mapToObj(x -> getRandomMockedUserDto()).toList();
    }

    private static String getRandomElementFromList(List<String> list) {
        return list.get(new Random().ints(1, 0, list.size() - 1).iterator().nextInt());
    }

    private static List<EWeekday> getRandomAvailabilityForMocked() {
        // a set to not allow duplicates
        List<EWeekday> weekdays = new ArrayList<>();

        // number of days to add
        int availabilityDays = new Random().ints(1, 0, 7).iterator().nextInt();

        // list of numbers
        SortedSet<Integer> days = new TreeSet<>();
        IntStream.range(0, availabilityDays).forEach(x -> days.add(new Random().ints(1, 0, 7).iterator().nextInt()));

        // list of days (ordered)
        days.forEach(i -> weekdays.add(EWeekday.values()[i]));

        return weekdays;
    }

    private static EUserRole getRandomRole() {
        List<EUserRole> roles = Arrays.asList(EUserRole.values());
        return roles.get(new Random().nextInt(roles.size()));
    }

    public static VolunteerDto getEmptyVolunteer() {
        return VolunteerDto.builder().id(0).build();
    }

    public static VolunteerDto getRandomVolunteer() {
        Integer randomId = getRandomId();

        return VolunteerDto.builder()
            .id(randomId)
            .name(new Faker().name().firstName())
            .lastName(String.format("%s %s", new Faker().name().lastName(), new Faker().name().lastName()))
            .builderAssistantId(getRandomBuilderAssistantId())
            .availability(getRandomAvailabilityForMocked())
            .build();
    }

    public static VolunteerDto getRandomVolunteerWithoutAvailability() {
        return getRandomVolunteer().toBuilder().availability(null).build();
    }

    private static Integer getRandomId() {
        return new Random().ints(1, 0, 500000).iterator().nextInt();
    }

    public static String getRandomBuilderAssistantId() {
        return RandomStringUtils.randomAlphanumeric(8).toUpperCase();
    }

    public static List<VolunteerDto> getListOfMockedVolunteers(Integer listSize) {
        return Stream.generate(MockedUserVolunteer::getRandomVolunteer).limit(listSize).toList();
    }

    private static List<AbsenceDto> getRandomAbsences() {
        // number of absences to add
        int numAbsences = new Random().ints(1, 1, 7).iterator().nextInt();

        // list of absences and ranges of days different days of absences
        List<AbsenceDto> absences = new ArrayList<>();
        IntStream.range(0, numAbsences).forEach(x -> {
            int rangeOfDays = new Random().ints(1, 0, 7).iterator().nextInt();
            LocalDate randomDate = getRandomDate(false);
            AbsenceDto absence = AbsenceDto.builder()
                .id(new Random().ints(1, 0, 100_000).iterator().nextInt())
                .dateFrom(randomDate)
                .dateTo(randomDate.plusDays(rangeOfDays))
                .builderAssistantId(getRandomBuilderAssistantId())
                .build();
            absences.add(absence);

        });

        absences.sort(Comparator.comparing(AbsenceDto::getDateFrom));

        return absences;
    }

    private static LocalDate getRandomDate(boolean includeNullValue) {
        return getRandomDate(includeNullValue,-366, 366);
    }

    private static LocalDate getRandomFutureDate(boolean includeNullValue) {
        return getRandomDate(includeNullValue, 0, 366);
    }

    private static LocalDate getRandomPastDate(boolean includeNullValue) {
        return getRandomDate(includeNullValue, -366, 0);
    }

    private static LocalDate getRandomDate(boolean includeNullValue, int pastDays, int futureDays) {
        if(includeNullValue && ThreadLocalRandom.current().nextBoolean())
            return null;
        return LocalDate.now().plusDays(ThreadLocalRandom.current().nextInt(pastDays, futureDays));
    }

    private static String getRandomPassword(boolean fromFaker) {
        if(fromFaker) return passwordEncoder.encode(new Faker().internet().password());

        return passwordEncoder.encode(RandomStringUtils.randomAlphanumeric(10));
    }

}
