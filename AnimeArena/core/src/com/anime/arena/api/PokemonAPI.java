package com.anime.arena.api;

import com.anime.arena.dto.PlayerProfile;
import com.anime.arena.dto.UsernamePasswordDTO;
import com.anime.arena.pokemon.BasePokemon;
import com.anime.arena.pokemon.BasePokemonFactory;
import com.anime.arena.tools.ScriptParameters;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.Json;
import com.google.gson.*;

import java.util.HashMap;
import java.util.Map;

public class PokemonAPI {
    private boolean loggedIn;
    private boolean createdCharacter;
    private boolean fetchingResponse;
    private JsonArray pokedexResponse;
    private String errorMessage;


    private PlayerProfile playerProfile;
    public PokemonAPI() {
        loggedIn = false;
        createdCharacter = false;
        fetchingResponse = false;
        errorMessage = "";
    }

    public void createCharacter(PlayerProfile newPlayerProfile) {
        if (!fetchingResponse && !ScriptParameters.DISABLE_CREATE_CHARACTER) {
            fetchingResponse = true;
            Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
            request.setUrl("http://kelsiegr.com/pokemononline/createCharacter.php");
            Gson g = new Gson();
            String gson = g.toJson(newPlayerProfile);
            Gdx.app.log("PokemonAPI::createCharacter", gson);
            request.setContent(gson);
            Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

                @Override
                public void handleHttpResponse(Net.HttpResponse httpResponse) {
                    fetchingResponse = false;
                    String jsonResponse = httpResponse.getResultAsString();
                    Gdx.app.log("PokemonAPI::createCharacter:handleHttpResponse", jsonResponse);

                    JsonObject loggedInResponse = new JsonParser().parse(jsonResponse).getAsJsonObject();
                    int responseCode = loggedInResponse.get("success").getAsInt();

                    if (responseCode == 1) {
                       createdCharacter = true;
                    } else {
                        Gdx.app.log("PokemonAPI::createCharacter:handleHttpResponse", "Failed to create character");
                        errorMessage = loggedInResponse.get("message").getAsString();
                    }
                }

                @Override
                public void failed(Throwable t) {
                    Gdx.app.log("PokemonAPI::createCharacter:failed", t.getMessage());
                    fetchingResponse = false;
                }

                @Override
                public void cancelled() {
                    Gdx.app.log("PokemonAPI::createCharacter:cancelled", "cancelled");
                    fetchingResponse = false;
                }
            });
        }

    }


    public boolean isFetchingResponse() {
        return fetchingResponse;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean hasError() {
        return !errorMessage.equals("");
    }

    public String getErrorMessage() {
        String msg = errorMessage;
        errorMessage = "";
        return msg;
    }

    public boolean hasCreatedCharacter() {
        return createdCharacter;
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
                            String mapName = loggedInResponse.get("map_name").getAsString();
                            int topID = loggedInResponse.get("upper").getAsInt();
                            int bottomID = loggedInResponse.get("lower").getAsInt();
                            int xPosition = loggedInResponse.get("x_position").getAsInt();
                            int yPosition = loggedInResponse.get("y_position").getAsInt();
                            playerProfile = new PlayerProfile(uid, username, startedGame, money, gender, skinTone, hairStyle, hairColour, mapName, topID, bottomID, xPosition, yPosition);
                        }
                    } else if (responseCode == 0) {
                        errorMessage = loggedInResponse.get("message").getAsString();
                    }

                }

                @Override
                public void failed(Throwable t) {
                    Gdx.app.log("PokemonAPI::login:failed", t.getMessage());
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
