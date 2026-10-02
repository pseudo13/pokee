package com.example.pokemon.external.model;

import java.util.List;

public record PokemonListResponse(
        Integer count,
        String next,
        String previous,
        List<PokemonListItem> results) {
}