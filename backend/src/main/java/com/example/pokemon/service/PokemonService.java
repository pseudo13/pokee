package com.example.pokemon.service;

import com.example.pokemon.config.CacheConfig;
import com.example.pokemon.dto.PokemonDto;
import com.example.pokemon.dto.PokemonSearchResultDto;
import com.example.pokemon.entity.Pokemon;
import com.example.pokemon.external.PokeApiClient;
import com.example.pokemon.external.model.ExternalPokemon;
import com.example.pokemon.repository.PokemonRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PokemonService {

    private final PokemonRepository pokemonRepository;
    private final PokeApiClient pokeApiClient;
    private final PokemonCatalogService pokemonCatalogService;

    public PokemonService(
            PokemonRepository pokemonRepository,
            PokeApiClient pokeApiClient,
            PokemonCatalogService pokemonCatalogService) {
        this.pokemonRepository = pokemonRepository;
        this.pokeApiClient = pokeApiClient;
        this.pokemonCatalogService = pokemonCatalogService;
    }

    public List<PokemonDto> findAll() {
        return pokemonRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public PokemonDto findById(Long id) {

        Pokemon pokemon = pokemonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pokemon not found: " + id));

        return toDto(pokemon);
    }

    public PokemonDto create(PokemonDto dto) {

        Pokemon pokemon = new Pokemon(
                dto.externalId(),
                dto.name(),
                dto.imageUrl(),
                dto.height(),
                dto.weight());

        Pokemon saved = pokemonRepository.save(pokemon);

        return toDto(saved);
    }

    public PokemonDto update(Long id, PokemonDto dto) {

        Pokemon pokemon = pokemonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pokemon not found: " + id));

        pokemon.setExternalId(dto.externalId());
        pokemon.setName(dto.name());
        pokemon.setImageUrl(dto.imageUrl());
        pokemon.setHeight(dto.height());
        pokemon.setWeight(dto.weight());

        Pokemon saved = pokemonRepository.save(pokemon);

        return toDto(saved);
    }

    public void delete(Long id) {

        if (!pokemonRepository.existsById(id)) {
            throw new RuntimeException("Pokemon not found: " + id);
        }

        pokemonRepository.deleteById(id);
    }

    @Cacheable(value = CacheConfig.POKEMON_BY_ID, key = "#externalId")
    public PokemonDto findOrFetch(Integer externalId) {

        var existing = pokemonRepository.findByExternalId(externalId);

        if (existing.isPresent()) {
            return toDto(existing.get());
        }

        ExternalPokemon externalPokemon = pokeApiClient.getPokemon(externalId.toString());

        Pokemon pokemon = new Pokemon(
                externalPokemon.id(),
                externalPokemon.name(),
                extractImageUrl(externalPokemon),
                externalPokemon.height(),
                externalPokemon.weight());

        Pokemon saved = pokemonRepository.save(pokemon);

        return toDto(saved);
    }

    private String extractImageUrl(ExternalPokemon pokemon) {

        if (pokemon.sprites() == null) {
            return null;
        }

        // if (pokemon.sprites().other() == null) {
        // return null;
        // }

        // if (pokemon.sprites().other().officialArtwork() == null) {
        // return null;
        // }

        return pokemon.sprites()
                .frontDefault();
    }

    private PokemonDto toDto(Pokemon pokemon) {

        return new PokemonDto(
                pokemon.getId(),
                pokemon.getExternalId(),
                pokemon.getName(),
                pokemon.getImageUrl(),
                pokemon.getHeight(),
                pokemon.getWeight());
    }

    @Cacheable(value = CacheConfig.POKEMON_SEARCH, key = "#search.trim().toLowerCase()")
    public List<PokemonSearchResultDto> search(String search) {

        if (search == null || search.isBlank()) {
            return List.of();
        }

        String normalizedSearch = search
                .trim()
                .toLowerCase();

        return pokemonCatalogService.getPokemonNames()
                .stream()
                .filter(pokemon -> pokemon.name()
                        .toLowerCase()
                        .contains(normalizedSearch))
                .limit(20)
                .map(pokemon -> new PokemonSearchResultDto(
                        extractExternalId(pokemon.url()),
                        pokemon.name()))
                .toList();
    }

    private Integer extractExternalId(String url) {

        String cleanUrl = url.endsWith("/")
                ? url.substring(0, url.length() - 1)
                : url;

        String[] parts = cleanUrl.split("/");

        return Integer.valueOf(parts[parts.length - 1]);
    }
}