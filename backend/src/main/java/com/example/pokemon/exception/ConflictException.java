package com.example.pokemon.exception;

public class ConflictException
        extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}