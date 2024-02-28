package org.ldcgc.backend.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.security.user.UserDetailsImpl;
import org.ldcgc.backend.security.user.UserDetailsServiceImpl;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.ldcgc.backend.util.common.ERole.ROLE_ADMIN;
import static org.ldcgc.backend.util.common.ERole.ROLE_MANAGER;
import static org.ldcgc.backend.validator.Endpoint.isNotReplaceTokenEndpoint;
import static org.ldcgc.backend.validator.Endpoint.isTokenEndpoint;
import static org.ldcgc.backend.validator.Endpoint.nonTokenEndpoint;
import static org.ldcgc.backend.validator.Endpoint.notExemptedEndpoint;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;
    private final TokenRepository tokenRepository;

    @Value("${jwtExpirationMs}")
    private int jwtExpirationSeconds;

    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        final String refreshJwt = request.getHeader("x-refresh-token");
        final String jwtHeaderPayload = request.getHeader("x-header-payload-token");
        final String jwtSignature = request.getHeader("x-signature-token");

        String jwt = null;
        if(Objects.nonNull(refreshJwt) && request.getMethod().equals("POST") && request.getRequestURI().equals("/api/accounts/refresh-token"))
            jwt = String.format("Bearer %s", refreshJwt);
        else if(ObjectUtils.allNotNull(jwtHeaderPayload, jwtSignature))
            jwt = String.format("Bearer %s.%s", jwtHeaderPayload, jwtSignature);

        final boolean authIsNotPresent = Objects.isNull(jwt);

        if (authIsNotPresent || nonTokenEndpoint(request.getMethod(), request.getRequestURI())) {
            response.setHeader("Expires", LocalDateTime.now().plusSeconds(jwtExpirationSeconds).toString());
            filterChain.doFilter(request, response);
            return;
        }

        if(StringUtils.isBlank(jwt))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOKEN_NOT_FOUND_HEADERS);

        SignedJWT decodedJWT = jwtUtils.getDecodedJwt(jwt);

        try {
            if(!jwtUtils.verifyJwt(decodedJWT, null))
                throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOKEN_NOT_VALID);

            String userEmail = jwtUtils.getEmailFromJwtToken(decodedJWT);

            UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(userEmail);

            // skip eula if header or exempted endpoint (i.e.: get/accept eula)
            if(notExemptedEndpoint(request.getMethod(), request.getRequestURI())) {

                // get eula details (standard user)
                if (userDetails.getAcceptedEULA() == null)
                    throw new RequestException(HttpStatus.FORBIDDEN, Messages.Error.EULA_STANDARD_NOT_ACCEPTED);

                // get eula details (manager)
                if((userDetails.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_MANAGER.name())) ||
                    userDetails.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_ADMIN.name())))
                    && userDetails.getAcceptedEULAManager() == null)
                    throw new RequestException(HttpStatus.FORBIDDEN, Messages.Error.EULA_MANAGER_NOT_ACCEPTED);
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ParseException | JOSEException | IllegalArgumentException | NullPointerException e) {
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOKEN_NOT_VALID);
        }

        request.setAttribute("Authorization", jwt);

        if (isTokenEndpoint(request.getMethod(), request.getRequestURI()) &&
            isNotReplaceTokenEndpoint(request.getMethod(), request.getRequestURI())) {
            response.setHeader("x-refresh-token", refreshJwt);
            response.setHeader("x-header-payload-token", jwtHeaderPayload);
            response.setHeader("x-signature-token", jwtSignature);
            try {
                response.setHeader("Expires", decodedJWT.getJWTClaimsSet().getExpirationTime().toString());
            } catch (ParseException e) {
                response.setHeader("Expires", LocalDateTime.now().plusSeconds(jwtExpirationSeconds).toString());
            }
        }

        filterChain.doFilter(request, response);

    }

}
