package org.ldcgc.backend.controller.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.annotation.TestConstrainValidationFactory;
import org.ldcgc.backend.configuration.ContextConstants;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.payload.dto.category.BrandDto;
import org.ldcgc.backend.payload.dto.other.NonPaged;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.security.user.UserDetailsServiceImpl;
import org.ldcgc.backend.service.resources.common.BrandService;
import org.ldcgc.backend.service.resources.consumable.ConsumableExcelService;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.validator.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.ldcgc.backend.base.Authentication.setAuthenticationForRequest;
import static org.ldcgc.backend.base.Constants.API_ROOT;
import static org.ldcgc.backend.base.factory.TestRequestFactory.deleteRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.getRequest;
import static org.ldcgc.backend.base.factory.TestRequestFactory.postRequest;
import static org.ldcgc.backend.base.mock.MockedResources.getRandomBrand;
import static org.ldcgc.backend.base.mock.MockedResources.getRandomIndexFromList;
import static org.ldcgc.backend.base.mock.MockedResources.listRandomResource;
import static org.ldcgc.backend.util.creation.Constructor.buildResponseMessage;
import static org.ldcgc.backend.util.creation.Constructor.buildResponseMessageObject;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = BrandController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BrandControllerImplTest {

    // controller
    @Autowired private BrandController brandController;

    // services
    @MockBean private BrandService brandService;
    @MockBean private UserDetailsServiceImpl userDetailsService;
    @MockBean private JwtUtils jwtUtils;
    @MockBean private ConsumableExcelService consumableExcelService;

    // repositories
    @MockBean private TokenRepository tokenRepository;
    @MockBean private UserRepository userRepository;
    @MockBean private ConsumableRepository consumableRepository;
    @MockBean private BrandRepository brandRepository;
    @MockBean private LocationRepository locationRepository;
    @MockBean private GroupRepository groupRepository;

    // other
    @MockBean private UserValidation userValidation;
    @MockBean private ContextConstants contextConstants;

    // context
    @Autowired private WebApplicationContext context;

    // mapper
    @Autowired private ObjectMapper mapper;

    private final String requestRoot = "/resources/brands";

    private MockMvc mockMvc;
    private final List<BrandDto> brands = listRandomResource(BrandDto.class, null);

    @BeforeEach
    public void init() throws ParseException {

        final GenericWebApplicationContext context = new GenericWebApplicationContext(new MockServletContext());
        final ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
        beanFactory.registerSingleton(UserValidation.class.getCanonicalName(), UserValidation.bean(userRepository, jwtUtils));
        context.refresh();

        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setApplicationContext(context);
        TestConstrainValidationFactory constrainValidationFactory = new TestConstrainValidationFactory(context);
        validatorFactoryBean.setConstraintValidatorFactory(constrainValidationFactory);
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.afterPropertiesSet();

        mockMvc = MockMvcBuilders
            .standaloneSetup(brandController)
            .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
            .setValidator(validatorFactoryBean)
            .setHandlerExceptionResolvers()
            .build();

        setAuthenticationForRequest(jwtUtils, userRepository, userValidation);

    }

    @Test
    void getBrands() throws Exception {
        final String request = requestRoot;

        log.info("Testing a GET Request to %s%s\n".formatted(API_ROOT, request));

        String message = String.format(Messages.Info.BRAND_FOUND, brands.size());
        int index = getRandomIndexFromList(brands);

        ResponseEntity<?> response = buildResponseMessageObject(HttpStatus.OK, message, NonPaged.of(brands));

        given(brandService.getBrands(null, null)).willAnswer(invocation -> response);

        mockMvc.perform(getRequest(request, ERole.ROLE_USER))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", Matchers.is(message)))
            .andExpect(jsonPath("$.data.elements", hasSize(brands.size())))
            .andExpect(jsonPath("$.data.elements[" + index +"].id", Matchers.is(brands.get(index).getId())))
            .andExpect(jsonPath("$.data.elements[" + index +"].name", Matchers.is(brands.get(index).getName())))
            .andExpect(jsonPath("$.data.elements[" + index +"].locked", Matchers.is(brands.get(index).getLocked())))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    void createBrand() throws Exception {
        final String request = requestRoot;

        log.info("Testing a POST Request to %s%s\n".formatted(API_ROOT, request));

        BrandDto brandDto = getRandomBrand();

        ResponseEntity<?> response = buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.BRAND_CREATED, brandDto);

        given(brandService.createBrand(any(BrandDto.class))).willAnswer(invocation -> response);

        mockMvc.perform(postRequest(request, ERole.ROLE_ADMIN)
                .content(mapper.writeValueAsString(brandDto)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message", Matchers.is(Messages.Info.BRAND_CREATED)))
            .andExpect(jsonPath("$.data.id", Matchers.is(brandDto.getId())))
            .andExpect(jsonPath("$.data.name", Matchers.is(brandDto.getName())))
            .andExpect(jsonPath("$.data.locked", Matchers.is(brandDto.getLocked())))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

    @Test
    void deleteBrand() throws Exception {
        final String request = requestRoot + "/{brandId}";

        log.info("Testing a DELETE Request to %s%s\n".formatted(API_ROOT, request));

        BrandDto brandDto = getRandomBrand();

        String message = String.format(Messages.Info.BRAND_DELETED, brandDto.getId());

        ResponseEntity<?> response = buildResponseMessage(HttpStatus.OK, message);

        given(brandService.deleteBrand(anyInt())).willAnswer(invocation -> response);

        mockMvc.perform(deleteRequest(request, ERole.ROLE_ADMIN, brandDto.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", Matchers.is(message)))
            .andExpect(content().encoding(StandardCharsets.UTF_8));

    }

}
