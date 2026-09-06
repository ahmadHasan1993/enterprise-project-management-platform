package com.ahmadhassan.projectmanagement.identity;

import com.ahmadhassan.projectmanagement.PostgreSqlIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerIntegrationTest extends PostgreSqlIntegrationTestSupport {

    @Autowired
    MockMvc mockMvc;

    @Test
    void registerReturnsCreatedForValidRequest() throws Exception {
        String requestBody = """
                {
                  "email": "api-user@example.com",
                  "password": "this-is-a-secure-password"
                }
                """;

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    void registerReturnsConflictForDuplicateEmail() throws Exception {
        String firstRequestBody = """
                {
                  "email": "duplicate@example.com",
                  "password": "this-is-a-secure-password"
                }
                """;

        String duplicateRequestBody = """
                {
                  "email": "DUPLICATE@example.com",
                  "password": "another-secure-password"
                }
                """;

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(firstRequestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(duplicateRequestBody))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"code\":\"EMAIL_ALREADY_IN_USE\"")));
    }

    @Test
    void registerReturnsBadRequestForInvalidRequest() throws Exception {
        String requestBody = """
                {
                  "email": "not-an-email",
                  "password": "short"
                }
                """;

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"code\":\"VALIDATION_FAILED\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"email\":\"must be a well-formed email address\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"password\":\"size must be between 12 and 64\"")));
    }

    @Test
    void registerReturnsBadRequestForPasswordExceedingBCryptInputLimit() throws Exception {
        String password = "€".repeat(40);
        String requestBody = """
                {
                  "email": "bcrypt-api@example.com",
                  "password": "%s"
                }
                """.formatted(password);

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"code\":\"PASSWORD_INVALID\"")));
    }
}