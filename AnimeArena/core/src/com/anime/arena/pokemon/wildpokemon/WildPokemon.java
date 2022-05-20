package com.anime.arena.pokemon.wildpokemon;

public class WildPokemon {
    private int pokemonID;
    private double probability;
    private double customProbability;
    private int lowerLevel;
    private int higherLevel;

    public WildPokemon(int pokemonID, double probability, double customProbability, int lowerLevel, int higherLevel) {
        this.pokemonID = pokemonID;
        this.probability = probability;
        this.customProbability = customProbability;
        this.lowerLevel = lowerLevel;
        this.higherLevel = higherLevel;
    }

    public int generateLevel() {
        return (int) ((Math.random() * (higherLevel - lowerLevel)) + lowerLevel);
    }

    public int getPokemonID() {
        return pokemonID;
    }

    public double getProbability() {
        return probability;
    }

    public double getCustomProbability() {
        return customProbability;
    }

    public int getLowerLevel() {
        return lowerLevel;
    }

    public int getHigherLevel() {
        return higherLevel;
    }
}
