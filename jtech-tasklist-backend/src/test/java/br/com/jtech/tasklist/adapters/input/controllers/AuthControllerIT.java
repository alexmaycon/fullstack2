package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIT extends AbstractIntegrationTest {

    @Test
    void shouldRegisterAndLoginSuccessfully() throws Exception {
        String email = uniqueEmail("auth");
        ObjectNode register = objectMapper.createObjectNode()
                .put("name", "Alex")
                .put("email", email)
                .put("password", "secret123");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email));

        ObjectNode login = objectMapper.createObjectNode()
                .put("email", email)
                .put("password", "secret123");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void shouldReturn409WhenEmailDuplicated() throws Exception {
        String email = uniqueEmail("dup");
        ObjectNode register = objectMapper.createObjectNode()
                .put("name", "Alex")
                .put("email", email)
                .put("password", "secret123");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn401OnInvalidCredentials() throws Exception {
        ObjectNode login = objectMapper.createObjectNode()
                .put("email", "nobody@test.com")
                .put("password", "wrongpass");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400OnValidationError() throws Exception {
        ObjectNode invalid = objectMapper.createObjectNode()
                .put("name", "")
                .put("email", "not-an-email")
                .put("password", "x");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.subErrors").isArray());
    }

    @Test
    void shouldReturn401OnInvalidRefreshToken() throws Exception {
        ObjectNode body = objectMapper.createObjectNode().put("refreshToken", "garbage.token.value");
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }
}
