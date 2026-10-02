package com.example.pokemon.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "user_collection_items", uniqueConstraints = @UniqueConstraint(name = "uk_user_pokemon", columnNames = {
        "user_id",
        "pokemon_id"
}))
public class UserCollectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pokemon_id", nullable = false)
    private Pokemon pokemon;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private boolean favorite;

    @Column(length = 2000)
    private String notes;

    @Column(nullable = false)
    private Instant addedAt;

    protected UserCollectionItem() {
    }

    public UserCollectionItem(
            User user,
            Pokemon pokemon,
            Integer rating,
            boolean favorite,
            String notes) {
        this.user = user;
        this.pokemon = pokemon;
        this.rating = rating;
        this.favorite = favorite;
        this.notes = notes;
        this.addedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public Integer getRating() {
        return rating;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}