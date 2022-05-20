package com.anime.arena.api;

import com.anime.arena.dto.UsernamePasswordDTO;
import com.anime.arena.pokemon.BasePokemon;
import com.anime.arena.pokemon.BasePokemonFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.Json;
import com.google.gson.*;

import java.util.HashMap;
import java.util.Map;

public class PokemonAPI {
    private boolean loggedIn;
    private JsonArray pokedexResponse;
    public PokemonAPI() {
        loggedIn = false;
    }

    public void login(String username, String password) {
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl("http://kelsiegr.com/pokemononline/jsonlogin.php");

        UsernamePasswordDTO up = new UsernamePasswordDTO(username, password);
        Gson g = new Gson();
        String gson = g.toJson(up);
        request.setContent(gson);
        Gdx.app.log("userpassword", gson);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gdx.app.log("MSG", httpResponse.getResultAsString());
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("Failed", "Error");
            }

            @Override
            public void cancelled() {
                Gdx.app.log("Cancelled", "cancelled");
            }
        });

    }

    private String getPokedexString() {
        if (pokedexResponse != null) {
            return pokedexResponse.toString();
        }
        return "";
    }

    private Map<Integer, JsonObject> getPokedexMap() {
        if (pokedexResponse != null) {
            Map<Integer, JsonObject> pokemonList = new HashMap<Integer, JsonObject>();
            for (JsonElement obj : pokedexResponse) {
                JsonObject jo = obj.getAsJsonObject();
                pokemonList.put(jo.get("dex").getAsInt(), jo);
            }
        }
        return null;
    }


    public void getPokedex() {
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl("http://kelsiegr.com/loadDex.php");

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String jsonResponse = httpResponse.getResultAsString();
                Gdx.app.log("MSG", jsonResponse);
                JsonObject jsonObject = convertToJsonObject(jsonResponse);
                JsonArray pokemonArray = jsonObject.get("results").getAsJsonArray();
                pokedexResponse = pokemonArray;
                Gdx.app.log("JSONRESP", pokemonArray.size() + "");
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
    }

    private JsonObject convertToJsonObject(String str) {
        return new JsonParser().parse(str).getAsJsonObject();
    }
}
