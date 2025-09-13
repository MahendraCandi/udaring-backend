package co.id.udaring.dto.tenant;

import co.id.udaring.entity.Tenant;

public record TenantResponseDTO(
        String tenantId
) {
    public static TenantResponseDTO from(Tenant tenant) {
        return new TenantResponseDTO(tenant.getTenantId());
    }
}
