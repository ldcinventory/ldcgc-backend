package org.ldcgc.backend.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.security.user.UserDetailsImpl;
import org.ldcgc.backend.security.user.UserDetailsServiceImpl;
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

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.TOKEN_NOT_VALID;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;
import static org.ldcgc.backend.validator.Endpoint.isTokenEndpoint;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;
    private final TokenRepository tokenRepository;

    @Value("${jwtExpirationMs}")
    private int jwtExpirationSeconds;

    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        final String jwtHeaderPayload = request.getHeader("x-header-payload-token");
        final String jwtSignature = request.getHeader("x-signature-token");

        final boolean authIsNotPresent = StringUtils.isBlank(jwtHeaderPayload) && StringUtils.isBlank(jwtSignature);

        if (authIsNotPresent) {
            response.setHeader("Expires", LocalDateTime.now().plusSeconds(jwtExpirationSeconds).toString());
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = String.format("Bearer %s.%s", jwtHeaderPayload, jwtSignature);

        SignedJWT decodedJWT = jwtUtils.getDecodedJwt(jwt);

        try {
            if(!jwtUtils.verifyJwt(decodedJWT, null))
                throw new RequestException(HttpStatus.BAD_REQUEST, getErrorMessage(TOKEN_NOT_VALID));

            String userEmail = jwtUtils.getEmailFromJwtToken(decodedJWT);

            // TODO delete this section in the future to force login
            if(StringUtils.isNotBlank(request.getHeader("useMockedUser")))
                UserDetailsServiceImpl.setMockedUser(request.getHeader("useMockedUser"));

            UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(userEmail);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
                tokenRepository.deleteExpiredTokens();

        } catch (ParseException | JOSEException | IllegalArgumentException | NullPointerException e) {
            throw new RequestException(HttpStatus.BAD_REQUEST, getErrorMessage(TOKEN_NOT_VALID));
        }

        request.setAttribute("Authorization", jwt);

        if (isTokenEndpoint(request.getMethod(), request.getRequestURI())) {
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
