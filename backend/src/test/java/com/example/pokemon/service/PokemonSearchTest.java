package com.example.pokemon.service;

import com.example.pokemon.external.model.PokemonListItem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PokemonSearchTest {

    @Mock
    private PokemonCatalogService pokemonCatalogService;

    @InjectMocks
    private PokemonService pokemonService;

    @Test
    void shouldFindPokemonByName() {

        when(pokemonCatalogService.getPokemonNames())
                .thenReturn(List.of(
                        new PokemonListItem(
                                "pikachu",
                                "https://pokeapi.co/api/v2/pokemon/25/"),
                        new PokemonListItem(
                                "bulbasaur",
                                "https://pokeapi.co/api/v2/pokemon/1/")));

        var result = pokemonService.search("pika");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("pikachu");
        assertThat(result.get(0).externalId()).isEqualTo(25);
    }

    @Test
    void shouldSearchCaseInsensitive() {

        when(pokemonCatalogService.getPokemonNames())
                .thenReturn(List.of(
                        new PokemonListItem(
                                "pikachu",
                                "https://pokeapi.co/api/v2/pokemon/25/")));

        var result = pokemonService.search("PIKA");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("pikachu");
    }

    @Test
    void shouldReturnMaximumTwentyResults() {

        List<PokemonListItem> pokemon = java.util.stream.IntStream.rangeClosed(1, 50)
                .mapToObj(id -> new PokemonListItem(
                        "pokemon" + id,
                        "https://pokeapi.co/api/v2/pokemon/" + id + "/"))
                .toList();

        when(pokemonCatalogService.getPokemonNames())
                .thenReturn(pokemon);

        var result = pokemonService.search("pokemon");

        assertThat(result).hasSize(20);
    }

    @SpringBootTest
    class PokemonSearchCacheTest {

        @Autowired
        private PokemonService pokemonService;

        @MockBean
        private PokemonCatalogService pokemonCatalogService;

        @Test
        void shouldCacheSearchResult() {

            when(pokemonCatalogService.getPokemonNames())
                    .thenReturn(List.of(
                            new PokemonListItem(
                                    "pikachu",
                                    "https://pokeapi.co/api/v2/pokemon/25/")));

            var first = pokemonService.search("pika");
            var second = pokemonService.search("pika");

            assertThat(first).hasSize(1);
            assertThat(second).hasSize(1);

            verify(pokemonCatalogService, times(1))
                    .getPokemonNames();
        }
    }

    @Test
    void shouldNormalizeWhitespaceAroundQuery() {

        when(pokemonCatalogService.getPokemonNames())
                .thenReturn(List.of(
                        new PokemonListItem(
                                "pikachu",
                                "https://pokeapi.co/api/v2/pokemon/25/")));

        var result = pokemonService.search("  PIKA  ");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("pikachu");
    }
}