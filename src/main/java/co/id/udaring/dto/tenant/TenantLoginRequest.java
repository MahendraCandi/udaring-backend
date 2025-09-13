package co.id.udaring.dto.tenant;

import jakarta.validation.constraints.NotBlank;

public record TenantLoginRequest(
        @NotBlank
        String tenantId,
        @NotBlank
        String password
) {
}
