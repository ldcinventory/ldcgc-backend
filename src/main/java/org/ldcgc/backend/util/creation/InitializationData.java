package org.ldcgc.backend.util.creation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.db.model.category.Responsibility;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.history.ConsumableRegister;
import org.ldcgc.backend.db.model.history.Maintenance;
import org.ldcgc.backend.db.model.history.ToolRegister;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.model.users.Absence;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
import org.ldcgc.backend.db.repository.category.ResponsibilityRepository;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.history.ConsumableRegisterRepository;
import org.ldcgc.backend.db.repository.history.MaintenanceRepository;
import org.ldcgc.backend.db.repository.history.ToolRegisterRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.common.EStatus;
import org.ldcgc.backend.util.common.EStockType;
import org.ldcgc.backend.util.common.ETimeUnit;
import org.ldcgc.backend.util.common.EVStatus;
import org.ldcgc.backend.util.common.EWeekday;
import org.ldcgc.backend.util.process.Files;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.ldcgc.backend.util.common.EStockType.KILOGRAMS;
import static org.ldcgc.backend.util.common.EStockType.UNITS;
import static org.ldcgc.backend.util.conversion.Convert.stringToLocalDate;
import static org.ldcgc.backend.util.conversion.Convert.toFloat;
import static org.ldcgc.backend.util.conversion.Convert.toFloat2Decimals;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitializationData {

    private final UserRepository userRepository;
    private final VolunteerRepository volunteerRepository;
    private final BrandRepository brandRepository;
    private final ResourceTypeRepository resourceTypeRepository;
    private final ResponsibilityRepository responsibilityRepository;
    private final LocationRepository locationRepository;
    private final ToolRepository toolRepository;
    private final ConsumableRepository consumableRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final GroupRepository groupRepository;
    private final ConsumableRegisterRepository consumableRegisterRepository;
    private final ToolRegisterRepository toolRegisterRepository;

    private final JdbcTemplate jdbcTemplate;

    private final PasswordEncoder passwordEncoder;

    @Value("${DB_NAME:mydb}") private String dbName;

    @Value("${LOAD_INITIAL_DATA:false}") private boolean loadInitialData;
    @Value("${LOAD_FROM_CSV:false}") private boolean loadFromCSV;
    @Value("${ONLY_RANDOM_TEST_DATA:false}") private boolean onlyRandomTestData;
    @Value("${RANDOM_TEST_DATA:false}") private boolean randomTestData;
    @Value("${CREATE_TEST_USERS:true}") private boolean createTestUsers;

    @Value("classpath:consumables.csv") private Resource consumablesCSV;
    @Value("classpath:tools.csv") private Resource toolsCSV;

    @Value("classpath:users.csv") private Resource usersCSV;
    @Value("classpath:volunteers.csv") private Resource volunteersCSV;

    private Group group8;
    private List<Brand> brandEntities;
    private Map<String, Brand> brandsMap;
    private List<ResourceType> resourceTypeEntities;
    private Map<String, ResourceType> resourceTypeMap;
    private ZoneOffset systemOffset;
    private long minLocalDateTime;
    private long maxLocalDateTime;
    private LocalDateTime timeIn;
    private LocalDateTime timeOut;

    private final String[] testURLTools = new String[]{"1hi2rxiQgjPZXCQwR7bv9X2uFBNQiA3sU","1tOc34YXR3-0JXGjiii7pbWp5BFC_5GYW","1bHf4Scn5P2FnL9Ni1ETY8tcuHzg7IGK9","1pA9FqP2OLTsZlNxr2_1eRrY_JAjAHWTF","1lkP-rmQX6iVAG9WK2PKAlF9Z_5lywHeD","1YJ7SUpyzg5gNDD07r8mT3p_Zl-GXVZOC"};
    private final String[] testURLConsumables = new String[]{"1CVrLvhmaPKD_OvKFihtF9N_yPl-m6Qmt","1CUVZpUXRabC7NuMB8Sz16rmGGIc3ALfK","1tvxjnQyWFdeYSnwkw6MXd_pwowZVzYcS","1tCjP99iUO3ZR36IoYsJR57bf5DLAtLVE","1oA95QY8sh529LlU5eIsS9epbPLGh_Lgn","195JIjIFdqh-t48oAzebd-dIaIl4CgaSw"};

    @Bean
    @Profile("!pro")
    InitializingBean sendDatabase() {
        if (!loadInitialData) return null;

        if (onlyRandomTestData) return this::createRandomTestData;

        return () -> {
            // execute when changing database version
            jdbcTemplate.execute("ALTER DATABASE \"%s\" REFRESH COLLATION VERSION;".formatted(dbName));
            // set accent-insensitive on searches
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS unaccent;");

            // null user, tool, consumable for when some registers are not permanently deleted (just deactivated)
            createNullRegisters();

            // Ferretería (no es necesario indicar dirección)
            // ==> "Arcón" o "Estantería" estará ubicado en Ferreteria
            // GROUP
            group8 = Group.builder()
                .name("Grupo 8 de Construcción")
                .phoneNumber("+34630480855")
                .build();
            group8 = groupRepository.saveAndFlush(group8);

            Location ferreteria = createFerreteria();
            ferreteria = locationRepository.saveAndFlush(ferreteria);
            log.info("Created main location for group");

            group8.setLocation(ferreteria);
            group8 = groupRepository.saveAndFlush(group8);
            log.info("Created group 8");

            // Guadalajara SR (Calle León Felipe, 6, bajo derecha)
            locationRepository.saveAndFlush(Location.builder()
                    .name("Guadalajara SR")
                    .description("Calle León Felipe, 6, bajo derecha")
                    .url("https://maps.app.goo.gl/cfp7UVDjD3dumBRp7")
                    .level(0)
                    .groupId(group8.getId())
                    .build());
            // Leganés SR Maestro (Sótano del Salón del Reino situado en Calle del Maestro, 13 Leganés)
            locationRepository.saveAndFlush(Location.builder()
                    .name("Leganés SR Maestro")
                    .description("Sótano del Salón del Reino situado en Calle del Maestro, 13 Leganés")
                    .url("https://maps.app.goo.gl/c2tn7Pzwb62SwyVNA")
                    .level(0)
                    .groupId(group8.getId())
                    .build());
            // Parla SR Zurbarán (Salón del Reino situado en Calle Zurbarán 1 posterior Parla)
            locationRepository.saveAndFlush(Location.builder()
                    .name("Parla SR Zurbarán")
                    .description("Salón del Reino situado en Calle Zurbarán 1 posterior Parla")
                    .url("https://maps.app.goo.gl/7yvYEgCqbqeS3Jsm8")
                    .level(0)
                    .groupId(group8.getId())
                    .build());
            // Local/Almacén Cristopher
            locationRepository.saveAndFlush(Location.builder()
                    .name("Local/Almacén Cristopher")
                    .description("Local/Almacén Cristopher")
                    .level(0)
                    .groupId(group8.getId())
                    .build());
            // Local/Almacén Geñi
            locationRepository.saveAndFlush(Location.builder()
                    .name("Local/Almacén Geñi")
                    .description("Local/Almacén Geñi")
                    .level(0)
                    .groupId(group8.getId())
                    .build());
            // Betel
            locationRepository.saveAndFlush(Location.builder()
                    .name("Betel")
                    .description("Sede Nacional, M-108, Km. 5, 28864 Ajalvir, Madrid")
                    .url("https://maps.app.goo.gl/Zv9CVjCPqNW6sbZs6")
                    .level(0)
                    .groupId(group8.getId())
                    .build());
            // SA Ajalvir
            locationRepository.saveAndFlush(Location.builder()
                    .name("SA Ajalvir")
                    .description("Salón de Asambleas de los Testigos Cristianos de Jehová")
                    .url("https://maps.app.goo.gl/bM7CcMEqNygdwhVC9")
                    .level(0)
                    .groupId(group8.getId())
                    .build());
            // Oficina (no es necesario indicar dirección)
            locationRepository.saveAndFlush(Location.builder()
                    .name("Oficina")
                    .description("Oficina")
                    .level(0)
                    .groupId(group8.getId())
                    .build());
            log.info("Created rest of locations");

            // RESOURCE TYPES (select name from categories;)
            // --> resources
            List<String> resourceNames = Arrays.asList("Acabados", "Accesorios", "Alargos", "Albañilería", "Alicatado y solado", "Clima", "Electricidad", "Fontanería", "Herramientas de mano", "Iluminación", "Maquinaria", "Oficina", "Pintura", "Pladur", "Seguridad", "Soldadura", "Sin especificar");

            resourceNames.stream()
                .map(c -> ResourceType.builder()
                    .name(c)
                    .locked(true)
                    .build())
                .forEach(resourceTypeRepository::saveAndFlush);
            log.info("Created resource types");

            // VOLUNTEERS
            if(loadFromCSV) loadVolunteersCSV(group8);
            else loadVolunteers();
            log.info("Created volunteers from {}", loadFromCSV ? "csv" : "random data");

            // CONSUMABLES + TOOLS

            // --> BRANDS (select name from brands;)

            List<String> brandNames = Arrays.asList("Sin marca", "ABAC", "Acesa", "Alyco", "Anean", "Argoco", "Bahco", "Bellota", "Blackwire", "BM-RS", "Boll", "Bosch", "Bossram", "Brüder Mannesmann", "Butsir", "Climaver", "Daher", "Delta", "Deltaplus", "Desa", "Dewalt", "Dexter", "DIN", "Dogher", "DURO", "Duro Meister", "Electrovolt", "ERGO", "Expert", "Ez Fasten", "EZ-Fasten", "Femi", "Fischer Darex", "Forged ", "GICNAC2", "Grespania", "Hermin", "Hilti", "HP", "HR", "IFAM", "IKEA", "Imcoinsa", "INDEX", "Indiuka", "Intercable", "Irazola", "Irimo", "Isover", "Jar", "Kartcher", "Klein Tools", "Knipex", "Kreator", "LEMAN", "Lenovo", "Letratag", "LIMIT", "Loria", "Makita", "Mannesmann", "Metal Works", "Milwaukee", "Mirka", "ML-OK", "MT", "Multi Star", "Ninguna", "Novipro", "Nusac", "Opel", "Palmera", "Panduit", "Pentrilo", "Petzl", "Powerfix", "Profi", "Proiman", "Quilosa", "Retevis", "Rigo", "Rothenberger", "Rubi", "Rubi negra", "Ruvi", "Samsung", "Sanyipace", "Savage", "Schneider", "Sofamel", "SREMTCH", "Stanley", "Starrett", "Stayer", "Stayer Welding", "Svelt", "Syntesi", "Tacklife", "Termiser", "Testo", "TUV", "UKACA", "UNI-T", "UniGrip", "Urceri", "Velour", "Vevor", "Vorel", "Wera", "Werku", "Western Knipex", "Wiesman", "Wiha", "Witte", "Wolfpack", "Würth", "Xiaomi", "Zosi Smart");

            List<Brand> brands = brandNames.stream()
                    .map(b -> Brand.builder()
                            .name(b)
                            .locked(true)
                            .build())
                    .toList();

            brandRepository.saveAllAndFlush(brands);
            log.info("Created brands");

            // REGISTRATION (TOOLS + CONSUMABLES) initial data
            systemOffset = OffsetDateTime.now().getOffset();
            minLocalDateTime = LocalDateTime.of(2023, 1, 1, 0, 0, 0).toEpochSecond(systemOffset);
            maxLocalDateTime = LocalDateTime.now().minusDays(1).toEpochSecond(systemOffset);

            brandEntities = brandRepository.findAll();
            brandsMap = brandEntities.stream().collect(Collectors.toMap(Brand::getName, b -> b));

            resourceTypeEntities = resourceTypeRepository.findAll();
            resourceTypeMap = resourceTypeEntities.stream().collect(Collectors.toMap(ResourceType::getName, b -> b));

            // --> TOOLS
            if(loadFromCSV) loadToolsCSV();
            else loadTools();
            log.info("Created tools from {}", loadFromCSV ? "csv" : "random data");

            // --> CONSUMABLES
            if(loadFromCSV) loadConsumablesCSV();
            else loadConsumables();
            log.info("Created consumables from {}", loadFromCSV ? "csv" : "random data");

            if(randomTestData) createRandomTestData();

            // --> USERS
            List<Responsibility> responsibilities = Stream.of("Coordinador", "Auxiliar de coordinador", "Voluntario")
                .map(r ->
                    Responsibility.builder()
                        .name(r)
                        .locked(true)
                        .build())
                .toList();

            responsibilityRepository.saveAllAndFlush(responsibilities);
            log.info("Created responsibilities");

            List<Responsibility> responsibilitiesEntities = responsibilityRepository.findAll();

            if(createTestUsers) createTestUsers(group8, responsibilitiesEntities);
            else loadUsersCSV();
            log.info("Created users from {}", loadFromCSV ? "csv" : "random data");

        };

    }

    private @NotNull Location createFerreteria() {
        // location
        Location colmenarViejo = new Location("Colmenar Viejo", "Proyecto Colmenar Viejo", 0, group8.getId());

        Location ferreteria = new Location("Ferreteria", colmenarViejo, 1, group8.getId());

        // placements
        List<Location> placementsShelving = Stream.of("Albañilería", "Baúl Acabados", "Baúl Albañilería 1", "Baúl Albañilería 2", "Baúl Albañilería Fina", "Baúl Arneses", "Baúl Clima", "Baúl Electricidad", "Baúl Fontanería", "Baúl Iluminación", "Baúl Pintura", "Baúl Pladur", "Baúl Seguridad Y Salud", "Baúl Soldadura", "Caja De Plástico", "Cajonera Negra", "Carro Rojo Con Cajones", "Estantería")
            .map(p -> new Location(p, ferreteria, 2, group8.getId()))
            .toList();
        ferreteria.setLocations(placementsShelving);

        colmenarViejo.setLocations(List.of(ferreteria));

        colmenarViejo.setGroupId(group8.getId());
        return colmenarViejo;
    }

    private Set<EWeekday> getRandomAvailability() {
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

    private List<Absence> getRandomAbsences(Volunteer volunteer) {
        // number of absences to add
        int numAbsences = new Random().ints(1, 0, 7).iterator().nextInt();

        // list of absences and ranges of days different days of absences
        List<Absence> absences = new ArrayList<>();
        IntStream.range(0, numAbsences).forEach(x -> {
            int rangeOfDays = new Random().ints(1, 0, 7).iterator().nextInt();
            LocalDate randomDate = getRandomFutureDate(false);
            Absence absence = Absence.builder()
                .dateFrom(randomDate)
                .dateTo(randomDate.plusDays(rangeOfDays))
                .volunteer(volunteer)
                .build();
            absences.add(absence);

        });

        absences.sort(Comparator.comparing(Absence::getDateFrom));

        return absences;
    }

    private <E extends Enum<E>> E getRandomEnum(Class<E> enumType) {
        return enumType.getEnumConstants()[getRandomIntegerFromRange(0, enumType.getEnumConstants().length)];
    }

    private Integer getRandomIntegerFromRange(int min, int max) {
        return new Random().ints(1, min, max).iterator().nextInt();
    }

    private Float getRandomFloatFromRange(float min, float max) {
        return min + new Random().nextFloat() * (max - min);
    }

    private LocalDate getRandomDate(boolean includeNullValue) {
        return getRandomDate(includeNullValue,-366, 366);
    }

    private LocalDate getRandomFutureDate(boolean includeNullValue) {
        return getRandomDate(includeNullValue, 0, 366);
    }

    private LocalDate getRandomPastDate(boolean includeNullValue) {
        return getRandomDate(includeNullValue, -366, 0);
    }

    private LocalDate getRandomDate(boolean includeNullValue, int pastDays, int futureDays) {
        if(includeNullValue && ThreadLocalRandom.current().nextBoolean())
            return null;
        return LocalDate.now().plusDays(ThreadLocalRandom.current().nextInt(pastDays, futureDays));
    }

    private LocalDate calculateNextMaintenance(ETimeUnit timeUnit, Integer period, LocalDate fromDate) {
        LocalDate date = ObjectUtils.defaultIfNull(fromDate, LocalDate.now());
        return switch (timeUnit) {
            case HOURS   -> date;
            case DAYS    -> date.plusDays(period);
            case WEEKS   -> date.plusWeeks(period);
            case MONTHS  -> date.plusMonths(period);
            case YEARS   -> date.plusYears(period);
            case NEVER,
                 UNKNOWN -> null;
        };
    }

    private enum EResourceType {
        TOOL, CONSUMABLE
    }

    private String[] getRandomURLs(EResourceType resourceType) {
        List<String> urls = new ArrayList<>(List.of(resourceType.equals(EResourceType.TOOL) ? testURLTools : testURLConsumables));
        Collections.shuffle(urls);

        return urls.subList(0, getRandomIntegerFromRange(0, urls.size() - 1)).toArray(String[]::new);

    }

    private Tool getRandomTool() {
        int maintenancePeriod = getRandomIntegerFromRange(0,10);
        ETimeUnit maintenanceTime = getRandomEnum(ETimeUnit.class);
        LocalDate lastMaintenanceDate = getRandomPastDate(true);
        LocalDate nextMaintenance = calculateNextMaintenance(maintenanceTime, maintenancePeriod, lastMaintenanceDate);

        return Tool.builder()
            .barcode(RandomStringUtils.randomAlphanumeric(10))
            .brand(brandEntities.get(getRandomIntegerFromRange(0, brandEntities.size() - 1)))
            .resourceType(resourceTypeEntities.get(getRandomIntegerFromRange(0, resourceTypeEntities.size() - 1)))
            .name(new Random().nextBoolean() ? new Faker().appliance().brand() : new Faker().brand().watch())
            .model(String.format("%s %s", new Faker().coffee().variety(), new Faker().ancient().titan()))
            .description(new Faker().lorem().sentence())
            .weight(getRandomFloatFromRange(1,100))
            .stockWeightType(getRandomEnum(EStockType.class))
            .price(new Faker().random().nextFloat())
            .purchaseDate(getRandomDate(false))
            .urlImages(getRandomURLs(EResourceType.TOOL))
            .maintenancePeriod(maintenancePeriod)
            .maintenanceTime(maintenanceTime)
            .lastMaintenance(lastMaintenanceDate)
            .nextMaintenance(nextMaintenance)
            .status(getRandomEnum(EStatus.class))
            .location(locationRepository.getRandomLocation())
            .group(groupRepository.getRandomGroup())
            .build();
    }

    private Consumable getRandomConsumable() {
        return Consumable.builder()
            .barcode(RandomStringUtils.randomAlphanumeric(10))
            .brand(brandEntities.get(getRandomIntegerFromRange(0, brandEntities.size() - 1)))
            .resourceType(resourceTypeEntities.get(getRandomIntegerFromRange(0, resourceTypeEntities.size() - 1)))
            .name(new Faker().funnyName().name())
            .model(new Faker().coffee().variety())
            .description(new Faker().lorem().sentence())
            .price(new Faker().random().nextFloat())
            .purchaseDate(getRandomDate(false))
            .urlImages(getRandomURLs(EResourceType.CONSUMABLE))
            .stock(new Random().nextBoolean() ? getRandomFloatFromRange(1,100) : 0.0f)
            .stockType(getRandomEnum(EStockType.class))
            .minStock(getRandomFloatFromRange(0,100))
            .quantityEachItem(getRandomFloatFromRange(1,100))
            .location(locationRepository.getRandomLocation())
            .group(groupRepository.getRandomGroup())
            .build();
    }

    private Volunteer getRandomVolunteer() {
        Volunteer volunteer = Volunteer.builder()
            .name(new Faker().name().firstName())
            .lastName(String.format("%s %s", new Faker().name().lastName(), new Faker().name().lastName()))
            .builderAssistantId(RandomStringUtils.randomAlphanumeric(8))
            .availability(getRandomAvailability())
            .status(getRandomEnum(EVStatus.class))
            .group(groupRepository.getRandomGroup())
            .build();
        volunteer.setAbsences(getRandomAbsences(volunteer));

        return volunteer;
    }

    // load random data
    private void loadVolunteers() {
        IntStream.range(0, 5_000).parallel().forEach(i -> volunteerRepository.saveAndFlush(getRandomVolunteer()));
    }

    private void loadTools() {
        IntStream.range(0, 1_000).parallel().forEach(i -> toolRepository.saveAndFlush(getRandomTool()));
    }

    private void loadToolsRegistration() {
        List<Integer> openedToolRegisters = new ArrayList<>();
        IntStream.range(0, 3_000)
            .parallel()
            .forEach(i -> {
                Tool tool = toolRepository.getRandomTool();
                boolean isOpen = !openedToolRegisters.contains(tool.getId());

                if(isOpen)
                    openedToolRegisters.add(tool.getId());

                toolRegisterRepository.saveAndFlush(
                    ToolRegister.builder()
                        .registerFrom(timeIn)
                        .registerTo(isOpen ? null : timeOut)
                        .tool(toolRepository.getRandomTool())
                        .volunteer(volunteerRepository.getRandomVolunteer())
                        .build());
            });
    }

    private void loadConsumables() {
        IntStream.range(0, 2_000).parallel().forEach(i -> consumableRepository.saveAndFlush(getRandomConsumable()));
    }

    private void createRandomTestData() {
        systemOffset = OffsetDateTime.now().getOffset();
        minLocalDateTime = LocalDateTime.of(2023, 1, 1, 0, 0, 0).toEpochSecond(systemOffset);
        maxLocalDateTime = LocalDateTime.now().minusDays(1).toEpochSecond(systemOffset);

        timeIn = LocalDateTime.ofEpochSecond(ThreadLocalRandom.current().nextLong(minLocalDateTime, maxLocalDateTime), 0, systemOffset);
        timeOut = timeIn.plusDays(new Random().nextInt(0, (int) ChronoUnit.DAYS.between(timeIn, LocalDateTime.now())));

        // --> TOOLS REGISTRATION
        loadToolsRegistration();
        log.info("Created random registration data for tools");

        // --> CONSUMABLES REGISTRATION
        loadConsumablesRegistration();
        log.info("Created random registration data for consumables");

        // --> MAINTENANCE
        loadMaintenance();
        log.info("Created random data for maintenance");
    }

    private void loadMaintenance() {
        IntStream.range(0, 1_000)
            .parallel()
            .forEach(mFieldList -> {
                final Tool tool = toolRepository.getRandomTool();
                final Volunteer volunteer = volunteerRepository.getRandomVolunteer();

                LocalDate dateIn = LocalDate.ofInstant(Instant.ofEpochSecond(ThreadLocalRandom.current().nextLong(minLocalDateTime, maxLocalDateTime)), systemOffset);
                LocalDate dateOut = dateIn.plusDays(new Random().nextInt(0, (int) ChronoUnit.DAYS.between(dateIn, LocalDateTime.now())));

                maintenanceRepository.saveAndFlush(
                    Maintenance.builder()
                        .inRegistration(dateIn)
                        .outRegistration(dateOut)
                        .details(new Faker().restaurant().description())
                        .urlImages(new Faker().internet().url())
                        .tool(tool)
                        .volunteer(volunteer)
                        .inStatus(getRandomEnum(EStatus.class))
                        .outStatus(getRandomEnum(EStatus.class))
                        .build());
        });
    }

    private void loadConsumablesRegistration() {
        List<Integer> openedConsumableRegisters = new ArrayList<>();
        IntStream.range(0, 3_000)
            .parallel()
            .forEach(i -> {
                LocalDateTime timeIn = LocalDateTime.ofEpochSecond(ThreadLocalRandom.current().nextLong(minLocalDateTime, maxLocalDateTime), 0, systemOffset);
                LocalDateTime timeOut = timeIn.plusDays(new Random().nextInt(0, (int) ChronoUnit.DAYS.between(timeIn, LocalDateTime.now())));
                float amountRequest = new Random().nextFloat(0.01f, 20.00f);
                float amountReturn = new Random().nextFloat(0.00f, amountRequest);

                Consumable consumable = consumableRepository.getRandomConsumable();
                boolean isOpen = !openedConsumableRegisters.contains(consumable.getId());

                if(isOpen)
                    openedConsumableRegisters.add(consumable.getId());

                consumableRegisterRepository.saveAndFlush(
                    ConsumableRegister.builder()
                        .registerFrom(timeIn)
                        .registerTo(isOpen ? null : timeOut)
                        .stockAmountRequest(amountRequest)
                        .stockAmountReturn(isOpen ? null : amountReturn)
                        .consumable(consumable)
                        .volunteer(volunteerRepository.getRandomVolunteer())
                        .closedRegister(!isOpen)
                        .build());
            });
    }

    private void createTestUsers(Group group, List<Responsibility> responsibilities) {
        userRepository.saveAndFlush(User.builder()
            .email("admin@admin")
            .password(passwordEncoder.encode("admin"))
            .group(group)
            .role(ERole.ROLE_ADMIN)
            .responsibility(responsibilities.stream()
                .filter(r -> r.getName().equals("Coordinador")).findFirst()
                .orElse(null))
            .enabled(true)
            .build());

        userRepository.saveAndFlush(User.builder()
            .email("noeula@admin")
            .password(passwordEncoder.encode("admin"))
            .group(group)
            .role(ERole.ROLE_ADMIN)
            .acceptedEULA(LocalDateTime.now())
            .acceptedEULAManager(LocalDateTime.now())
            .responsibility(responsibilities.stream()
                .filter(r -> r.getName().equals("Coordinador")).findFirst()
                .orElse(null))
            .enabled(true)
            .build());

        userRepository.saveAndFlush(User.builder()
            .email("noeula@adminv")
            .password(passwordEncoder.encode("admin"))
            .group(group)
            .role(ERole.ROLE_ADMIN)
            .acceptedEULA(LocalDateTime.now())
            .acceptedEULAManager(LocalDateTime.now())
            .volunteer(volunteerRepository.getRandomVolunteer())
            .responsibility(responsibilities.stream()
                .filter(r -> r.getName().equals("Coordinador")).findFirst()
                .orElse(null))
            .enabled(true)
            .build());

        userRepository.saveAndFlush(User.builder()
            .email("manager@manager")
            .password(passwordEncoder.encode("manager"))
            .group(group)
            .role(ERole.ROLE_MANAGER)
            .responsibility(responsibilities.stream()
                .filter(r -> r.getName().equals("Auxiliar de coordinador")).findFirst()
                .orElse(null))
            .enabled(true)
            .build());

        userRepository.saveAndFlush(User.builder()
            .email("noeula@manager")
            .password(passwordEncoder.encode("manager"))
            .group(group)
            .role(ERole.ROLE_MANAGER)
            .acceptedEULA(LocalDateTime.now())
            .acceptedEULAManager(LocalDateTime.now())
            .responsibility(responsibilities.stream()
                .filter(r -> r.getName().equals("Coordinador")).findFirst()
                .orElse(null))
            .enabled(true)
            .build());

        userRepository.saveAndFlush(User.builder()
            .email("noeula@managerv")
            .password(passwordEncoder.encode("manager"))
            .group(group)
            .role(ERole.ROLE_MANAGER)
            .acceptedEULA(LocalDateTime.now())
            .acceptedEULAManager(LocalDateTime.now())
            .volunteer(volunteerRepository.getRandomVolunteer())
            .responsibility(responsibilities.stream()
                .filter(r -> r.getName().equals("Coordinador")).findFirst()
                .orElse(null))
            .enabled(true)
            .build());

        userRepository.saveAndFlush(User.builder()
            .email("user@user")
            .password(passwordEncoder.encode("user"))
            .group(group)
            .role(ERole.ROLE_USER)
            .responsibility(responsibilities.stream()
                .filter(r -> r.getName().equals("Voluntario")).findFirst()
                .orElse(null))
            .enabled(true)
            .build());

        userRepository.saveAndFlush(User.builder()
            .email("noeula@user")
            .password(passwordEncoder.encode("user"))
            .group(group)
            .role(ERole.ROLE_USER)
            .acceptedEULA(LocalDateTime.now())
            .responsibility(responsibilities.stream()
                .filter(r -> r.getName().equals("Coordinador")).findFirst()
                .orElse(null))
            .enabled(true)
            .build());

        userRepository.saveAndFlush(User.builder()
            .email("noeula@userv")
            .password(passwordEncoder.encode("user"))
            .group(group)
            .role(ERole.ROLE_USER)
            .acceptedEULA(LocalDateTime.now())
            .volunteer(volunteerRepository.getRandomVolunteer())
            .responsibility(responsibilities.stream()
                .filter(r -> r.getName().equals("Voluntario")).findFirst()
                .orElse(null))
            .enabled(true)
            .build());
    }

    // load from CSV
    private void loadVolunteersCSV(Group group) {
        // VOLUNTEERS
        // select builderAssistantId, name, surname, active from volunteers;

        List<List<String>> volunteers = Files.getContentFromCSV(volunteersCSV, ',', true);

        Map<String, Volunteer> volunteerEntities = new HashMap<>();
        volunteers.forEach(vFieldList -> {
            if(Objects.nonNull(volunteerEntities.get(vFieldList.get(1))))
                return;

            Volunteer volunteer = Volunteer.builder()
                .builderAssistantId(vFieldList.get(0))
                .name(vFieldList.get(1))
                .lastName(vFieldList.get(2))
                .status(EVStatus.ACTIVE)
                .group(group)
                .build();
            volunteerEntities.put(vFieldList.get(1), volunteer);
        });

        List<Volunteer> volunteerEntitiesList = volunteerEntities.values().stream().toList();

        for(int i = 0; i < volunteerEntitiesList.size(); i += 500) {
            if(i + 500 > volunteerEntitiesList.size()) {
                volunteerRepository.saveAllAndFlush(volunteerEntitiesList.subList(i, volunteerEntitiesList.size() - 1));
                continue;
            }
            volunteerRepository.saveAllAndFlush(volunteerEntitiesList.subList(i, i + 500));
        }
    }

    private void loadToolsCSV() {
        // --> TOOLS
        // select t.Barcode, b.Name as brand, t.Model, t.Name as name,
        //                   t.Description, c.Name as category, t.Weight, t.Price, t.PurchaseDate
        //            from Tools t, Brands b, Categories c
        //            where t.BrandId = b.BrandId
        //            and t.CategoryId = c.CategoryId;

        Location location = locationRepository.getLocationByName("Ferretería").orElse(null);

        List<List<String>> tools = Files.getContentFromCSV(toolsCSV, ',', false);
        Map<String, Tool> toolEntities = new HashMap<>();
        tools.forEach(tFieldList -> {
            Tool tool = Tool.builder()
                .barcode(toolEntities.get(tFieldList.get(0)) != null
                    ? RandomStringUtils.randomAlphanumeric(10).toUpperCase()
                    : tFieldList.get(0))
                .brand(StringUtils.isBlank(tFieldList.get(1))
                    ? brandsMap.get("<empty>")
                    : brandsMap.get(tFieldList.get(1)))
                .model(tFieldList.get(2))
                .name(tFieldList.get(3))
                .description(tFieldList.get(4))
                .location(location)
                .group(group8)
                .resourceType(resourceTypeMap.get(tFieldList.get(5)))
                .status(EStatus.AVAILABLE)
                .weight(toFloat(tFieldList.get(6)))
                .stockWeightType(KILOGRAMS)
                .price(toFloat(tFieldList.get(7)))
                .purchaseDate(tFieldList.get(8).length() < 10 ? null : stringToLocalDate(tFieldList.get(8).substring(0, 10), "yyyy-MM-dd"))
                .urlImages(new String[]{"url-imagen-1", "url-imagen-2"})
                .maintenanceTime(getRandomEnum(ETimeUnit.class))
                .maintenancePeriod(getRandomIntegerFromRange(1,30))
                .lastMaintenance(getRandomPastDate(true))
                .build();
            toolEntities.put(tool.getBarcode(), tool);
        });
        toolRepository.saveAll(toolEntities.values());
    }

    private void loadConsumablesCSV() {
        // --> CONSUMABLES
        // select cn.Barcode, b.Name as brand, cn.Model, cn.Name as name,
        //                         cn.Description, c.Name as category, cn.Price, cn.PurchaseDate,
        //                         cn.Stock, cn.MinimumStock
        //                  from Consumables cn, Brands b, Categories c
        //                  where cn.BrandId = b.BrandId
        //                  and cn.CategoryId = c.CategoryId;

        Location location = locationRepository.getLocationByName("Ferretería").orElse(null);

        List<List<String>> consumables = Files.getContentFromCSV(consumablesCSV, ',', false);
        Map<String, Consumable> consumableEntities = new HashMap<>();
        for (List<String> cFieldList : consumables) {
            int stockInt = getRandomIntegerFromRange(2, 10);

            float quantityEachItem = StringUtils.isBlank(cFieldList.get(8))
                ? getRandomFloatFromRange(0.01f, 10.00f)
                : Float.parseFloat(cFieldList.get(8)) / stockInt;

            Float stock = StringUtils.isBlank(cFieldList.get(8))
                ? (float) stockInt * quantityEachItem
                : Float.parseFloat(cFieldList.get(8));

            Float minStock = StringUtils.isBlank(cFieldList.get(9))
                ? (float) getRandomIntegerFromRange(1, stockInt) * quantityEachItem
                : Float.parseFloat(cFieldList.get(9));

            Consumable consumable = Consumable.builder()
                .barcode(consumableEntities.get(cFieldList.get(0)) != null
                    ? RandomStringUtils.randomAlphanumeric(10).toUpperCase()
                    : cFieldList.get(0))
                .brand(brandsMap.get(cFieldList.get(1)))
                .model(cFieldList.get(2))
                .name(cFieldList.get(3))
                .description(cFieldList.get(4))
                .location(location)
                .group(group8)
                .resourceType(resourceTypeMap.get(cFieldList.get(5)))
                .price(toFloat2Decimals(cFieldList.get(6)))
                .purchaseDate(stringToLocalDate(cFieldList.get(7).substring(0, 10), "yyyy-MM-dd"))
                .quantityEachItem(quantityEachItem)
                .stock(stock)
                .stockType(getRandomEnum(EStockType.class))
                .minStock(minStock)
                .urlImages(new String[]{"url-imagen-1", "url-imagen-2"})
                .build();
            consumableEntities.put(consumable.getBarcode(), consumable);
        }
        consumableRepository.saveAll(consumableEntities.values());
    }

    private void loadUsersCSV() {
        List<List<String>> users = Files.getContentFromCSV(usersCSV, ',', true);
        users.forEach(userFields -> {
            User user = User.builder()
                .email(userFields.get(4))
                .password(passwordEncoder.encode(userFields.get(3)))
                .role(Integer.parseInt(userFields.get(6)) == 3 ? ERole.ROLE_ADMIN :
                    Integer.parseInt(userFields.get(6)) == 2 ? ERole.ROLE_MANAGER :
                        ERole.ROLE_USER)
                .responsibility(responsibilityRepository.findByName("Voluntario").orElse(null))
                .group(group8)
                .acceptedEULA(LocalDateTime.now())
                .acceptedEULAManager(Integer.parseInt(userFields.get(6)) > 1 ? LocalDateTime.now() : null)
                .build();
            Volunteer volunteer = volunteerRepository.getRandomVolunteer();
            user.setVolunteer(volunteer);

            userRepository.saveAndFlush(user);
        });
    }

    // null registers
    private void createNullRegisters() {
        userRepository.saveAndFlush(User.builder()
            .email("null")
            .password(passwordEncoder.encode(RandomStringUtils.randomAlphanumeric(20)))
            .role(ERole.ROLE_NULL)
            .enabled(false)
            .build());

        volunteerRepository.saveAndFlush(Volunteer.builder()
            .name("null")
            .status(EVStatus.LOCKED)
            .build());

        toolRepository.saveAndFlush(Tool.builder()
            .barcode("null")
            .name("null")
            .enabled(false)
            .build());

        consumableRepository.saveAndFlush(Consumable.builder()
            .barcode("null")
            .name("null")
            .quantityEachItem(0.0f)
            .stock(0.0f)
            .stockType(UNITS)
            .enabled(false)
            .build());

        Location location = Location.builder().name("Sin localización").level(0).storesResources(true).groupId(0).build();
        Location warehouse = Location.builder().name("Sin almacén").level(0).storesResources(true).groupId(0).build();
        Location placement = Location.builder().name("Sin ubicación").level(0).storesResources(true).groupId(0).build();

        placement.setParent(warehouse);
        warehouse.setParent(location);
        warehouse.setLocations(List.of(placement));
        location.setLocations(List.of(warehouse));

        locationRepository.saveAndFlush(location);

    }

}
