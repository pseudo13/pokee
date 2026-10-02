package com.example.pokemon.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            "this-is-a-very-long-secret-key-for-testing-123456789",
            86_400_000);

    @Test
    void shouldGenerateAndReadToken() {

        String token = jwtService.generateToken(
                "bao@example.com");

        assertThat(token).isNotBlank();

        assertThat(
                jwtService.extractUsername(token)).isEqualTo("bao@example.com");

        assertThat(
                jwtService.isValid(token)).isTrue();
    }

    @Test
    void shouldRejectInvalidToken() {

        assertThat(
                jwtService.isValid("invalid-token")).isFalse();
    }
}