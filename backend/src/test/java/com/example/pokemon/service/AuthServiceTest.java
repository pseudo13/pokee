package com.example.pokemon.service;

import com.example.pokemon.dto.RegisterRequest;
import com.example.pokemon.entity.User;
import com.example.pokemon.repository.UserRepository;
import com.example.pokemon.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldHashPasswordDuringRegistration() {

        when(userRepository.existsByEmailIgnoreCase(
                "bao@example.com")).thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("HASHED_PASSWORD");

        when(jwtService.generateToken("bao@example.com"))
                .thenReturn("JWT_TOKEN");

        var result = authService.register(
                new RegisterRequest(
                        "bao@example.com",
                        "password123"));

        assertThat(result.token())
                .isEqualTo("JWT_TOKEN");

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(argThat(user -> user.getPasswordHash()
                        .equals("HASHED_PASSWORD")));
    }
}