package com.example.pokemon.repository;

import com.example.pokemon.entity.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {

    Optional<Pokemon> findByExternalId(Integer externalId);

}