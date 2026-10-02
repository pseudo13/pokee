package com.example.pokemon.dto;

import java.time.Instant;

public record CollectionItemDto(
        Long id,
        Long pokemonId,
        Integer externalId,
        String name,
        String imageUrl,
        Integer rating,
        boolean favorite,
        String notes,
        Instant addedAt) {
}