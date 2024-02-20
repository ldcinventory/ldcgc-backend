package org.ldcgc.backend.base.mock;

import net.datafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.util.common.EStatus;
import org.ldcgc.backend.util.common.EStockType;
import org.ldcgc.backend.util.common.ETimeUnit;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class MockedResources {

    private static final List<String> brandNames = Arrays.asList("<empty>", "ABAC MONTECARLO", "Bahco", "Bellota", "Bellota 5894-150", "Blackwire", "bo", "Climaver", "Deltaplus", "Desa", "Dewalt", "Disponible", "EZ-Fasten", "Femi", "Fischer Darex", "Forged ", "GRESPANIA", "Hermin", "Hilti", "HP", "IFAM", "INDEX", "Irazola", "Irimo", "Kartcher", "Knipex", "Lenovo", "Loria", "Makita", "Mannesmann", "Metal Works", "Milwaukee", "Mirka", "ML-OK", "Novipro", "Nusac", "OPEL", "Palmera", "Panduit", "Pentrilo", "Petzl", "Powerfix", "Proiman", "Quilosa", "Retevis", "Rothenberger", "Rubi", "Rubi negra", "Samsung", "Schneider", "Stanley", "Stayer", "Svelt", "Tacklife", "Testo", "UNI-T", "Urceri", "Velour", "Vorel", "Würth", "WERKU", "Wiha", "Xiaomi", "Zosi Smart");

    public static ConsumableDto getRandomConsumableDto() {
        return ConsumableDto.builder()
            .id(getRandomId())
            .barcode(getRandomBarcode())
            .group(getRandomGroup())
            .model(getRandomAlphaNumeric(getRandomIntegerFromRange(3, 10)))
            .brand(getRandomBrand())
            .description(new Faker().text().text())
            .price(new Faker().random().nextFloat())
            .name(getRandomString(getRandomIntegerFromRange(5, 15)))
            .stock(getRandomFloatFromRange(1,100))
            .stockType(getRandomEnum(EStockType.class))
            .minStock(getRandomFloatFromRange(0,100))
            .purchaseDate(getRandomLocalDateUntilNow())
            .urlImages(getRandomURLs())
            .category(getRandomCategory())
            .location(getRandomLocation())
            .build();
    }

    public static ToolDto getRandomToolDto() {
        return ToolDto.builder()
            .id(getRandomId())
            .barcode(getRandomBarcode())
            .category(getRandomCategory())
            .brand(getRandomBrand())
            .name(getRandomString(getRandomIntegerFromRange(5, 15)))
            .model(getRandomAlphaNumeric(getRandomIntegerFromRange(3, 10)))
            .description(new Faker().text().text())
            .weight(getRandomFloatFromRange(1,100))
            .stockWeightType(getRandomEnum(EStockType.class))
            .price(new Faker().random().nextFloat())
            .purchaseDate(getRandomLocalDateUntilNow())
            .urlImages(getRandomURLs())
            .maintenancePeriod(getRandomIntegerFromRange(0,10))
            .maintenanceTime(getRandomEnum(ETimeUnit.class))
            .lastMaintenance(null)
            .nextMaintenance(getRandomLocalDateFromNow())
            .status(getRandomEnum(EStatus.class))
            .location(getRandomLocation())
            .group(getRandomGroup())
            .build();
    }

    private static LocationDto getRandomLocation() {
        return LocationDto.builder()
            .id(getRandomId())
            .name(new Faker().address().streetName())
            .url(new Faker().internet().url())
            .description(new Faker().text().text())
            .build();
    }

    private static GroupDto getRandomGroup() {
        return GroupDto.builder()
            .id(getRandomId())
            .name(new Faker().cat().name())
            .description(new Faker().cat().breed())
            .urlImage(new Faker().internet().url())
            .build();
    }

    private static CategoryDto getRandomCategory() {
        return CategoryDto.builder()
            .id(getRandomId())
            .name(new Faker().brand().watch())
            .build();
    }

    private static String[] getRandomURLs() {
        return IntStream.rangeClosed(1, getRandomIntegerFromRange(2, 4))
            .mapToObj(x -> new Faker().internet().url())
            .toArray(String[]::new);
    }

    private static LocalDate getRandomLocalDateUntilNow() {
        long minDay = LocalDate.of(1970, 1, 1).toEpochDay();
        long maxDay = LocalDate.now().toEpochDay();
        return getRandomLocalDate(minDay, maxDay);
    }

    private static LocalDate getRandomLocalDateFromNow() {
        long minDay = LocalDate.now().toEpochDay();
        long maxDay = LocalDate.of(2050, 12, 31).toEpochDay();
        return getRandomLocalDate(minDay, maxDay);
    }

    private static LocalDate getRandomLocalDate(long minDay, long maxDay) {
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    private static <E extends Enum<E>> E getRandomEnum(Class<E> enumType) {
        return enumType.getEnumConstants()[getRandomIntegerFromRange(0, enumType.getEnumConstants().length)];
    }

    private static String getRandomAlphaNumeric(int size) {
        return RandomStringUtils.randomAlphanumeric(size);
    }

    private static Integer getRandomId() {
        return Integer.valueOf(RandomStringUtils.randomNumeric(5));
    }

    private static String getRandomString(int size) {
        return RandomStringUtils.randomAlphabetic(size);
    }

    private static String getRandomBarcode() {
        return getRandomBarcode(10);
    }

    private static String getRandomBarcode(int size) {
        return RandomStringUtils.randomNumeric(size);
    }

    private static Integer getRandomIntegerFromRange(int min, int max) {
        return new Random().ints(1, min, max).iterator().nextInt();
    }

    private static Float getRandomFloatFromRange(float min, float max) {
        return min + new Random().nextFloat() * (max - min);
    }

    private static CategoryDto getRandomBrand() {
        return CategoryDto.builder()
            .id(getRandomId())
            .name(brandNames.get(getRandomIntegerFromRange(1, brandNames.size()) - 1))
            .parent(CategoryDto.builder()
                .id(0)
                .name("Marcas")
                .build())
            .build();
    }


}
