package co.id.udaring.entity;

import co.id.udaring.config.TokenConfiguration;
import co.id.udaring.entity.entitytype.EntityType;
import co.id.udaring.util.Constant;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
public class RefreshTokenInfo {

    @Id
    @NotNull
    private UUID id; // either tenant table key or user id
    @NotNull
    private byte[] refreshToken;
    @NotNull
    private LocalDateTime expiresAt;
    @NotNull
    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    protected RefreshTokenInfo() {}

    protected RefreshTokenInfo(UUID id, String refreshToken, LocalDateTime localDateTime, EntityType entityType) {
        this.id = id;
        this.refreshToken = refreshToken.getBytes();
        this.expiresAt = localDateTime;
        this.entityType = entityType;
    }

    public static RefreshTokenInfo from(Tenant tenant, TokenConfiguration.RefreshToken refreshTokenConfig) {
        return from(tenant.getTableId(), tenant.getEntityType(), refreshTokenConfig.getDuration(), refreshTokenConfig.getSecret());
    }

    private static RefreshTokenInfo from(UUID id, EntityType entityType, Duration expiresDuration, String refreshTokenSecret) {
        final var refreshTokenDuration = LocalDateTime.now().plus(expiresDuration);
        final var refreshToken = createRefreshToken(id, refreshTokenDuration, refreshTokenSecret);
        return new RefreshTokenInfo(id, refreshToken, refreshTokenDuration, entityType);
    }

    public void rotateToken(TokenConfiguration.RefreshToken refreshTokenConfig) {
        final var refreshTokenDuration = LocalDateTime.now().plus(refreshTokenConfig.getDuration());
        final var token = createRefreshToken(this.id, refreshTokenDuration, refreshTokenConfig.getSecret());
        this.refreshToken = token.getBytes();
    }

    private static String createRefreshToken(UUID id, LocalDateTime expiresAt, String refreshTokenSecret) {
        return JWT.create()
                .withSubject(id.toString())
                .withExpiresAt(Constant.toInstant(expiresAt))
                .sign(Algorithm.HMAC256(refreshTokenSecret));
    }

    public String getToken() {
        return new String(refreshToken);
    }
}
