package org.ldcgc.backend.payload.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nimbusds.jwt.SignedJWT;
import lombok.Builder;
import lombok.Value;
import org.ldcgc.backend.util.common.ERole;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenDto {

    private Integer id;
    private String jwtID;
    private String jwk;
    private LocalDateTime expiresAt;
    private LocalDateTime issuedAt;
    private Integer userId;
    private ERole role;
    private SignedJWT signedJWT;
    private boolean isRecoveryToken;
    private boolean isRefreshToken;
    private Integer refreshTokenId;

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return issuedAt.isAfter(now) || expiresAt.isBefore(now);
    }
}
