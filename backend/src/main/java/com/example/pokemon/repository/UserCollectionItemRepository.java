package com.example.pokemon.repository;

import com.example.pokemon.entity.UserCollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCollectionItemRepository
        extends JpaRepository<UserCollectionItem, Long> {

    List<UserCollectionItem> findAllByUserIdOrderByAddedAtDesc(Long userId);

    Optional<UserCollectionItem> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndPokemonId(
            Long userId,
            Long pokemonId);

    void deleteByIdAndUserId(
            Long id,
            Long userId);
}