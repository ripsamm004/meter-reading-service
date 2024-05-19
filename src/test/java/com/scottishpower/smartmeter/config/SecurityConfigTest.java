package com.scottishpower.smartmeter.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.scottishpower.smartmeter.enums.Error;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenUnauthenticatedAccessUnconfiguredPath_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/public"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.code").value(Error.UNAUTHORIZED_EXCEPTION.getCode()))
            .andExpect(jsonPath("$.message").value(Error.UNAUTHORIZED_EXCEPTION.getMessage()));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void whenAuthenticatedAccessUnconfiguredPath_thenForbidden() throws Exception {
        mockMvc.perform(get("/public"))
            .andExpect(status().isForbidden())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.code").value(Error.FORBIDDEN_EXCEPTION.getCode()))
            .andExpect(jsonPath("$.message").value(Error.FORBIDDEN_EXCEPTION.getMessage()));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void whenAuthenticatedAccessSmart_thenOk() throws Exception {
        mockMvc.perform(get("/api/smart/reads/ACC123"))
            .andExpect(status().isOk());
    }

    @Test
    void whenUnauthenticatedAccessSmart_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/smart/reads/ACC123"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.code").value(Error.UNAUTHORIZED_EXCEPTION.getCode()))
            .andExpect(jsonPath("$.message").value(Error.UNAUTHORIZED_EXCEPTION.getMessage()));
    }

    @Test
    void whenAccessActuatorHealth_thenOk() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk());
    }

    @Test
    void whenAccessActuatorInfo_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/actuator/info"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.code").value(Error.UNAUTHORIZED_EXCEPTION.getCode()))
            .andExpect(jsonPath("$.message").value(Error.UNAUTHORIZED_EXCEPTION.getMessage()));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    void whenAuthenticatedAccessActuatorEndpoints_thenOk() throws Exception {
        mockMvc.perform(get("/actuator"))
            .andExpect(status().isOk());
        mockMvc.perform(get("/actuator/metrics"))
            .andExpect(status().isOk());
    }
}
