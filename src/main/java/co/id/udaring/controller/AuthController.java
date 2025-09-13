package co.id.udaring.controller;

import co.id.udaring.dto.AuthTokenDTO;
import co.id.udaring.dto.tenant.TenantLoginRequest;
import co.id.udaring.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/tenant")
    public AuthTokenDTO loginTenant(
            @RequestBody @Valid TenantLoginRequest request) {
        return authService.loginTenant(request.tenantId(), request.password());
    }
}
