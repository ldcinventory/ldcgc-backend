package org.ldcgc.backend.app.configuration;

import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.ldcgc.backend.app.security.jwt.JwtUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiHandlerInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws ParseException {

        String headerPayload = request.getHeader("x-header-payload-token");
        String signature = request.getHeader("x-signature-token");

        if(ObjectUtils.allNotNull(headerPayload, signature)) {
            SignedJWT signedJWT = jwtUtils.getDecodedJwt(String.format("%s.%s", headerPayload, signature));
            String groupId = ((LinkedTreeMap<?,?>) signedJWT.getJWTClaimsSet().getClaim("userClaims")).get("groupId").toString();
            MDC.put("groupId", groupId);
        }

        return true;
    }

    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) {
        MDC.clear();
    }

}
