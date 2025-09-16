package co.id.udaring.service;

import co.id.udaring.config.TokenConfiguration;
import co.id.udaring.dto.AuthTokenDTO;
import co.id.udaring.entity.RefreshTokenInfo;
import co.id.udaring.entity.Tenant;
import co.id.udaring.entity.entitytype.EntityType;
import co.id.udaring.exception.AuthenticationException;
import co.id.udaring.repository.RefreshTokenInfoRepository;
import co.id.udaring.repository.TenantRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static co.id.udaring.util.Constant.*;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final TenantRepository tenantRepository;
    private final RefreshTokenInfoRepository refreshTokenInfoRepository;
    private final TokenConfiguration tokenConfiguration;

    public AuthTokenDTO loginTenant(String tenantId, String password) {
        final var tenant = tenantRepository.findByTenantId(tenantId)
                .orElseThrow(AuthenticationException::new);
        tenant.validatePassword(password);

        // generate access token
        final var accessTokenConfig = tokenConfiguration.getAccessToken();
        final var accessTokenDuration = LocalDateTime.now().plus(accessTokenConfig.getDuration());
        final var accessToken = generateAccessToken(tenant, accessTokenDuration, accessTokenConfig);

        // generate refresh token
        final var refreshToken = RefreshTokenInfo.from(tenant, tokenConfiguration.getRefreshToken());
        refreshTokenInfoRepository.save(refreshToken);

        return new AuthTokenDTO(accessToken, refreshToken.getToken(), accessTokenDuration);
    }

    public AuthTokenDTO refreshToken(String bearerToken) {
        final var requestedRefreshToken = bearerToken.replace("Bearer ", "");
        final var refreshToken = findRefreshTokenInfo(requestedRefreshToken);

        if (LocalDateTime.now().isAfter(refreshToken.getExpiresAt())) {
            throw new AuthenticationException("Refresh token has expired");
        }

        if (refreshToken.getEntityType().equals(EntityType.TENANT)) {
            final var tenant = tenantRepository.findById(refreshToken.getId())
                    .orElseThrow(() -> new AuthenticationException("Tenant not found"));

            // generate access token
            final var accessTokenConfig = tokenConfiguration.getAccessToken();
            final var accessTokenDuration = LocalDateTime.now().plus(accessTokenConfig.getDuration());
            final var accessToken = generateAccessToken(tenant, accessTokenDuration, accessTokenConfig);

            // rotate refresh token without changing the expiration date
            refreshToken.rotateToken(tokenConfiguration.getRefreshToken());
            refreshTokenInfoRepository.save(refreshToken);

            return new AuthTokenDTO(accessToken, refreshToken.getToken(), accessTokenDuration);
        }

        throw new UnsupportedOperationException("Unsupported entity type");
    }

    public void testValidateToken(String bearerToken) {
        final var requestedRefreshToken = bearerToken.replace("Bearer ", "");
        try {
            final var jwt = JWT.decode(requestedRefreshToken);
            final var now = LocalDateTime.now();
            final var expirationDateTime = LocalDateTime.ofInstant(jwt.getExpiresAtAsInstant(), ZoneId.systemDefault());
            System.out.println(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
            System.out.println(expirationDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
            if (now.isAfter(expirationDateTime)) {
                throw new AuthenticationException("Access token has expired");
            }
        } catch (IllegalArgumentException | JWTDecodeException e) {
            throw new AuthenticationException("Invalid refresh token");
        }
    }

    private RefreshTokenInfo findRefreshTokenInfo(String requestedRefreshToken) {
        final UUID id;
        try {
            final var jwt = JWT.decode(requestedRefreshToken);
            final var refreshTokenInfoId = jwt.getSubject();
            id = UUID.fromString(refreshTokenInfoId);
        } catch (IllegalArgumentException | JWTDecodeException e) {
            throw new AuthenticationException("Invalid refresh token");
        }
        return refreshTokenInfoRepository.findById(id).orElseThrow(AuthenticationException::new);
    }

    private static String generateAccessToken(Tenant tenant, LocalDateTime expiresAt, TokenConfiguration.AccessToken accessTokenConfig) {
        return JWT.create()
                .withIssuer(accessTokenConfig.getIssuer())
                .withSubject(tenant.getTenantId())
                .withExpiresAt(toInstant(expiresAt))
                .sign(Algorithm.HMAC256(accessTokenConfig.getSecret()));
    }
}
