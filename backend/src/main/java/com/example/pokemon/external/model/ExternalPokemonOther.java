package com.example.pokemon.external.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExternalPokemonOther(

        @JsonProperty("official-artwork")
        ExternalPokemonOfficialArtwork officialArtwork

) {
}