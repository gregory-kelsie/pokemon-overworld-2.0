package com.anime.arena.pokemon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.compression.lzma.Base;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasePokemonFactory {
    private String basePokemonResponse;
    private Map<Integer, JsonObject> database;
    private Map<Integer, List<JsonObject>> pokemonFormDatabase;
    private Map<Integer, List<JsonObject>> evolutionDatabase;
    private Map<Integer, List<String>> abilityDatabase;

    private static final boolean useDatabase = true;
    public BasePokemonFactory(String basePokemonResponse, Map<Integer, JsonObject> database) {
        this.basePokemonResponse = basePokemonResponse;
        this.database = database;
    }

    public List<JsonObject> getForms(int dexNumber) {
        List<JsonObject> forms = new ArrayList<JsonObject>();
        for (Integer key : database.keySet()) {
            JsonObject pokemon = database.get(key);
            if (pokemon.get("dex").getAsInt() == dexNumber) {
                forms.add(pokemon);
            }
        }
        return forms;
    }

    public boolean hasDexNumber(int dexNumber) {
        return database.containsKey(dexNumber);
    }

    public Map<Integer, JsonObject> getDatabase() {
        return database;
    }

    public void setEvolutionDatabase(Map<Integer, List<JsonObject>> evolutionDatabase) {
        this.evolutionDatabase = evolutionDatabase;
    }

    public void setAbilityDatabase(Map<Integer, List<String>> abilityDatabase) {
        this.abilityDatabase = abilityDatabase;
    }

    public Map<Integer, List<String>> getAbilityDatabase() {
        return abilityDatabase;
    }





    private List<Evolution> getEvolutions(int pid) {
        if (evolutionDatabase.containsKey(pid)) {
            List<JsonObject> evolutionJson = evolutionDatabase.get(pid);
            List<Evolution> evolutions = new ArrayList<Evolution>();
            for (JsonObject obj : evolutionJson) {
                String gender;
                if (!obj.get("gender").isJsonNull()) {
                    gender = obj.get("gender").getAsString();
                } else {
                    gender = null;
                }
                Evolution evolution = new Evolution(obj.get("level").getAsInt(), obj.get("evolved-form").getAsInt(),
                        obj.get("method").getAsInt(), obj.get("attack").getAsBoolean(), obj.get("defense").getAsBoolean(), obj.get("equal").getAsBoolean(),
                        obj.get("second-evolution").getAsInt(), gender, obj.get("nature-type").getAsInt());
                evolutions.add(evolution);
            }
            return evolutions;
        }
        return new ArrayList<Evolution>();
    }

    public BasePokemon createBasePokemon(JsonObject pokemon) {
        if (pokemon != null) {
            BasePokemon base = new BasePokemon();
            base.setPID(pokemon.get("pid").getAsInt());
            base.setName(pokemon.get("name").getAsString());
            base.setDexNumber(pokemon.get("dex").getAsInt());
            base.setClassification(pokemon.get("classification").getAsString());
            base.setDescription(pokemon.get("description").getAsString());
            base.setImage(pokemon.get("img").getAsString());
            base.setCaptureRate(pokemon.get("capture_rate").getAsDouble());
            base.setHeight(pokemon.get("height").getAsDouble());
            base.setWeight(pokemon.get("weight").getAsDouble());
            base.setBaseExp(pokemon.get("base_exp").getAsInt());
            base.setFirstAbility(pokemon.get("first_ability").getAsInt());
            base.setSecondAbility(pokemon.get("second_ability").getAsInt());
            base.setHiddenAbility(pokemon.get("hidden_ability").getAsInt());
            base.setFirstType(pokemon.get("type_one").getAsInt());
            base.setSecondType(pokemon.get("type_two").getAsInt());
            base.setBaseStats(new int[] {pokemon.get("base_hp").getAsInt(), pokemon.get("base_atk").getAsInt(),
                    pokemon.get("base_def").getAsInt(),pokemon.get("base_spatk").getAsInt(),pokemon.get("base_spdef").getAsInt(),
                    pokemon.get("base_spd").getAsInt()});
            base.setEvYield(new int[] {pokemon.get("yield_hp").getAsInt(), pokemon.get("yield_atk").getAsInt(),
                    pokemon.get("yield_def").getAsInt(),pokemon.get("yield_spatk").getAsInt(),pokemon.get("yield_spdef").getAsInt(),
                    pokemon.get("yield_spd").getAsInt()});
            base.setEvolutionList(getEvolutions(base.getPID()));
            return base;
        }
        return null;
    }

    public BasePokemon createBasePokemonDB(int pokemonID) {
        return createBasePokemon(database.get(pokemonID));
    }

    public BasePokemon createBasePokemonString(int pokemonID) {
        JsonArray pokemonArray  = new JsonParser().parse(basePokemonResponse).getAsJsonArray();
        int i = 0;
        boolean foundPokemon = false;
        JsonObject pokemon = null;
        for (JsonElement obj : pokemonArray) {
            JsonObject jo = obj.getAsJsonObject();
            if (jo.get("pid").getAsInt() == pokemonID) {
                pokemon = jo;
                break;
            }
        }
        return createBasePokemon(pokemon);
    }

    public BasePokemon createBasePokemon(int dexNumber) {
        if (useDatabase) {
            return createBasePokemonDB(dexNumber);
        } else {
            return createBasePokemonString(dexNumber);
        }
    }
}
