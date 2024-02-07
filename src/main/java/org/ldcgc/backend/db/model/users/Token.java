package org.ldcgc.backend.db.model.users;

import com.nimbusds.jwt.SignedJWT;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ldcgc.backend.util.common.ERole;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String jwtID;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String jwk;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @NotNull
    @Column(nullable = false)
    private Integer userId;

    @NotNull
    @Column(nullable = false)
    private ERole role;

    @NotNull
    private boolean isRecoveryToken;

    @NotNull
    private boolean isRefreshToken;

    private Integer refreshTokenId;

    @Transient
    private SignedJWT signedJWT;

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return issuedAt.isAfter(now) || expiresAt.isBefore(now);
    }
}
