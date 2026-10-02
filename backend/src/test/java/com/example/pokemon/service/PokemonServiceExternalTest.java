package com.example.pokemon.service;

import com.example.pokemon.entity.Pokemon;
import com.example.pokemon.external.PokeApiClient;
import com.example.pokemon.external.model.ExternalPokemon;
import com.example.pokemon.external.model.ExternalPokemonSprites;
import com.example.pokemon.repository.PokemonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PokemonServiceExternalTest {

    @Mock
    private PokemonRepository pokemonRepository;

    @Mock
    private PokeApiClient pokeApiClient;

    @InjectMocks
    private PokemonService pokemonService;

    @Test
    void shouldFetchPokemonFromApiWhenNotInDatabase() {

        when(pokemonRepository.findByExternalId(25))
                .thenReturn(Optional.empty());

        ExternalPokemon externalPokemon = new ExternalPokemon(
                25,
                "pikachu",
                4,
                60,
                new ExternalPokemonSprites(
                        "https://example.com/pikachu.png"
                ));

        when(pokeApiClient.getPokemon("25"))
                .thenReturn(externalPokemon);

        Pokemon savedPokemon = new Pokemon(
                25,
                "pikachu",
                "https://example.com/pikachu.png",
                4,
                60);

        when(pokemonRepository.save(any(Pokemon.class)))
                .thenReturn(savedPokemon);

        var result = pokemonService.findOrFetch(25);

        assertThat(result.externalId()).isEqualTo(25);
        assertThat(result.name()).isEqualTo("pikachu");
        assertThat(result.imageUrl())
                .isEqualTo("https://example.com/pikachu.png");

        verify(pokeApiClient).getPokemon("25");
        verify(pokemonRepository).save(any(Pokemon.class));
    }

    @Test
void shouldUseDatabaseWhenPokemonAlreadyExists() {

    Pokemon existingPokemon = new Pokemon(
            25,
            "pikachu",
            "https://example.com/pikachu.png",
            4,
            60
    );

    when(pokemonRepository.findByExternalId(25))
            .thenReturn(Optional.of(existingPokemon));

    var result = pokemonService.findOrFetch(25);

    assertThat(result.externalId()).isEqualTo(25);
    assertThat(result.name()).isEqualTo("pikachu");

    verify(pokemonRepository).findByExternalId(25);

    verifyNoInteractions(pokeApiClient);
}
}