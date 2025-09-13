package co.id.udaring.service;

import co.id.udaring.dto.tenant.TenantCreateRequest;
import co.id.udaring.entity.Tenant;
import co.id.udaring.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TenantService {

    private final PasswordEncoder passwordEncoder;
    private final TenantRepository tenantRepository;

    public Tenant registerTenant(TenantCreateRequest request) {
        final var tenant = new Tenant();
        tenant.setTenantName(request.tenantName());
        tenant.setEmail(request.email());
        tenant.setPassword(passwordEncoder, request.password());
        return tenantRepository.save(tenant);
    }
}
