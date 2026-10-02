package com.example.pokemon.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
public class CacheConfig {

    public static final String POKEMON_NAMES = "pokemonNames";
    public static final String POKEMON_SEARCH = "pokemonSearch";
    public static final String POKEMON_BY_ID = "pokemonById";

    @Bean
    CacheManager cacheManager() {

        CaffeineCache pokemonNames = new CaffeineCache(
                POKEMON_NAMES,
                Caffeine.newBuilder()
                        .maximumSize(1)
                        .expireAfterWrite(Duration.ofHours(24))
                        .recordStats()
                        .build());

        CaffeineCache pokemonSearch = new CaffeineCache(
                POKEMON_SEARCH,
                Caffeine.newBuilder()
                        .maximumSize(500)
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .recordStats()
                        .build());

        CaffeineCache pokemonById = new CaffeineCache(
                POKEMON_BY_ID,
                Caffeine.newBuilder()
                        .maximumSize(500)
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .recordStats()
                        .build());

        SimpleCacheManager cacheManager = new SimpleCacheManager();

        cacheManager.setCaches(List.of(
                pokemonNames,
                pokemonSearch,
                pokemonById));

        return cacheManager;
    }
}