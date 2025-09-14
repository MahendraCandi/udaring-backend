package co.id.udaring.controller;

import co.id.udaring.dto.AuthTokenDTO;
import co.id.udaring.dto.tenant.TenantLoginRequest;
import co.id.udaring.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/refresh-token")
    public AuthTokenDTO refreshToken(
            @RequestHeader(name = "Authorization") String bearerToken) {
        return authService.refreshToken(bearerToken);
    }
}
