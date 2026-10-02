package com.example.pokemon.controller;

import com.example.pokemon.dto.PokemonDto;
import com.example.pokemon.security.CustomUserDetailsService;
import com.example.pokemon.security.JwtService;
import com.example.pokemon.service.PokemonService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PokemonController.class)
@AutoConfigureMockMvc(addFilters = false)
class PokemonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PokemonService pokemonService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldReturnAllPokemon() throws Exception {

        when(pokemonService.findAll())
                .thenReturn(List.of(new PokemonDto(
                        1L,
                        25,
                        "pikachu",
                        "image.png",
                        4,
                        60)));

        mockMvc.perform(get("/api/pokemon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("pikachu"))
                .andExpect(jsonPath("$[0].externalId").value(25));

        verify(pokemonService).findAll();
    }

    @Test
    void shouldReturnPokemonById() throws Exception {

        PokemonDto pikachu = new PokemonDto(
                1L,
                25,
                "pikachu",
                "image.png",
                4,
                60);

        when(pokemonService.findById(anyLong()))
                .thenReturn(pikachu);

        mockMvc.perform(get("/api/pokemon/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("pikachu"))
                .andExpect(jsonPath("$.externalId").value(25));
    }
}
