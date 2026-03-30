package com.vault.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GeneratorIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGeneratePasswordWithDefaults() throws Exception {
        mockMvc.perform(get("/api/v1/generator"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generatedPassword").isNotEmpty())
                .andExpect(jsonPath("$.generatedPassword").isString());
    }

    @Test
    void shouldGeneratePasswordWithCustomLength() throws Exception {
        mockMvc.perform(get("/api/v1/generator")
                        .param("length", "32"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generatedPassword").isNotEmpty());
    }

    @Test
    void shouldGeneratePasswordWithoutSymbols() throws Exception {
        mockMvc.perform(get("/api/v1/generator")
                        .param("symbols", "false")
                        .param("length", "64"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generatedPassword").isNotEmpty());
    }

    @Test
    void shouldGeneratePasswordExcludingAmbiguous() throws Exception {
        mockMvc.perform(get("/api/v1/generator")
                        .param("ambiguous", "true")
                        .param("length", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generatedPassword").isNotEmpty());
    }

    @Test
    void shouldRejectInvalidLength() throws Exception {
        mockMvc.perform(get("/api/v1/generator")
                        .param("length", "7"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectLengthAboveMaximum() throws Exception {
        mockMvc.perform(get("/api/v1/generator")
                        .param("length", "129"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAcceptMinimumLength() throws Exception {
        mockMvc.perform(get("/api/v1/generator")
                        .param("length", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generatedPassword").isNotEmpty());
    }

    @Test
    void shouldAcceptMaximumLength() throws Exception {
        mockMvc.perform(get("/api/v1/generator")
                        .param("length", "128"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generatedPassword").isNotEmpty());
    }

    @Test
    void shouldNotRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/generator"))
                .andExpect(status().isOk());
    }
}
