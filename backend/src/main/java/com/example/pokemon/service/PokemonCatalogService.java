package com.example.pokemon.service;

import com.example.pokemon.config.CacheConfig;
import com.example.pokemon.external.PokeApiClient;
import com.example.pokemon.external.model.PokemonListItem;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PokemonCatalogService {

    private final PokeApiClient pokeApiClient;

    public PokemonCatalogService(PokeApiClient pokeApiClient) {
        this.pokeApiClient = pokeApiClient;
    }

    @Cacheable(value = CacheConfig.POKEMON_NAMES, key = "'all'")
    public List<PokemonListItem> getPokemonNames() {

        var response = pokeApiClient.getPokemonList();

        if (response == null || response.results() == null) {
            return List.of();
        }

        return response.results();
    }
}