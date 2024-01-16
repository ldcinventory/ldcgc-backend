package org.ldcgc.backend.base.mock;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.payload.dto.users.AbsenceDto;
import org.ldcgc.backend.util.common.EWeekday;
import org.springframework.boot.test.context.TestConfiguration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.ldcgc.backend.base.mock.MockedUserVolunteer.getRandomBuilderAssistantId;

@TestConfiguration
@RequiredArgsConstructor
public class MockedAbsencesAvailability {

    public static Set<EWeekday> getRandomAvailabilitySet() {
        // a set to not allow duplicates
        Set<EWeekday> weekdays = new LinkedHashSet<>();

        // number of days to add
        int availabilityDays = new Random().ints(1, 0, 7).iterator().nextInt();

        // list of numbers
        SortedSet<Integer> days = new TreeSet<>();
        IntStream.range(0, availabilityDays).forEach(x -> days.add(new Random().ints(1, 0, 7).iterator().nextInt()));

        // list of days (ordered)
        days.forEach(i -> weekdays.add(EWeekday.values()[i]));

        return weekdays;
    }

    public static List<EWeekday> getRandomAvailabilityList() {
        return getRandomAvailabilitySet().stream().toList();
    }

    public static List<AbsenceDto> getRandomAbsences(int numAbsences) {
        // list of absences and ranges of days different days of absences
        List<AbsenceDto> absences = new ArrayList<>();
        IntStream.range(0, numAbsences).forEach(x -> {
            int rangeOfDays = new Random().ints(1, 0, 7).iterator().nextInt();
            LocalDate randomDate = LocalDate.now().plusDays(ThreadLocalRandom.current().nextInt(0, 366));
            AbsenceDto absence = AbsenceDto.builder()
                .dateFrom(randomDate)
                .dateTo(randomDate.plusDays(rangeOfDays))
                .builderAssistantId(getRandomBuilderAssistantId())
                .build();
            absences.add(absence);

        });

        absences.sort(Comparator.comparing(AbsenceDto::getDateFrom));

        return absences;
    }
}
