package com.example.pokemon.service;

import com.example.pokemon.external.PokeApiClient;
import com.example.pokemon.external.model.PokemonListItem;
import com.example.pokemon.external.model.PokemonListResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class PokemonCatalogServiceCacheTest {

    @Autowired
    private PokemonCatalogService pokemonCatalogService;

    @MockBean
    private PokeApiClient pokeApiClient;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void shouldCachePokemonCatalog() {

        PokemonListResponse response = new PokemonListResponse(
                1,
                null,
                null,
                List.of(
                        new PokemonListItem(
                                "pikachu",
                                "https://pokeapi.co/api/v2/pokemon/25/")));

        when(pokeApiClient.getPokemonList())
                .thenReturn(response);

        var first = pokemonCatalogService.getPokemonNames();
        var second = pokemonCatalogService.getPokemonNames();

        assertThat(first).hasSize(1);
        assertThat(second).hasSize(1);

        verify(pokeApiClient, times(1))
                .getPokemonList();
    }
}