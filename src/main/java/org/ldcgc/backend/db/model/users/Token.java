package org.ldcgc.backend.db.model.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String jwtID;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String jwk;

    @NotNull
    private LocalDateTime expiresAt;

    @NotNull
    private LocalDateTime issuedAt;

    @NotNull
    private Integer userId;

    @NotNull
    private ERole role;

    @NotNull
    private boolean isRecoveryToken;

    // TODO
    private Integer refreshTokenId;

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return issuedAt.isAfter(now) || expiresAt.isBefore(now);
    }
}
