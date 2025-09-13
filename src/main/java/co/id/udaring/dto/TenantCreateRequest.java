package co.id.udaring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TenantCreateRequest(
        @NotBlank
        String tenantName,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password
) {
}
