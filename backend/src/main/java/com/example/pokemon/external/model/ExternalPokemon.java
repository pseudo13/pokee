package com.example.pokemon.external.model;

public record ExternalPokemon(
        Integer id,
        String name,
        Integer height,
        Integer weight,
        ExternalPokemonSprites sprites
) {
}