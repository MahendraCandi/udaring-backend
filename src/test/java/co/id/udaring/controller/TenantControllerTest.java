package co.id.udaring.controller;

import co.id.udaring.dto.TenantCreateRequest;
import co.id.udaring.entity.Tenant;
import co.id.udaring.exception.GlobalExceptionHandler;
import co.id.udaring.service.TenantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TenantControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TenantService tenantService;

    @InjectMocks
    private TenantController tenantController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tenantController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registerTenant_shouldReturnTenantResponseDto_whenRequestIsValid() throws Exception {
        TenantCreateRequest validRequest = new TenantCreateRequest("Tenant Name", "tenant@email.com", "password123");

        final var tenant = Tenant.builder()
                .tenantId("tenant-id-123")
                .tenantName(validRequest.tenantName())
                .email(validRequest.email())
                .createdDate(LocalDateTime.now())
                .build();
        when(tenantService.registerTenant(any(TenantCreateRequest.class))).thenReturn(tenant);

        mockMvc.perform(post("/tenant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value(tenant.getTenantId()));
    }

    @Test
    void registerTenant_shouldReturnBadRequest_whenRequestHasInvalidEmail() throws Exception {
        TenantCreateRequest invalidRequest = new TenantCreateRequest("", "invalid-email", "");

        mockMvc.perform(post("/tenant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(Matchers.endsWithIgnoringCase("/error/invalid-parameters")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Invalid parameters"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Invalid request content."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.instance").value("/tenant/register"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[*]", Matchers.containsInAnyOrder(
                        Matchers.allOf(
                                Matchers.hasEntry("detail", "must be a well-formed email address"),
                                Matchers.hasEntry("field", "email"),
                                Matchers.hasEntry("pointer", "/email")
                        ),
                        Matchers.allOf(
                                Matchers.hasEntry("detail", "must not be blank"),
                                Matchers.hasEntry("field", "tenantName"),
                                Matchers.hasEntry("pointer", "/tenantName")
                        ),
                        Matchers.allOf(
                                Matchers.hasEntry("detail", "must not be blank"),
                                Matchers.hasEntry("field", "password"),
                                Matchers.hasEntry("pointer", "/password")
                        )
                )))
                ;
    }
}
