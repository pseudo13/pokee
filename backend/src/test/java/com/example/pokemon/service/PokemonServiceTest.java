package com.example.pokemon.service;

import com.example.pokemon.dto.PokemonDto;
import com.example.pokemon.entity.Pokemon;
import com.example.pokemon.repository.PokemonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PokemonServiceTest {

    @Mock
    private PokemonRepository pokemonRepository;

    @InjectMocks
    private PokemonService pokemonService;

    @Test
    void shouldFindPokemonById() {

        Pokemon pokemon = new Pokemon(
                25,
                "pikachu",
                "image.png",
                4,
                60
        );

        when(pokemonRepository.findById(1L))
                .thenReturn(Optional.of(pokemon));

        PokemonDto result = pokemonService.findById(1L);

        assertThat(result.externalId()).isEqualTo(25);
        assertThat(result.name()).isEqualTo("pikachu");

        verify(pokemonRepository).findById(1L);
    }

    @Test
    void shouldCreatePokemon() {

        Pokemon pokemon = new Pokemon(
                25,
                "pikachu",
                "image.png",
                4,
                60
        );

        when(pokemonRepository.save(any(Pokemon.class)))
                .thenReturn(pokemon);

        PokemonDto input = new PokemonDto(
                null,
                25,
                "pikachu",
                "image.png",
                4,
                60
        );

        PokemonDto result = pokemonService.create(input);

        assertThat(result.name()).isEqualTo("pikachu");
        assertThat(result.externalId()).isEqualTo(25);

        verify(pokemonRepository).save(any(Pokemon.class));
    }
}