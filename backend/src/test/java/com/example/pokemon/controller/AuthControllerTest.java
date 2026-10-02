package com.example.pokemon.controller;

import com.example.pokemon.repository.UserRepository;
import com.example.pokemon.security.CustomUserDetailsService;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldRegisterLoginAndAccessProtectedEndpoint()
            throws Exception {

        String email = "test-" + System.currentTimeMillis()
                + "@example.com";

        when(customUserDetailsService.loadUserByUsername(email))
                .thenAnswer(invocation ->
                        new User(
                                email,
                                "hashed",
                                List.of()));

        String registerJson = """
                {
                  "email": "%s",
                  "password": "password123"
                }
                """.formatted(email);

        String registerResponse = mockMvc.perform(
                post("/api/auth/register")
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = extractToken(registerResponse);

        mockMvc.perform(
                get("/api/me")
                        .header(
                                "Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(email));

        userRepository.deleteAll();
    }

    @Test
    void shouldRejectProtectedEndpointWithoutToken()
            throws Exception {

        mockMvc.perform(
                get("/api/me"))
                .andExpect(status().isForbidden());
    }

    private String extractToken(String json) {

        int start = json.indexOf("\"token\":\"")
                + "\"token\":\"".length();

        int end = json.indexOf("\"", start);

        return json.substring(start, end);
    }
}
