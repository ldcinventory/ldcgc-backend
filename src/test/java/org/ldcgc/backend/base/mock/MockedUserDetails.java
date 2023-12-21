package org.ldcgc.backend.base.mock;

import lombok.RequiredArgsConstructor;
import org.checkerframework.common.value.qual.MinLen;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.users.AvailabilityDto;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.common.EWeekday;
import org.springframework.boot.test.context.TestConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.ldcgc.backend.util.common.EWeekday.FRIDAY;
import static org.ldcgc.backend.util.common.EWeekday.HOLIDAY;
import static org.ldcgc.backend.util.common.EWeekday.MONDAY;
import static org.ldcgc.backend.util.common.EWeekday.SUNDAY;
import static org.ldcgc.backend.util.common.EWeekday.TUESDAY;
import static org.ldcgc.backend.util.common.EWeekday.WEDNESDAY;

@TestConfiguration
@RequiredArgsConstructor
public class MockedUserDetails {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!·$%&/()=|@#?¿´‚ºª'¡`+´ç-.,<>;:_¨Ç^*[]{}";

    private static final List<String> NAMES = List.of(
        "Adriel","Agni","Anmon","Areu","Axel","Baco","Bernal","Brais","Caín","Catriel","Ciro","Dante","Drac","Éber","Elián","Elm","Elon","Enzo","Eros","Esaú","Farid","Flavio","Hans","Inder","Ion","Jaguar","Jou","Keanu","Liam","Lolo","Milos","Mirt","Mon","Nadir","Nahuel","Naim","Neón","Nil","Quiles","Remo","Roso","Silveri","Tadeo","Tahiel","Tarik","Telmo","Truman","Uriel","Vice","Zac"
    );
    private static final List<String> LAST_NAMES = List.of(
        "Zuzunaga","Sorní","Garza","Sandemetrio","Urriaga","Bonachera","Vital","Pregonas","Sazón","Sorda","Enamorado","Cacharro","Víbora","Cama","Pieldelobo","Piesplanos","Tenedor","Delfín","Pechoabierto","Alcoholado","Verdugo","Llagaria","Cidoncha","Anacleto","Parraverde","Nuero","Nomdedeu","Piernavieja","Perfume","Ariztimuño","Arrubal","Barato","Viejobueno","Cayado","Callado","Cazador","Caimanes","Sin","Zas","Rajado","Chinchurreta","Cosío","Fermonsel","Gandul","Piernabierta","Guarnido","Física","Sacamoco","Lúcido","Triunfo","Hergueta","Bru","Raga","Cuñat","Pruñonosa","Lujan","Fajardo","Coscojuela","Funes","Mantilla","Gallur","Melgar","Longán","Ibars","Cedeño"
    );
    private static final List<String> EMAILS = List.of(
        "marcofpn@mail.com","marcelinobassett@mail.com","lauraquirogavalpa1985@mail.com","joselyn-monsalve@lycos.es","karandare@ymail.com","franciscpalma@ymail.com","maleelizondo@mail.com","mcanelos@vtr.net","mefi_sto-scum@mail.com","mariamorfin@mail.com","luzgarciaguzman@ymail.com","atihnfa@yajoo.com","cristina_ush@mail.com","ruthkochenschub@mail.com","aliciaecheveste@mail.com","marion2008@yajoo.com","edifornasin@mail.com","edopenaval@ymail.com","gerardo_sauri@mail.com","grace0304@ymail.com","sundesertroses@ymail.com","luisbelloh@mail.com","maella_32@mail.com","josem.yac@ymail.com","cijacuarel@yajoo.es","inesaolima@mail.com","miguelcolman@mail.com","caratag@ymail.com","marilyn_m3@mail.com","martinamariafh@yajoo.com","mariceleiriz@ymail.com","enriquefaundez@ymail.com","mariamarquesmargarida@ymail.com","fonsykidss@yajoo.com","centro.epifania@ymail.com","delmyrecinosg@mail.com","jorgeetoro@mail.com","jafsdoc@yajoo.com","luchito.venegas@ymail.com","hildahormazabal@mail.com","miriamsian@mail.com","jacob_1770@mail.com","lpaz131@ymail.com","mifcat@mail.com","marlagonzale@mail.com","mnnatsop_sede@yajoo.es","mdlopez24@mail.com","despinozapatel@mail.com","info@wfmh.com","monikaniederle@mail.com","erica_lanas@yajoo.es","marisolantigua@mail.com","adr1054@ymail.com","cosecodeni@yajoo.es","amaliabr@mail.com","peliptiu@ymail.com","geovaldona@ymail.com","jmerin.reig@ymail.com","charomendiola@mail.com","despujoljv@mail.com","julio_valle_iscar@mail.com","mnnatsop_sede@yajoo.es","lola_40@mail.com","irmandrea53@mail.com","alejuarez_316@mail.com","maritzadelepiani@mail.com","mreyesherrera@ymail.com","ivovega@yajoo.com","ivan.salinasbarrios@ymail.com","ioannys_15@mail.com","jorgeenriqueescobar@latinmail.com","marvinrabanales@mail.com","rfsepulveda@ymail.com","karinajjm@ymail.com","blanri@mail.com"
    );

