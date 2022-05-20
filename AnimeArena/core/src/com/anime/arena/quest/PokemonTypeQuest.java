package com.anime.arena.quest;

public class PokemonTypeQuest {
    private int pokemonType; //Which pokemon type is being hunted (fire, water, grass etc)
    private int pokemonTotalAmount;
    private int pokemonCurrentAmount;

    public PokemonTypeQuest(int pokemonType, int pokemonTotalAmount) {
        this.pokemonType = pokemonType;
        this.pokemonTotalAmount = pokemonTotalAmount;
    }

    public PokemonTypeQuest(int pokemonType, int pokemonTotalAmount, int pokemonCurrentAmount) {
        this(pokemonType, pokemonTotalAmount);
        this.pokemonCurrentAmount = pokemonCurrentAmount;
    }

    public void setPokemonCurrentAmount(int pokemonCurrentAmount) {
        this.pokemonCurrentAmount = pokemonCurrentAmount;
    }

    public int getPokemonType() {
        return pokemonType;
    }

    public void increment() {
        pokemonCurrentAmount = Math.min(pokemonCurrentAmount + 1, pokemonTotalAmount);
    }

    public boolean isComplete() {
        if (pokemonTotalAmount == pokemonCurrentAmount) {
            return true;
        }
        return false;
    }
}