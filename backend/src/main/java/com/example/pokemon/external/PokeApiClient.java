package com.example.pokemon.external;

import com.example.pokemon.external.model.ExternalPokemon;
import com.example.pokemon.external.model.PokemonListResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PokeApiClient {

    private final RestClient restClient;

    public PokeApiClient(RestClient pokeApiRestClient) {
        this.restClient = pokeApiRestClient;
    }

    public ExternalPokemon getPokemon(String nameOrId) {

        return restClient.get()
                .uri("/api/v2/pokemon/{nameOrId}", nameOrId)
                .retrieve()
                .body(ExternalPokemon.class);
    }

    public PokemonListResponse getPokemonList() {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v2/pokemon")
                        .queryParam("limit", 2000)
                        .build())
                .retrieve()
                .body(PokemonListResponse.class);
    }
}