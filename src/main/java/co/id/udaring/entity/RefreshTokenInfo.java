package co.id.udaring.entity;

import co.id.udaring.util.Constant;
import co.id.udaring.entity.entitytype.EntityType;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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

    public static RefreshTokenInfo from(Tenant tenant) {
        return from(tenant.getTableId(), tenant.getEntityType());
    }

    private static RefreshTokenInfo from(UUID id, EntityType entityType) {
        final var expiresAt = Constant.INSTANT_NOW.plus(3, ChronoUnit.DAYS);
        final var refreshToken = createToken(id, expiresAt);
        return new RefreshTokenInfo(id, refreshToken, LocalDateTime.ofInstant(expiresAt, ZoneId.systemDefault()), entityType);
    }

    private static String createToken(UUID id, Instant expiresAt) {
        return JWT.create()
                .withSubject(id.toString())
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256("secret")); // todo change me to properties
    }

    public void rotateToken() {
        final var token = createToken(this.id, Constant.toInstant(this.expiresAt));
        this.refreshToken = token.getBytes();
    }

    public String getToken() {
        return new String(refreshToken);
    }
}
