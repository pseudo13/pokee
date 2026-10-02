package com.example.pokemon.service;

import com.example.pokemon.dto.AddCollectionItemRequest;
import com.example.pokemon.dto.CollectionItemDto;
import com.example.pokemon.dto.UpdateCollectionItemRequest;
import com.example.pokemon.entity.UserCollectionItem;
import com.example.pokemon.exception.ConflictException;
import com.example.pokemon.exception.ResourceNotFoundException;
import com.example.pokemon.repository.PokemonRepository;
import com.example.pokemon.repository.UserCollectionItemRepository;
import com.example.pokemon.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionService {

    private final UserRepository userRepository;
    private final PokemonRepository pokemonRepository;
    private final UserCollectionItemRepository collectionRepository;

    public CollectionService(
            UserRepository userRepository,
            PokemonRepository pokemonRepository,
            UserCollectionItemRepository collectionRepository) {
        this.userRepository = userRepository;
        this.pokemonRepository = pokemonRepository;
        this.collectionRepository = collectionRepository;
    }

    public List<CollectionItemDto> getCollection(String email) {

        var user = getUser(email);

        return collectionRepository
                .findAllByUserIdOrderByAddedAtDesc(user.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    public CollectionItemDto add(
            String email,
            AddCollectionItemRequest request) {

        var user = getUser(email);

        var pokemon = pokemonRepository
                .findById(request.pokemonId())
                .orElseThrow(() -> new RuntimeException(
                        "Pokemon not found: "
                                + request.pokemonId()));

        boolean alreadyExists = collectionRepository
                .existsByUserIdAndPokemonId(
                        user.getId(),
                        pokemon.getId());

        if (alreadyExists) {
            throw new ConflictException(
                    "Pokemon already in collection");
        }

        UserCollectionItem item = new UserCollectionItem(
                user,
                pokemon,
                request.rating(),
                request.favorite(),
                request.notes());

        UserCollectionItem saved = collectionRepository.save(item);

        return toDto(saved);
    }

    public CollectionItemDto update(
            String email,
            Long collectionItemId,
            UpdateCollectionItemRequest request) {

        var user = getUser(email);

        UserCollectionItem item = collectionRepository
                .findByIdAndUserId(
                        collectionItemId,
                        user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Collection item not found"));

        if (request.rating() != null) {
            item.setRating(request.rating());
        }

        if (request.favorite() != null) {
            item.setFavorite(request.favorite());
        }

        if (request.notes() != null) {
            item.setNotes(request.notes());
        }

        UserCollectionItem saved = collectionRepository.save(item);

        return toDto(saved);
    }

    public void delete(
            String email,
            Long collectionItemId) {

        var user = getUser(email);

        var item = collectionRepository
                .findByIdAndUserId(
                        collectionItemId,
                        user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Collection item not found"));

        collectionRepository.delete(item);
    }

    private com.example.pokemon.entity.User getUser(
            String email) {
        return userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException(
                        "User not found"));
    }

    private CollectionItemDto toDto(
            UserCollectionItem item) {

        var pokemon = item.getPokemon();

        return new CollectionItemDto(
                item.getId(),
                pokemon.getId(),
                pokemon.getExternalId(),
                pokemon.getName(),
                pokemon.getImageUrl(),
                item.getRating(),
                item.isFavorite(),
                item.getNotes(),
                item.getAddedAt());
    }
}