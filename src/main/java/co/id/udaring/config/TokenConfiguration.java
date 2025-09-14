package co.id.udaring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.token.config")
public class TokenConfiguration {
    private AccessToken accessToken;
    private RefreshToken refreshToken;

    @Getter
    @Setter
    public static class AccessToken{
        private Duration duration;
        private String secret;
        private String issuer;
    }

    @Getter
    @Setter
    public static class RefreshToken{
        private Duration duration;
        private String secret;
    }
}
