package com.example.pokemon.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pokemon")
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer externalId;

    @Column(nullable = false)
    private String name;

    private String imageUrl;

    private Integer height;

    private Integer weight;

    protected Pokemon() {
    }

    public Pokemon(
            Integer externalId,
            String name,
            String imageUrl,
            Integer height,
            Integer weight
    ) {
        this.externalId = externalId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.height = height;
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public Integer getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setExternalId(Integer externalId) {
        this.externalId = externalId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}