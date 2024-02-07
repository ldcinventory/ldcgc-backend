package org.ldcgc.backend.base.mock;

import net.datafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.util.common.EStockType;

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
            .model(getRandomAlphaNumeric(getRandomFromRange(3, 10)))
            .brand(getRandomBrand())
            .description(new Faker().text().text())
            .price(new Faker().random().nextFloat())
            .name(getRandomString(getRandomFromRange(5, 15)))
            .stock(getRandomFromRange(1,100))
            .stockType(getRandomStockType())
            .minStock(getRandomFromRange(0,100))
            .purchaseDate(getRandomLocalDate())
            .urlImages(getRandomURLs())
            .category(getRandomCategory())
            .location(getRandomLocation())
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
        return null;
    }

    private static CategoryDto getRandomCategory() {
        return CategoryDto.builder()
            .id(getRandomId())
            .name(new Faker().brand().watch())
            .build();
    }

    private static String[] getRandomURLs() {
        return IntStream.rangeClosed(1, getRandomFromRange(2, 4))
            .mapToObj(x -> new Faker().internet().url())
            .toArray(String[]::new);
    }

    private static LocalDate getRandomLocalDate() {
        long minDay = LocalDate.of(1970, 1, 1).toEpochDay();
        long maxDay = LocalDate.now().toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    private static EStockType getRandomStockType() {
        return EStockType.values()[getRandomFromRange(0, EStockType.values().length - 1)];
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

    private static Integer getRandomFromRange(int min, int max) {
        return new Random().ints(1, min, max).iterator().nextInt();
    }

    private static CategoryDto getRandomBrand() {
        return CategoryDto.builder()
            .id(getRandomId())
            .name(brandNames.get(getRandomFromRange(1, brandNames.size()) - 1))
            .parent(CategoryDto.builder()
                .id(0)
                .name("Marcas")
                .build())
            .build();
    }


}
