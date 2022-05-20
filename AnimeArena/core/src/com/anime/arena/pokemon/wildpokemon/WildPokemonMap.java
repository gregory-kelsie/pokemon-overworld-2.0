package com.anime.arena.pokemon.wildpokemon;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class WildPokemonMap {
    //TODO: Brainstorming about grass having an id in Tiled. Ex one patch of grass could have id = 2 and that set of grass has only eevees. Something like that
    private List<WildPokemon> wildPokemonList;
    private double currentProbability;

    public WildPokemonMap() {
        wildPokemonList = new ArrayList<WildPokemon>();
        currentProbability = 0.0;
    }

    public WildPokemon getRandomPokemon() {
        double pokemonRoll = Math.random();
        for (int i = 0; i < wildPokemonList.size(); i++) {
            if (pokemonRoll <= wildPokemonList.get(i).getCustomProbability()) {
                return wildPokemonList.get(i);
            }
        }
        if (wildPokemonList.size() > 0) {
            return wildPokemonList.get(wildPokemonList.size() - 1);
        } else {
            return null;
        }
    }

    public void addWildPokemon(int pokemonID, double probability, int lowerLevel, int higherLevel) {
        if (currentProbability < 1.0) {
            currentProbability = Math.min(1.0, currentProbability + probability);
            wildPokemonList.add(new WildPokemon(pokemonID, probability, currentProbability, lowerLevel, higherLevel));
        } else {
            Gdx.app.log("WildPokemonMap", "Trying to add pokemon (" + pokemonID + ") but the currentProbability is at its max");
        }
    }



}
