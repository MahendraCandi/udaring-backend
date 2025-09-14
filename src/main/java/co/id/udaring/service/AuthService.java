package co.id.udaring.service;

import co.id.udaring.dto.AuthTokenDTO;
import co.id.udaring.entity.RefreshTokenInfo;
import co.id.udaring.entity.Tenant;
import co.id.udaring.exception.AuthenticationException;
import co.id.udaring.repository.RefreshTokenInfoRepository;
import co.id.udaring.repository.TenantRepository;
import co.id.udaring.util.Constant;
import co.id.udaring.entity.entitytype.EntityType;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final TenantRepository tenantRepository;
    private final RefreshTokenInfoRepository refreshTokenInfoRepository;

    public AuthTokenDTO loginTenant(String tenantId, String password) {
        final var tenant = tenantRepository.findByTenantId(tenantId)
                .orElseThrow(AuthenticationException::new);
        tenant.validatePassword(password);

        final var expiresAt = Constant.INSTANT_NOW.plus(1, ChronoUnit.HOURS);
        final var accessToken = generateAccessToken(tenant, expiresAt);
        final var refreshTokenInfo = RefreshTokenInfo.from(tenant);
        refreshTokenInfoRepository.save(refreshTokenInfo);

        return new AuthTokenDTO(accessToken, refreshTokenInfo.getToken(), LocalDateTime.ofInstant(expiresAt, ZoneId.systemDefault()));
    }

    public AuthTokenDTO refreshToken(String bearerToken) {
        var refreshToken = bearerToken.replace("Bearer ", "");

        final var jwt = JWT.decode(refreshToken);
        final var refreshTokenInfoId = jwt.getSubject();
        final var refreshTokenInfo = refreshTokenInfoRepository.findById(UUID.fromString(refreshTokenInfoId))
                .orElseThrow(AuthenticationException::new);

        if (LocalDateTime.now().isAfter(refreshTokenInfo.getExpiresAt())) {
            throw new AuthenticationException("Refresh token has expired");
        }

        if (refreshTokenInfo.getEntityType().equals(EntityType.TENANT)) {
            final var tenant = tenantRepository.findById(refreshTokenInfo.getId())
                    .orElseThrow(() -> new AuthenticationException("Tenant not found"));

            // new access token
            final var expiresAt = Constant.INSTANT_NOW.plus(1, ChronoUnit.HOURS);
            final var accessToken = generateAccessToken(tenant, expiresAt);

            // rotate refresh token with same expires time
            refreshTokenInfo.rotateToken();
            refreshTokenInfoRepository.save(refreshTokenInfo);

            return new AuthTokenDTO(
                    accessToken,
                    refreshTokenInfo.getToken(),
                    LocalDateTime.ofInstant(expiresAt, ZoneId.systemDefault()));
        }

        throw new UnsupportedOperationException("Unsupported entity type");
    }

    private static String generateAccessToken(Tenant tenant, Instant expiresAt) {
        return JWT.create()
                .withIssuer("udaring.co.id") // todo change using properties
                .withSubject(tenant.getTenantId())
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256("secret")); // todo change secret using properties
    }
}
