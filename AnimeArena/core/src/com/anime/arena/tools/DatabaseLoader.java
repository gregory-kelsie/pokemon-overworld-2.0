package com.anime.arena.tools;

import com.anime.arena.items.ItemFactory;
import com.anime.arena.pokemon.BasePokemonFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;

public class DatabaseLoader {
    private BasePokemonFactory basePokemonFactory;
    private ItemFactory itemFactory;
    private int loadedPokemon;
    private int loadedEvolutions;
    private int loadedAbilities;
    private int loadedItems;

    public DatabaseLoader() {
        loadedPokemon = 0;
        loadedEvolutions = 0;
        loadedAbilities = 0;
        loadedItems = 0;
    }

    public void start() {
        Gdx.app.log("DatabaseLoader::start", "Start loading resources");
        getPokedex();
    }

    public boolean hasLoadedEssentials() {
        if (loadedPokemon == 1 && loadedEvolutions == 1 && loadedAbilities == 1 && loadedItems == 1) {
            return true;
        }
        return false;
    }

    public BasePokemonFactory getBasePokemonFactory() {
        return basePokemonFactory;
    }

    public ItemFactory getItemFactory() { return itemFactory; }


    private void getEvolutions() {
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl("http://kelsiegr.com/loadEvolutions.php");
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String jsonResponse = httpResponse.getResultAsString();
                Gdx.app.log("LoadEvolutions", jsonResponse);
                JsonObject jsonObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
                JsonArray evolutionArray = jsonObject.get("results").getAsJsonArray();
                Map<Integer, List<JsonObject>> evolutionMap = new HashMap<Integer, List<JsonObject>>();
                for (JsonElement obj : evolutionArray) {
                    JsonObject jo = obj.getAsJsonObject();
                    int baseForm = jo.get("base-form").getAsInt();
                    if (evolutionMap.containsKey(baseForm)) {
                        evolutionMap.get(baseForm).add(jo);
                    } else {
                        evolutionMap.put(baseForm, new ArrayList<JsonObject>());
                        evolutionMap.get(baseForm).add(jo);
                    }
                }
                basePokemonFactory.setEvolutionDatabase(evolutionMap);
                loadedEvolutions = 1;
                getAbilities();
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("Failed LoadEvolution", t.getMessage());
                loadedEvolutions = -1;
            }

            @Override
            public void cancelled() {
                Gdx.app.log("Cancelled", "cancelled");
            }
        });
    }

    private void getAbilities() {
        Gdx.app.log("DatabaseLoader::getAbilities", "Start Loading Abilities");
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl("http://kelsiegr.com/loadAbilities.php");

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String jsonResponse = httpResponse.getResultAsString();
                Gdx.app.log("Ability Response", jsonResponse);
                JsonObject jsonObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
                JsonArray abilityArray = jsonObject.get("results").getAsJsonArray();
                Map<Integer, List<String>> abilityDatabase = new HashMap<Integer, List<String>>();
                for (JsonElement obj : abilityArray) {
                    JsonObject jo = obj.getAsJsonObject();
                    int abilityID = jo.get("id").getAsInt();
                    String abilityName = jo.get("name").getAsString();
                    String abilityDescription = jo.get("description").getAsString();
                    abilityDatabase.put(abilityID, new ArrayList<String>(Arrays.asList(abilityName, abilityDescription)));
                }
                if (basePokemonFactory != null) {
                    basePokemonFactory.setAbilityDatabase(abilityDatabase);
                    loadedAbilities = 1;
                    Gdx.app.log("Success", "Populated abilities from DB");
                    getItems();
                } else {
                    Gdx.app.log("DatabaseLoader::getAbilities", "basePokemonFactory is null");
                }

            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("Failed", t.getMessage());
            }

            @Override
            public void cancelled() {
                Gdx.app.log("Cancelled", "cancelled");
            }
        });
        //request.setContent();

    }

    private void getItems() {
        Gdx.app.log("DatabaseLoader::getItems", "Start Loading Items");
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl("http://kelsiegr.com/loadItems.php");

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String jsonResponse = httpResponse.getResultAsString();
                Gdx.app.log("Items Response", jsonResponse);
                JsonObject jsonObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
                JsonArray itemsArray = jsonObject.get("results").getAsJsonArray();
                Map<Integer, JsonObject> itemDatabase = new HashMap<Integer, JsonObject>();
                for (JsonElement obj : itemsArray) {
                    JsonObject jo = obj.getAsJsonObject();
                    int itemID = jo.get("item_id").getAsInt();
                    itemDatabase.put(itemID, jo);
                }
                itemFactory = new ItemFactory(itemsArray.toString(), itemDatabase);
                loadedItems = 1;
                Gdx.app.log("Success", "Populated items from DB");

            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("Failed", t.getMessage());
            }

            @Override
            public void cancelled() {
                Gdx.app.log("Cancelled", "cancelled");
            }
        });
        Gdx.app.log("next", "line");
        //request.setContent();
    }


    private void getPokedex() {
        basePokemonFactory = null;
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl("http://kelsiegr.com/loadDex.php");

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String jsonResponse = httpResponse.getResultAsString();
                Gdx.app.log("MSG", jsonResponse);
                JsonObject jsonObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
                JsonArray pokemonArray = jsonObject.get("results").getAsJsonArray();
                Map<Integer, JsonObject> pokemonList = new HashMap<Integer, JsonObject>();
                for (JsonElement obj : pokemonArray) {
                    JsonObject jo = obj.getAsJsonObject();
                    int pid = jo.get("pid").getAsInt();
                    if (pokemonList.containsKey(pid)) {
                        Gdx.app.log("Database Error", "Pokemon Database has PID: " + pid);
                    } else {
                        pokemonList.put(pid, jo);
                    }
                }
                basePokemonFactory = new BasePokemonFactory(pokemonArray.toString(), pokemonList);
                Gdx.app.log("DatabaseLoader::getPokedex", "" + "Start loading Evolutions");
                getEvolutions();
                Gdx.app.log("JSONRESP", pokemonArray.size() + "");
                Gdx.app.log("DatabaseLoader::getPokedex", "Done loading Pokedex");
                loadedPokemon = 1;

            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("Failed LoadPokemon", t.getMessage());
                loadedPokemon = -1;
            }

            @Override
            public void cancelled() {
                Gdx.app.log("Cancelled", "cancelled");
            }
        });
    }
}
