package co.id.udaring.service;

import co.id.udaring.dto.TenantCreateRequest;
import co.id.udaring.entity.Tenant;
import co.id.udaring.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TenantServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantService tenantService;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterTenantSuccessfully() {
        // Arrange
        var request = new TenantCreateRequest(
                "Test Tenant",
                "test@tenant.com",
                "password123"
        );

        final var expectedCreatedDate = LocalDateTime.now();
        final var expectedId = UUID.randomUUID();
        final var expectedEncodedPassword = "encodedPassword";
        Mockito.when(passwordEncoder.encode(request.password())).thenReturn(expectedEncodedPassword);

        Mockito.when(tenantRepository.save(Mockito.any(Tenant.class)))
                .thenAnswer(invocation -> {
                    Tenant tenant = invocation.getArgument(0);
                    tenant.setId(expectedId);
                    tenant.setCreatedDate(expectedCreatedDate);
                    return tenant;
                });

        // Act
        tenantService.registerTenant(request);

        // Assert
        var tenantCaptor = ArgumentCaptor.forClass(Tenant.class);
        Mockito.verify(tenantRepository).save(tenantCaptor.capture());
        var actualTenant = tenantCaptor.getValue();

        assertThat(actualTenant.getTenantName()).isEqualTo(request.tenantName());
        assertThat(actualTenant.getEmail()).isEqualTo(request.email());
        assertThat(actualTenant.getId()).isEqualTo(expectedId);
        assertThat(actualTenant.getCreatedDate()).isEqualTo(expectedCreatedDate);
        assertThat(actualTenant.getPassword()).isEqualTo(expectedEncodedPassword.getBytes());
    }
}