    private static UserDto getMockedUser() {
        return UserDto.builder()
            .id(0)
            .email("test@ymail.com")
            //.password("Admin1234!")
            .responsibility(CategoryDto.builder()
                .id(0)
                .name("Coordinador GC")
                .categories(Collections.singletonList(CategoryDto.builder()
                    .id(0)
                    .name("Responsabilidades")
                    .build()))
                .build())
            .group(GroupDto.builder()
                .id(0)
                .name("Grupo 8 - Madrid")
                .description("Grupo de construcción en Madrid")
                .phoneNumber("600111345")
                .urlImage("https://img.freepik.com/free-vector/engineering-construction-illustration_23-2148886139.jpg")
                .location(LocationDto.builder()
                    .id(0)
                    .name("Betel")
                    .description("M-108, Km. 5, 28864 Ajalvir, Madrid")
                    .url("https://maps.app.goo.gl/pATo32bqj4JZhrp39")
                    .build())
                .build())
            .role(ERole.ROLE_ADMIN)
            .volunteer(VolunteerDto.builder()
                .id(0)
                .name("Axel")
                .lastName("Sandemetrio Bonachera")
                .availability(AvailabilityDto.builder()
                    .volunteerId(0)
                    .availabilityDays(List.of(MONDAY, TUESDAY, WEDNESDAY, FRIDAY, SUNDAY, HOLIDAY))
                    .build())
                .builderAssistantId("233578")
                .isActive(true)
                .build())
            .build();
    }

    public static UserDto getRandomMockedUserDto() {
        VolunteerDto volunteerFromMocked = getMockedUser().getVolunteer();
        AvailabilityDto availabilityFromMocked = getMockedUser().getVolunteer().getAvailability();
        Integer id = new Random().ints(1, 0, 500000).iterator().nextInt();
        String randomBuilderAssistantId = String.valueOf(new Random()
            .ints(1, 1000, 500000).iterator().nextInt());

        return getMockedUser().toBuilder()
            .id(id)
            .email(getRandomElementFromList(EMAILS))
            .role(getRandomRole())
            .volunteer(volunteerFromMocked.toBuilder()
                .name(getRandomElementFromList(NAMES))
                .lastName(String.format("%s %s",
                    getRandomElementFromList(LAST_NAMES), getRandomElementFromList(LAST_NAMES)))
                .builderAssistantId(randomBuilderAssistantId)
                .availability(availabilityFromMocked.toBuilder()
                    .availabilityDays(getRandomAvailabilityForMocked())
                    .build())
                .build())
            .build();
    }

    public static UserDto getRandomMockedUserDto(ERole role) {
        return getMockedUser().toBuilder().password(getRandomPassword()).role(role).build();
    }

    public static UserDto getRandomMockedUpdatingUserDto(ERole role) {
        Integer id = new Random().ints(1, 0, 500000).iterator().nextInt();

        return getMockedUser().toBuilder()
            .id(id)
            .email(getRandomElementFromList(EMAILS))
            .password(getRandomPassword())
            .role(role)
            .responsibility(CategoryDto.builder()
                .id(0)
                .name("Coordinador GC")
                .category(CategoryDto.builder().id(0).name("Responsabilidades").build())
                .build())
            .group(GroupDto.builder().id(0).build())
            .volunteer(VolunteerDto.builder().id(0).build())
            .build();
    }

    public static UserDto getRandomMockedUserDtoLogin() {
        UserDto userDto = getRandomMockedUserDto();
        return UserDto.builder()
            .email(userDto.getEmail())
            .password(getRandomPassword())
            .build();
    }

    public static User getRandomMockedUser() {
        return UserMapper.MAPPER.toEntity(getRandomMockedUserDto());
    }

    public static User getRandomMockedUser(ERole role) {
        return UserMapper.MAPPER.toEntity(getRandomMockedUserDto().toBuilder().role(role).build());
    }

    public static List<UserDto> getListOfMockedUsers(Integer listSize) {
        return Stream.generate(() -> getRandomMockedUserDto()).limit(listSize).toList();
    }

    private static String getRandomElementFromList(List<String> list) {
        return list.get(new Random().ints(1, 0, list.size() - 1).iterator().nextInt());
    }

    private static List<EWeekday> getRandomAvailabilityForMocked() {
        // a set to not allow duplicates
        Set<EWeekday> weekdays = new HashSet<>();

        // number of days to add
        int availabilityDays = new Random().ints(1, 0, 7).iterator().nextInt();

        IntStream.range(0, availabilityDays).forEach(x -> {
            // random day number to get from enum array
            int day = new Random().ints(1, 0, 7).iterator().nextInt();
            weekdays.add(EWeekday.values()[day]);
        });

        return weekdays.stream().toList();
    }

    private static String getRandomPassword(@MinLen(8) int length) {
        StringBuilder sb = new StringBuilder(length);

        for(int i = 0; i < length; i++)
            sb.append(ALPHABET.charAt(new Random().nextInt(ALPHABET.length())));

        return sb.toString();
    }

    private static String getRandomPassword() {
        return getRandomPassword(8);
    }

    private static ERole getRandomRole() {
        List<ERole> roles = Arrays.asList(ERole.values());
        return roles.get(new Random().nextInt(roles.size()));
    }

}
