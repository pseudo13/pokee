package com.example.pokemon.controller;

import com.example.pokemon.dto.PokemonDto;
import com.example.pokemon.dto.PokemonSearchResultDto;
import com.example.pokemon.service.PokemonService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    // @GetMapping
    // public List<PokemonDto> findAll() {
    //     return pokemonService.findAll();
    // }

    @GetMapping("/{id}")
    public PokemonDto findById(@PathVariable Long id) {
        return pokemonService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PokemonDto create(@RequestBody PokemonDto dto) {
        return pokemonService.create(dto);
    }

    @PutMapping("/{id}")
    public PokemonDto update(
            @PathVariable Long id,
            @RequestBody PokemonDto dto) {
        return pokemonService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        pokemonService.delete(id);
    }

    @GetMapping("/external/{externalId}")
    public PokemonDto findExternal(
            @PathVariable Integer externalId) {
        return pokemonService.findOrFetch(externalId);
    }

    @GetMapping
    public List<PokemonSearchResultDto> search(
            @RequestParam(required = false) String search) {
        if (search == null || search.isBlank()) {
            return pokemonService.findAll()
                    .stream()
                    .map(pokemon -> new PokemonSearchResultDto(
                            pokemon.externalId(),
                            pokemon.name()))
                    .toList();
        }

        return pokemonService.search(search);
    }
}