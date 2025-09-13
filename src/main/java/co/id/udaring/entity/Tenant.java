package co.id.udaring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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
    private UUID id; // the table key
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
        if (tenantId == null) {
            this.tenantId = RandomKeyGenerator.randomKey(12);
        }
    }

    public void setPassword(PasswordEncoder passwordEncoder, String password) {
        this.password = passwordEncoder.encode(password).getBytes();
    }

    private static class RandomKeyGenerator {
        private static final String ALPHANUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        private static final SecureRandom RANDOM = new SecureRandom();

        private RandomKeyGenerator() {}

        public static String randomKey(int length) {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
            }
            return sb.toString();
        }
    }

}
