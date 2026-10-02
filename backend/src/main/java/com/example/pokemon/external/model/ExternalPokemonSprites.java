package com.example.pokemon.external.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExternalPokemonSprites(
        @JsonProperty("front_default")
        String frontDefault
) {
}