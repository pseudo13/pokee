package com.example.pokemon.controller;

import com.example.pokemon.dto.AddCollectionItemRequest;
import com.example.pokemon.dto.CollectionItemDto;
import com.example.pokemon.dto.UpdateCollectionItemRequest;
import com.example.pokemon.service.CollectionService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/collection")
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(
            CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping
    public List<CollectionItemDto> getCollection(
            Authentication authentication) {
        return collectionService.getCollection(
                authentication.getName());
    }

    @PostMapping
    public CollectionItemDto add(
            Authentication authentication,
            @Valid @RequestBody AddCollectionItemRequest request) {
        return collectionService.add(
                authentication.getName(),
                request);
    }

    @PatchMapping("/{id}")
    public CollectionItemDto update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCollectionItemRequest request) {
        return collectionService.update(
                authentication.getName(),
                id,
                request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            Authentication authentication,
            @PathVariable Long id) {
        collectionService.delete(
                authentication.getName(),
                id);
    }
}