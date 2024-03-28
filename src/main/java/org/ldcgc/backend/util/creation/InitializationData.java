package org.ldcgc.backend.util.creation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.ldcgc.backend.util.common.EWeekday;
import org.ldcgc.backend.util.process.Files;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
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

import static org.ldcgc.backend.util.conversion.Convert.toFloat;
import static org.ldcgc.backend.util.conversion.Convert.toFloat2Decimals;
import static org.ldcgc.backend.util.conversion.Convert.stringToLocalDate;

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

    @Value("${DB_NAME:mydb}")
    private String dbName;

    @Value("${LOAD_INITIAL_DATA:false}")
    private boolean loadData;

    @Value("${TOOLS_REGISTRATION_TEST_DATA:false}") private boolean toolsRegistrationTestData;
    @Value("${CONSUMABLES_REGISTRATION_TEST_DATA:false}") private boolean consumablesRegistrationTestData;

    @Value("classpath:chests.csv")
    org.springframework.core.io.Resource chestsCSV;
    @Value("classpath:chestRegistration.csv")
    org.springframework.core.io.Resource chestRegisterCSV;

    @Value("classpath:consumables.csv")
    org.springframework.core.io.Resource consumablesCSV;
    @Value("classpath:tools.csv")
    org.springframework.core.io.Resource toolsCSV;
    @Value("classpath:maintenance.csv")
    org.springframework.core.io.Resource maintenanceCSV;

    @Value("classpath:users.csv")
    org.springframework.core.io.Resource usersCSV;
    @Value("classpath:volunteers.csv")
    org.springframework.core.io.Resource volunteersCSV;
    @Value("classpath:tool_register.csv")
    org.springframework.core.io.Resource toolRegisterCSV;

    @Bean
    @Profile("!pro")
    InitializingBean sendDatabase() {
        if (!loadData) return null;

        return () -> {
            // execute when changing database version
            jdbcTemplate.execute("ALTER DATABASE \"%s\" REFRESH COLLATION VERSION;".formatted(dbName));
            // set accent-insensitive on searches
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS unaccent;");

            // TODO LOCATIONS (waiting for more info)

            //List<String> locations = List.of("Guadalajara SR", "Salón Hilario Sangrador", "Oficina", "Estantería 2", "Ferretería", "Almacén C/Carrascales", "SR Getafe", "Estantería 1", "Arcón-suelo 2", "Arcón-suelo 1", "Arcón-medio 2", "Arcón-medio 1", "Betel");

            // Guadalajara SR (Calle León Felipe, 6, bajo derecha)
            locationRepository.saveAndFlush(Location.builder()
                    .name("Guadalajara SR")
                    .description("Calle León Felipe, 6, bajo derecha")
                    .url("https://maps.app.goo.gl/cfp7UVDjD3dumBRp7")
                    .level(0)
                    .build());
            // Leganés SR Maestro (Sótano del Salón del Reino situado en Calle del Maestro, 13 Leganés)
            locationRepository.saveAndFlush(Location.builder()
                    .name("Leganés SR Maestro")
                    .description("Sótano del Salón del Reino situado en Calle del Maestro, 13 Leganés")
                    .url("https://maps.app.goo.gl/c2tn7Pzwb62SwyVNA")
                    .level(0)
                    .build());
            // Parla SR Zurbarán (Salón del Reino situado en Calle Zurbarán 1 posterior Parla)
            locationRepository.saveAndFlush(Location.builder()
                    .name("Parla SR Zurbarán")
                    .description("Salón del Reino situado en Calle Zurbarán 1 posterior Parla")
                    .url("https://maps.app.goo.gl/7yvYEgCqbqeS3Jsm8")
                    .level(0)
                    .build());
            // Local/Almacén Cristopher
            locationRepository.saveAndFlush(Location.builder()
                    .name("Local/Almacén Cristopher")
                    .description("Local/Almacén Cristopher")
                    .level(0)
                    .build());
            // Local/Almacén Geñi
            locationRepository.saveAndFlush(Location.builder()
                    .name("Local/Almacén Geñi")
                    .description("Local/Almacén Geñi")
                    .level(0)
                    .build());
            // Betel
            locationRepository.saveAndFlush(Location.builder()
                    .name("Betel")
                    .description("Sede Nacional, M-108, Km. 5, 28864 Ajalvir, Madrid")
                    .url("https://maps.app.goo.gl/Zv9CVjCPqNW6sbZs6")
                    .level(0)
                    .build());
            // SA Ajalvir
            locationRepository.saveAndFlush(Location.builder()
                    .name("SA Ajalvir")
                    .description("Salón de Asambleas de los Testigos Cristianos de Jehová")
                    .url("https://maps.app.goo.gl/bM7CcMEqNygdwhVC9")
                    .level(0)
                    .build());
            // Oficina (no es necesario indicar dirección)
            locationRepository.saveAndFlush(Location.builder()
                    .name("Oficina")
                    .description("Oficina")
                    .level(0)
                    .build());
            // Ferreteria (no es necesario indicar dirección)
            // ==> "Arcón" o "Estantería" estará ubicado en Ferreteria
            Location ferreteria = new Location("Ferretería", 0);
            ferreteria.setLocations(List.of(
                    new Location("Estantería 1", ferreteria, 1),
                    new Location("Estantería 2", ferreteria, 1),
                    new Location("Arcón-suelo 1", ferreteria, 1),
                    new Location("Arcón-suelo 2", ferreteria, 1),
                    new Location("Arcón-medio 1", ferreteria, 1),
                    new Location("Arcón-medio 2", ferreteria, 1)
            ));

            ferreteria = locationRepository.saveAndFlush(ferreteria);

            Map<String, Location> locationMap = locationRepository.findAllByLevel(0).stream().collect(Collectors.toMap(Location::getName, l -> l));

            // CHEST
            // select c.Name, REPLACE(REPLACE(l.Name, CHAR(13), ''), CHAR(10), '')
            // from Chests c, Locations l
            // where c.LocationId = l.LocationId;

            List<List<String>> chests = Files.getContentFromCSV(chestsCSV, ',', false);

            chests.forEach(c -> {
                Location entityFromMap = locationMap.get(c.get(1));
                entityFromMap.getLocations().add(Location.builder()
                    .name(c.get(0))
                    .parent(entityFromMap)
                    .description(c.get(0))
                    .level(2)
                    .build());
                locationRepository.saveAndFlush(entityFromMap);
            });


            // GROUP

            final Group _8g = groupRepository.saveAndFlush(Group.builder()
                    .name("Grupo 8 de Construcción")
                    .phoneNumber("+34630480855")
                    .location(ferreteria)
                    .build());

            // CATEGORIES (select name from categories;)

            // --> resources
            List<String> resourceNames = Arrays.asList("Acabados", "Accesorios", "Alargos", "Albañilería", "Alicatado y solado", "Clima", "Electricidad", "Fontanería", "Herramientas de mano", "Iluminación", "Maquinaria", "Oficina", "Pintura", "Pladur", "Seguridad", "Soldadura");

            List<ResourceType> resourceTypeList = resourceNames.stream()
                    .map(c -> ResourceType.builder()
                            .name(c)
                            .locked(true)
                            .build())
                    .toList();

            resourceTypeRepository.saveAllAndFlush(resourceTypeList);

            // VOLUNTEERS (select builderAssistantId, name, surname, active from volunteers;)

            List<List<String>> volunteers = Files.getContentFromCSV(volunteersCSV, ',', true);
            Map<String, Volunteer> volunteerEntities = new HashMap<>();
            volunteers.forEach(vFieldList -> {
                if(Objects.nonNull(volunteerEntities.get(vFieldList.get(1))))
                    return;

                Volunteer volunteer = Volunteer.builder()
                    .builderAssistantId(vFieldList.get(1))
                    .name(vFieldList.get(2))
                    .lastName(vFieldList.get(3))
                    .isActive(Boolean.parseBoolean(vFieldList.get(4)))
                    .group(_8g)
                    .availability(getRandomAvailability())
                    .build();
                volunteer.setAbsences(getRandomAbsences(volunteer));
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

            // CONSUMABLES + TOOLS

            // --> BRANDS (select name from brands;)

            List<String> brandNames = Arrays.asList("<empty>", "ABAC MONTECARLO", "Bahco", "Bellota", "Bellota 5894-150", "Blackwire", "bo", "Bosch", "Climaver", "Deltaplus", "Desa", "Dewalt", "Disponible", "EZ-Fasten", "Femi", "Fischer Darex", "Forged ", "GRESPANIA", "Hermin", "Hilti", "HP", "IFAM", "INDEX", "Irazola", "Irimo", "Kartcher", "Knipex", "Lenovo", "Loria", "Makita", "Mannesmann", "Metal Works", "Milwaukee", "Mirka", "ML-OK", "Novipro", "Nusac", "OPEL", "Palmera", "Panduit", "Pentrilo", "Petzl", "Powerfix", "Proiman", "Quilosa", "Retevis", "Rothenberger", "Rubi", "Rubi negra", "Samsung", "Schneider", "Stanley", "Stayer", "Svelt", "Tacklife", "Testo", "UNI-T", "Urceri", "Velour", "Vorel", "Würth", "WERKU", "Wiha", "Xiaomi", "Zosi Smart");

            List<Brand> brands = brandNames.stream()
                    .map(b -> Brand.builder()
                            .name(b)
                            .locked(true)
                            .build())
                    .toList();

            brandRepository.saveAllAndFlush(brands);

            // REGISTRATION (TOOLS + CONSUMABLES) init data
            ZoneOffset systemOffset = OffsetDateTime.now().getOffset();
            long minLocalDateTime = LocalDateTime.of(2023, 1, 1, 0, 0, 0).toEpochSecond(systemOffset);
            long maxLocalDateTime = LocalDateTime.now().minusDays(1).toEpochSecond(systemOffset);

            // --> TOOLS (select t.Barcode, b.Name as brand, t.Model, t.Name as name,
            //                   t.Description, c.Name as category, t.Weight, t.Price, t.PurchaseDate
            //            from Tools t, Brands b, Categories c
            //            where t.BrandId = b.BrandId
            //            and t.CategoryId = c.CategoryId;)

            List<Brand> brandEntities = brandRepository.findAll();
            Map<String, Brand> brandsMap = brandEntities.stream().collect(Collectors.toMap(Brand::getName, b -> b));

            List<ResourceType> resourceTypeEntities = resourceTypeRepository.findAll();
            Map<String, ResourceType> resourceCategoriesMap = resourceTypeEntities.stream().collect(Collectors.toMap(ResourceType::getName, b -> b));

            // TODO check status when final migration
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
                    .group(_8g)
                    .resourceType(resourceCategoriesMap.get(tFieldList.get(5)))
                    .status(EStatus.AVAILABLE)
                    .weight(toFloat(tFieldList.get(6)))
                    .stockWeightType(EStockType.KILOGRAMS)
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

            // TOOLS REGISTRATION
            List<Integer> openedToolRegisters = new ArrayList<>();
            if (toolsRegistrationTestData)
                IntStream.range(0, 3_000)
                    .parallel()
                    .forEach(i -> {
                        LocalDateTime timeIn = LocalDateTime.ofEpochSecond(ThreadLocalRandom.current().nextLong(minLocalDateTime, maxLocalDateTime), 0, systemOffset);
                        LocalDateTime timeOut = timeIn.plusDays(new Random().nextInt(0, (int) ChronoUnit.DAYS.between(timeIn, LocalDateTime.now())));

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

            // --> CONSUMABLES (select cn.Barcode, b.Name as brand, cn.Model, cn.Name as name,
            //                         cn.Description, c.Name as category, cn.Price, cn.PurchaseDate,
            //                         cn.Stock, cn.MinimumStock
            //                  from Consumables cn, Brands b, Categories c
            //                  where cn.BrandId = b.BrandId
            //                  and cn.CategoryId = c.CategoryId;)

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
                    .group(_8g)
                    .resourceType(resourceCategoriesMap.get(cFieldList.get(5)))
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

            // CONSUMABLES REGISTRATION
            List<Integer> openedConsumableRegisters = new ArrayList<>();
            if (consumablesRegistrationTestData)
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

            List<List<String>> maintenance = Files.getContentFromCSV(maintenanceCSV, ',', false);

            maintenance.parallelStream().forEach(mFieldList -> {
                final Tool tool = toolRepository.findFirstByBarcode(mFieldList.get(3)).orElse(null);
                final Volunteer volunteer = volunteerRepository.findByBuilderAssistantId(mFieldList.get(4)).orElse(null);
                maintenanceRepository.saveAndFlush(Maintenance.builder()
                    .outRegistration(stringToLocalDate(mFieldList.get(0).substring(0, 10), "yyyy-MM-dd"))
                    .details(mFieldList.get(1))
                    .urlImages(mFieldList.get(2))
                    .tool(tool)
                    .volunteer(volunteer)
                    .inStatus(EStatus.AVAILABLE)
                    .outStatus(EStatus.AVAILABLE)
                    .build());
            });

            // USERS

            List<Responsibility> responsibilities = Stream.of("Coordinador", "Auxiliar de coordinador", "Voluntario")
                .map(r ->
                    Responsibility.builder()
                        .name(r)
                        .locked(true)
                        .build())
                .toList();

            responsibilityRepository.saveAllAndFlush(responsibilities);

            List<Responsibility> responsibilitiesEntities = responsibilityRepository.findAll();

            userRepository.saveAndFlush(User.builder()
                    .email("admin@admin")
                    .password(passwordEncoder.encode("admin"))
                    .group(_8g)
                    .role(ERole.ROLE_ADMIN)
                    .responsibility(responsibilitiesEntities.stream()
                            .filter(r -> r.getName().equals("Coordinador")).findFirst()
                            .orElse(null))
                    .build());

            userRepository.saveAndFlush(User.builder()
                .email("noeula@admin")
                .password(passwordEncoder.encode("admin"))
                .group(_8g)
                .role(ERole.ROLE_ADMIN)
                .acceptedEULA(LocalDateTime.now())
                .acceptedEULAManager(LocalDateTime.now())
                .responsibility(responsibilitiesEntities.stream()
                    .filter(r -> r.getName().equals("Coordinador")).findFirst()
                    .orElse(null))
                .build());

            userRepository.saveAndFlush(User.builder()
                .email("noeula@adminv")
                .password(passwordEncoder.encode("admin"))
                .group(_8g)
                .role(ERole.ROLE_ADMIN)
                .acceptedEULA(LocalDateTime.now())
                .acceptedEULAManager(LocalDateTime.now())
                .volunteer(volunteerRepository.getRandomVolunteer())
                .responsibility(responsibilitiesEntities.stream()
                    .filter(r -> r.getName().equals("Coordinador")).findFirst()
                    .orElse(null))
                .build());

            userRepository.saveAndFlush(User.builder()
                .email("manager@manager")
                .password(passwordEncoder.encode("manager"))
                .group(_8g)
                .role(ERole.ROLE_MANAGER)
                .responsibility(responsibilitiesEntities.stream()
                    .filter(r -> r.getName().equals("Auxiliar de coordinador")).findFirst()
                    .orElse(null))
                .build());

            userRepository.saveAndFlush(User.builder()
                .email("noeula@manager")
                .password(passwordEncoder.encode("manager"))
                .group(_8g)
                .role(ERole.ROLE_MANAGER)
                .acceptedEULA(LocalDateTime.now())
                .acceptedEULAManager(LocalDateTime.now())
                .responsibility(responsibilitiesEntities.stream()
                    .filter(r -> r.getName().equals("Coordinador")).findFirst()
                    .orElse(null))
                .build());

            userRepository.saveAndFlush(User.builder()
                .email("noeula@managerv")
                .password(passwordEncoder.encode("manager"))
                .group(_8g)
                .role(ERole.ROLE_MANAGER)
                .acceptedEULA(LocalDateTime.now())
                .acceptedEULAManager(LocalDateTime.now())
                .volunteer(volunteerRepository.getRandomVolunteer())
                .responsibility(responsibilitiesEntities.stream()
                    .filter(r -> r.getName().equals("Coordinador")).findFirst()
                    .orElse(null))
                .build());

            userRepository.saveAndFlush(User.builder()
                .email("user@user")
                .password(passwordEncoder.encode("user"))
                .group(_8g)
                .role(ERole.ROLE_USER)
                .responsibility(responsibilitiesEntities.stream()
                    .filter(r -> r.getName().equals("Voluntario")).findFirst()
                    .orElse(null))
                .build());

            userRepository.saveAndFlush(User.builder()
                .email("noeula@user")
                .password(passwordEncoder.encode("user"))
                .group(_8g)
                .role(ERole.ROLE_USER)
                .acceptedEULA(LocalDateTime.now())
                .responsibility(responsibilitiesEntities.stream()
                    .filter(r -> r.getName().equals("Coordinador")).findFirst()
                    .orElse(null))
                .build());

            userRepository.saveAndFlush(User.builder()
                .email("noeula@userv")
                .password(passwordEncoder.encode("user"))
                .group(_8g)
                .role(ERole.ROLE_USER)
                .acceptedEULA(LocalDateTime.now())
                .volunteer(volunteerRepository.getRandomVolunteer())
                .responsibility(responsibilitiesEntities.stream()
                    .filter(r -> r.getName().equals("Voluntario")).findFirst()
                    .orElse(null))
                .build());

            List<List<String>> users = Files.getContentFromCSV(usersCSV, ',', true);
            users.forEach(userFields -> {
                User user = User.builder()
                    .email(userFields.get(4))
                    .password(passwordEncoder.encode(userFields.get(3)))
                    .role(Integer.parseInt(userFields.get(6)) == 3 ? ERole.ROLE_ADMIN :
                        Integer.parseInt(userFields.get(6)) == 2 ? ERole.ROLE_MANAGER :
                            ERole.ROLE_USER)
                    .responsibility(responsibilitiesEntities.stream()
                        .filter(r -> r.getName().equals("Voluntario")).findFirst()
                        .orElse(null))
                    .group(_8g)
                    .acceptedEULA(LocalDateTime.now())
                    .acceptedEULAManager(Integer.parseInt(userFields.get(6)) > 1 ? LocalDateTime.now() : null)
                    .build();
                Volunteer volunteer = volunteerRepository.getRandomVolunteer();
                user.setVolunteer(volunteer);

                userRepository.saveAndFlush(user);
            });

        };

    }

    private static Set<EWeekday> getRandomAvailability() {
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

    private static List<Absence> getRandomAbsences(Volunteer volunteer) {
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

    private static <E extends Enum<E>> E getRandomEnum(Class<E> enumType) {
        return enumType.getEnumConstants()[getRandomIntegerFromRange(0, enumType.getEnumConstants().length)];
    }

    private static Integer getRandomIntegerFromRange(int min, int max) {
        return new Random().ints(1, min, max).iterator().nextInt();
    }

    private static Float getRandomFloatFromRange(float min, float max) {
        return min + new Random().nextFloat() * (max - min);
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

}
