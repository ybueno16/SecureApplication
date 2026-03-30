package com.vault.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.application.dto.CreateCredentialRequest;
import com.vault.application.dto.LoginRequest;
import com.vault.application.dto.RegisterUserRequest;
import com.vault.application.dto.UpdateCredentialRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CredentialCrudIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String EMAIL = "credcrud@example.com";
    private static final String PASSWORD = "my-master-password-123";
    private String accessToken;

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
    }

    @Nested
    class CreateCredential {

        @Test
        void shouldCreateCredential() throws Exception {
            var request = new CreateCredentialRequest("https://github.com", "myuser", "mypass", "notes", List.of("dev"));

            mockMvc.perform(post("/api/v1/credentials")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken)
                            .header("X-Master-Password", PASSWORD)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.siteUrl").value("https://github.com"))
                    .andExpect(jsonPath("$.tags[0]").value("dev"));
        }

        @Test
        void shouldCreateCredentialWithoutNotes() throws Exception {
            var request = new CreateCredentialRequest("https://example.com", "user", "pass", null, List.of());

            mockMvc.perform(post("/api/v1/credentials")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken)
                            .header("X-Master-Password", PASSWORD)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty());
        }

        @Test
        void shouldRejectCreateWithoutAuthentication() throws Exception {
            var request = new CreateCredentialRequest("https://github.com", "u", "p", null, List.of());

            mockMvc.perform(post("/api/v1/credentials")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Master-Password", PASSWORD)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldRejectCreateWithMissingSiteUrl() throws Exception {
            var body = "{\"username\":\"u\",\"password\":\"p\"}";

            mockMvc.perform(post("/api/v1/credentials")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken)
                            .header("X-Master-Password", PASSWORD)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class ListCredentials {

        @Test
        void shouldListCredentials() throws Exception {
            createTestCredential("https://site1.com");
            createTestCredential("https://site2.com");

            mockMvc.perform(get("/api/v1/credentials")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.hasMore").isBoolean());
        }

        @Test
        void shouldReturnEmptyListForNewUser() throws Exception {
            mockMvc.perform(get("/api/v1/credentials")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.hasMore").value(false));
        }

        @Test
        void shouldRejectListWithoutAuthentication() throws Exception {
            mockMvc.perform(get("/api/v1/credentials"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldSupportPaginationLimit() throws Exception {
            createTestCredential("https://a.com");
            createTestCredential("https://b.com");
            createTestCredential("https://c.com");

            mockMvc.perform(get("/api/v1/credentials")
                            .header("Authorization", "Bearer " + accessToken)
                            .param("limit", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray());
        }
    }

    @Nested
    class UpdateCredential {

        @Test
        void shouldUpdateCredential() throws Exception {
            var credentialId = createTestCredential("https://old-site.com");
            var update = new UpdateCredentialRequest("https://new-site.com", "newuser", "newpass", "newnotes", List.of("updated"));

            mockMvc.perform(put("/api/v1/credentials/" + credentialId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken)
                            .header("X-Master-Password", PASSWORD)
                            .content(objectMapper.writeValueAsString(update)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.siteUrl").value("https://new-site.com"));
        }

        @Test
        void shouldRejectUpdateOfNonexistentCredential() throws Exception {
            var update = new UpdateCredentialRequest("https://new.com", "u", "p", null, List.of());

            mockMvc.perform(put("/api/v1/credentials/00000000-0000-0000-0000-000000000000")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken)
                            .header("X-Master-Password", PASSWORD)
                            .content(objectMapper.writeValueAsString(update)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldRejectUpdateWithoutAuthentication() throws Exception {
            var update = new UpdateCredentialRequest("https://new.com", "u", "p", null, List.of());

            mockMvc.perform(put("/api/v1/credentials/some-id")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Master-Password", PASSWORD)
                            .content(objectMapper.writeValueAsString(update)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class DeleteCredential {

        @Test
        void shouldDeleteCredential() throws Exception {
            var credentialId = createTestCredential("https://delete-me.com");

            mockMvc.perform(delete("/api/v1/credentials/" + credentialId)
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldRejectDeleteOfNonexistentCredential() throws Exception {
            mockMvc.perform(delete("/api/v1/credentials/00000000-0000-0000-0000-000000000000")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldRejectDeleteWithoutAuthentication() throws Exception {
            mockMvc.perform(delete("/api/v1/credentials/some-id"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldNotListDeletedCredential() throws Exception {
            var credentialId = createTestCredential("https://will-be-deleted.com");

            mockMvc.perform(delete("/api/v1/credentials/" + credentialId)
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/v1/credentials/" + credentialId + "/reveal")
                            .header("Authorization", "Bearer " + accessToken)
                            .header("X-Master-Password", PASSWORD))
                    .andExpect(status().isNotFound());
        }
    }

    private String createTestCredential(String siteUrl) throws Exception {
        var request = new CreateCredentialRequest(siteUrl, "user", "pass", null, List.of());
        var result = mockMvc.perform(post("/api/v1/credentials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .header("X-Master-Password", PASSWORD)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        var json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asText();
    }
}
