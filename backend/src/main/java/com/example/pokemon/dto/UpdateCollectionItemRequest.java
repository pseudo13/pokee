package com.example.pokemon.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateCollectionItemRequest(

        @Min(1) @Max(5) Integer rating,

        Boolean favorite,

        @Size(max = 2000) String notes) {
}