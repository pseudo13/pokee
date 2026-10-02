package com.example.pokemon.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddCollectionItemRequest(

        @NotNull Long pokemonId,

        @NotNull @Min(1) @Max(5) Integer rating,

        boolean favorite,

        @Size(max = 2000) String notes) {
}