package com.anime.arena.quest;

public class PokemonQuest {
    private int pokemonID; //Which pokemon is being hunted
    private int pokemonTotalAmount;
    private int pokemonCurrentAmount;

    public PokemonQuest(int pokemonID, int pokemonTotalAmount) {
        this.pokemonID = pokemonID;
        this.pokemonTotalAmount = pokemonTotalAmount;
    }

    public PokemonQuest(int pokemonID, int pokemonTotalAmount, int pokemonCurrentAmount) {
        this(pokemonID, pokemonTotalAmount);
        this.pokemonCurrentAmount = pokemonCurrentAmount;
    }

    public void setPokemonCurrentAmount(int pokemonCurrentAmount) {
        this.pokemonCurrentAmount = pokemonCurrentAmount;
    }


    public int getPokemonID() {
        return pokemonID;
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
