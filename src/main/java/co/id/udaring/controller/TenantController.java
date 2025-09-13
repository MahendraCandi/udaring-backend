package co.id.udaring.controller;

import co.id.udaring.dto.tenant.TenantCreateRequest;
import co.id.udaring.dto.tenant.TenantLoginRequest;
import co.id.udaring.dto.tenant.TenantResponseDTO;
import co.id.udaring.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/tenant")
@RestController
public class TenantController {

    private final TenantService tenantService;

    @PostMapping("/register")
    public TenantResponseDTO registerTenant(
            @RequestBody @Valid TenantCreateRequest request
    ) {
        final var tenant = tenantService.registerTenant(request);
        return TenantResponseDTO.from(tenant);
    }
}
