package co.id.udaring.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
public class LoginInfo {
    @Id
    private UUID id; // either tenant table key or user id
    private String refreshToken;
    private LocalDateTime expiresAt;

    protected LoginInfo() {}

    private LoginInfo(UUID id, String refreshToken, LocalDateTime expiresAt) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }

    public static LoginInfo generateRefreshToken(UUID id) {
        return new LoginInfo(id, UUID.randomUUID().toString(), LocalDateTime.now().plusDays(3));
    }
}
