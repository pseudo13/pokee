package com.example.pokemon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient pokeApiRestClient() {
        return RestClient.builder()
                .baseUrl("https://pokeapi.co")
                .build();
    }
}