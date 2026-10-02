package com.example.pokemon.dto;

public record PokemonDto(
        Long id,
        Integer externalId,
        String name,
        String imageUrl,
        Integer height,
        Integer weight
) {
}