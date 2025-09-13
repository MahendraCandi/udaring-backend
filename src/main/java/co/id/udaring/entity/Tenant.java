package co.id.udaring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID tenantId;
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
}
