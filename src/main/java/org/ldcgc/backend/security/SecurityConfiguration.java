package org.ldcgc.backend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.security.jwt.AuthEntryPointJwt;
import org.ldcgc.backend.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final AuthTokenFilter authTokenFilter;
    private final AuthEntryPointJwt authEntryPointJwt;
    private final PasswordEncoder passwordEncoder;

    @Value("${CORS_ORIGIN:*}")
    private String corsOrigins;

    @Value("${REQUEST_MATCHERS:/**}")
    private String allowedRequestMatchers;

    private final String[] allowedMethods = { "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS" };

    private final String[] allowedHeaders = { "Authorization", "Accept", "Accept-Language", "content-type",
            "Requestor-Type", "X-Requested-With", "x-header-payload-token", "x-signature-token" };

    private final String[] exposedHeaders = { "Allow", "X-Get-Header", "x-header-payload-token", "x-signature-token" };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .headers(headersConfig -> headersConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(allowedRequestMatchers).permitAll()
                .requestMatchers("/alive", "/error").permitAll()
                .requestMatchers("/swagger-ui", "/swagger-ui/**", "/webjars/**", "/swagger-ui.html", "/swagger/api-docs/**", "/swagger/api-docs", "/swagger/api-docs.yaml").permitAll()
                .requestMatchers("/accounts/login", "/accounts/recover", "/accounts/new-pass", "/accounts/confirm-account").permitAll()
                .anyRequest().authenticated())
            .securityMatcher("/**")
            .userDetailsService(userDetailsService)
            .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(Customizer.withDefaults())
            .exceptionHandling(config -> config.authenticationEntryPoint(authEntryPointJwt))
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(corsOrigins.split(",")));

        log.info(String.format("Allowed CORS: %s", Arrays.toString(corsOrigins.split(","))));

        config.setAllowedHeaders(List.of(allowedHeaders));
        config.setAllowedMethods(List.of(allowedMethods));
        config.setExposedHeaders(List.of(exposedHeaders));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
