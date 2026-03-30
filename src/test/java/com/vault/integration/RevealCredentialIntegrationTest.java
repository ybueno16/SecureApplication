package com.vault.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.application.dto.CreateCredentialRequest;
import com.vault.application.dto.LoginRequest;
import com.vault.application.dto.RegisterUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RevealCredentialIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String EMAIL = "revealtest@example.com";
    private static final String PASSWORD = "my-master-password-123";
    private String accessToken;
    private String credentialId;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterUserRequest(EMAIL, PASSWORD))));

        var loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(EMAIL, PASSWORD))))
                .andReturn();

        var loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        accessToken = loginJson.get("access_token").asText();

        var createRequest = new CreateCredentialRequest(
                "https://example.com", "secret-user", "secret-pass", "secret notes", List.of("test"));
        var createResult = mockMvc.perform(post("/api/v1/credentials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .header("X-Master-Password", PASSWORD)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn();

        var createJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        credentialId = createJson.get("id").asText();
    }

    @Test
    void shouldRevealCredentialWithCorrectMasterPassword() throws Exception {
        mockMvc.perform(get("/api/v1/credentials/" + credentialId + "/reveal")
                        .header("Authorization", "Bearer " + accessToken)
                        .header("X-Master-Password", PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("secret-user"))
                .andExpect(jsonPath("$.password").value("secret-pass"))
                .andExpect(jsonPath("$.notes").value("secret notes"));
    }

    @Test
    void shouldRejectRevealWithWrongMasterPassword() throws Exception {
        mockMvc.perform(get("/api/v1/credentials/" + credentialId + "/reveal")
                        .header("Authorization", "Bearer " + accessToken)
                        .header("X-Master-Password", "wrong-password-123"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void shouldRejectRevealWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/credentials/" + credentialId + "/reveal")
                        .header("X-Master-Password", PASSWORD))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectRevealOfNonexistentCredential() throws Exception {
        mockMvc.perform(get("/api/v1/credentials/00000000-0000-0000-0000-000000000000/reveal")
                        .header("Authorization", "Bearer " + accessToken)
                        .header("X-Master-Password", PASSWORD))
                .andExpect(status().isNotFound());
    }
}
