package com.anime.arena.api;

import com.anime.arena.dto.PlayerProfile;
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
    private boolean fetchingResponse;
    private JsonArray pokedexResponse;

    private PlayerProfile playerProfile;
    public PokemonAPI() {
        loggedIn = false;
        fetchingResponse = false;
    }

    public boolean isFetchingResponse() {
        return fetchingResponse;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public PlayerProfile getPlayerProfile() {
        return playerProfile;
    }

    public void login(String username, String password) {
        if (!fetchingResponse) {
            fetchingResponse = true;
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
                    fetchingResponse = false;
                    String jsonResponse = httpResponse.getResultAsString();
                    Gdx.app.log("MSG", jsonResponse);
                    JsonObject loggedInResponse = new JsonParser().parse(jsonResponse).getAsJsonObject();
                    int responseCode = loggedInResponse.get("success").getAsInt();
                    if (responseCode == 1) {
                        loggedIn = true;
                        String username = loggedInResponse.get("username").getAsString();
                        int startedGame = loggedInResponse.get("startedOverworld").getAsInt();
                        int uid = loggedInResponse.get("uid").getAsInt();
                        if (startedGame == 0) {
                            //Create blank player
                            playerProfile = new PlayerProfile(uid, username, startedGame);
                        } else {
                            int money = loggedInResponse.get("money").getAsInt();
                            String gender = loggedInResponse.get("gender").getAsString();
                            String skinTone = loggedInResponse.get("skin-tone").getAsString();
                            String hairStyle = loggedInResponse.get("hair-style").getAsString();
                            String hairColour = loggedInResponse.get("hair-colour").getAsString();
                            playerProfile = new PlayerProfile(uid, username, startedGame, money, gender, skinTone, hairStyle, hairColour);
                        }
                    }

                }

                @Override
                public void failed(Throwable t) {
                    Gdx.app.log("Failed", t.getMessage());
                    fetchingResponse = false;
                }

                @Override
                public void cancelled() {
                    Gdx.app.log("Cancelled", "cancelled");
                    fetchingResponse = false;
                }
            });
        }

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
