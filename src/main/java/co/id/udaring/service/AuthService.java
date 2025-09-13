package co.id.udaring.service;

import co.id.udaring.dto.AuthTokenDTO;
import co.id.udaring.entity.LoginInfo;
import co.id.udaring.exception.AuthenticationException;
import co.id.udaring.repository.LoginInfoRepository;
import co.id.udaring.repository.TenantRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final TenantRepository tenantRepository;
    private final LoginInfoRepository loginInfoRepository;

    public AuthTokenDTO loginTenant(String tenantId, String password) {
        final var tenant = tenantRepository.findByTenantId(tenantId)
                .orElseThrow(AuthenticationException::new);

        tenant.validatePassword(password);

        final var expiresAt = Instant.now().plus(1, ChronoUnit.HOURS);
        final String accessToken = JWT.create()
                .withIssuer("udaring.co.id") // todo change using properties
                .withSubject(tenant.getTenantId())
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256("secret")); // todo change secret using properties

        final var loginInfo = LoginInfo.generateRefreshToken(tenant.getTableId());
        loginInfoRepository.save(loginInfo);

        return new AuthTokenDTO(accessToken, loginInfo.getRefreshToken(), LocalDateTime.ofInstant(expiresAt, ZoneId.systemDefault()));
    }
}
