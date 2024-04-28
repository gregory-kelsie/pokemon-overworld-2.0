package com.anime.arena.pokemon.wildpokemon;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WildPokemonMap {
    //TODO: Brainstorming about grass having an id in Tiled. Ex one patch of grass could have id = 2 and that set of grass has only eevees. Something like that
    private List<WildPokemon> wildPokemonList;
    private List<WildPokemon> nightWildPokemonList;
    private double currentProbability;

    public WildPokemonMap() {
        wildPokemonList = new ArrayList<WildPokemon>();
        nightWildPokemonList = new ArrayList<WildPokemon>();
        currentProbability = 0.0;
    }

    public WildPokemon getRandomPokemon() {
        double pokemonRoll = Math.random();
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        List<WildPokemon> currentWildPokemonList = ((hour > 5 && hour < 20) || nightWildPokemonList == null || nightWildPokemonList.size() == 0) ? wildPokemonList : nightWildPokemonList;
        for (int i = 0; i < currentWildPokemonList.size(); i++) {
            if (pokemonRoll <= currentWildPokemonList.get(i).getCustomProbability()) {
                return currentWildPokemonList.get(i);
            }
        }
        if (currentWildPokemonList.size() > 0) {
            return currentWildPokemonList.get(currentWildPokemonList.size() - 1);
        } else {
            return null;
        }
    }

    public void addWildPokemon(int pokemonID, double probability, int lowerLevel, int higherLevel, boolean isDay) {
        List<WildPokemon> currentWildPokemonList = isDay ? wildPokemonList : nightWildPokemonList;
        if (currentProbability < 1.0) {
            currentProbability = Math.min(1.0, currentProbability + probability);
            currentWildPokemonList.add(new WildPokemon(pokemonID, probability, currentProbability, lowerLevel, higherLevel));
        } else {
            Gdx.app.log("WildPokemonMap", "Trying to add pokemon (" + pokemonID + ") but the currentProbability is at its max");
        }
    }

    public void resetProbability() {
        currentProbability = 0.0;
    }



}
