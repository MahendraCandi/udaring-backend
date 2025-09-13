package co.id.udaring.entity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.persistence.Entity;
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
public class LoginInfo {
    @Id
    @NotNull
    private UUID id; // either tenant table key or user id
    @NotNull
    private byte[] refreshToken;
    @NotNull
    private LocalDateTime expiresAt;

    protected LoginInfo() {}

    private LoginInfo(UUID id, String refreshToken, LocalDateTime expiresAt) {
        this.id = id;
        this.refreshToken = refreshToken.getBytes();
        this.expiresAt = expiresAt;
    }

    public static LoginInfo generateRefreshToken(UUID id) {
        final var expiresAt = Instant.now().plus(3, ChronoUnit.DAYS);
        final var refreshToken = JWT.create()
                .withSubject(id.toString())
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256("secret")); // todo change me to properties
        return new LoginInfo(id, refreshToken, LocalDateTime.ofInstant(expiresAt, ZoneId.systemDefault()));
    }

    public String getRefreshToken() {
        return new String(refreshToken);
    }
}
