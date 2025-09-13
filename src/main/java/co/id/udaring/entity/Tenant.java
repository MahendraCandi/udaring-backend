package co.id.udaring.entity;

import co.id.udaring.exception.AuthenticationException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID tableId; // the table key
    @NotEmpty
    @Column(unique = true, nullable = false, length = 30)
    private String tenantId; // the business key
    @NotEmpty
    @Column(nullable = false, length = 150)
    private String tenantName;
    @Email
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    @NotNull
    @Column(nullable = false)
    private byte[] password;
    @NotNull
    private LocalDateTime createdDate;

    @PrePersist
    private void prePersist() {
        if (tenantId == null) this.tenantId = TenantUtil.randomKey(12);
        if (createdDate == null) this.createdDate = LocalDateTime.now();
    }

    public void setPassword(String rawPassword) {
        this.password = TenantUtil.encodingPassword(rawPassword);
    }

    public void validatePassword(String rawPassword) {
        if (!TenantUtil.matchingPassword(rawPassword, this.password)) {
            throw new AuthenticationException();
        }
    }

    private static class TenantUtil {
        private static final String ALPHANUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        private static final SecureRandom RANDOM = new SecureRandom();
        private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

        private TenantUtil() {}

        public static String randomKey(int length) {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
            }
            return sb.toString();
        }

        public static byte[] encodingPassword(String rawPassword) {
            return PASSWORD_ENCODER.encode(rawPassword).getBytes();
        }

        public static boolean matchingPassword(String rawPassword, byte[] encodedPassword) {
            return PASSWORD_ENCODER.matches(rawPassword, new String(encodedPassword));
        }
    }

}
