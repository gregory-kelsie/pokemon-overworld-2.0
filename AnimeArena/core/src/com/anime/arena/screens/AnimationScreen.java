package com.anime.arena.screens;

import com.anime.arena.AnimeArena;
import com.anime.arena.animation.BattleAnimationBackground;
import com.anime.arena.animation.TrainerBattleAnimation;
import com.anime.arena.dto.UsernamePasswordDTO;
import com.anime.arena.pokemon.BasePokemon;
import com.anime.arena.pokemon.BasePokemonFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationScreen implements Screen {

    private AnimeArena game;
    private Texture texture;
    private Texture black;
    private Texture white;


    private BattleAnimationBackground battleAnimationBackground;
    private BattleAnimationBackground battleAnimationBackground2;

    private Texture player;
    private Texture opponent;

    private Texture vs;
    private int vsX;
    private int vsY;
    private float vsWidth;
    private float vsHeight;
    private float elapsedVs;


    private OrthographicCamera gameCam;
    private OrthographicCamera controlsCam;
    private Viewport gamePort;

    private String vertexShader;
    private String fragmentShader;
    private String whiteFragmentShader;
    private ShaderProgram shaderProgram;
    private ShaderProgram shaderProgram2;
    private float uniformTime;
    private boolean showTrainer;

    private TrainerBattleAnimation trainerAnimation;

    private BasePokemonFactory basePokemonFactory;

    public AnimationScreen(AnimeArena game) {
        this.game = game;
        texture = new Texture("badlogic.jpg");
        black = new Texture("animation/black.png");
        white = new Texture("animation/white.png");
        trainerAnimation = new TrainerBattleAnimation();
        attemptLogin();
        getPokedex();
//        white = new Texture("animation/white.png");
//        player = new Texture("animation/faces/player.png");
//        opponent = new Texture("animation/faces/falkner.png");
//        vs = new Texture("animation/faces/vs.png");
//        battleAnimationBackground = new BattleAnimationBackground("animation/orangebg.png");
//        battleAnimationBackground2 = new BattleAnimationBackground("animation/bluebg.png", 540, 1000);
        initCamera();
//        vsX = 400;
//        vsY = 840;
//        elapsedVs = 0;
//        vsWidth = 70 * 4.21875f;
//        vsHeight = 70 * 4.21875f;
//        vertexShader = Gdx.files.internal("shaders/default.vs").readString();
//        fragmentShader = Gdx.files.internal("shaders/black.fs").readString();
//        whiteFragmentShader = Gdx.files.internal("shaders/white.fs").readString();
//        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
//        shaderProgram2 = new ShaderProgram(vertexShader, whiteFragmentShader);
//        uniformTime = 0;
//        showTrainer = false;
//        if (!shaderProgram.isCompiled()) {
//            Gdx.app.log("Error", "Shader Program failed to compile: " + shaderProgram.getLog());
//        }
        gameCam.position.set((AnimeArena.V_WIDTH / 2) / AnimeArena.PPM, (AnimeArena.V_HEIGHT / 2) / AnimeArena.PPM, 0);



    }

    private void attemptLogin() {
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl("http://kelsiegr.com/pokemononline/jsonlogin.php");

        UsernamePasswordDTO up = new UsernamePasswordDTO("mileycyrus", "wreckingball");
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
                    pokemonList.put(jo.get("dex").getAsInt(), jo);
                }
                //(add back when formatting is fixed from PokemonTestScreen) basePokemonFactory = new BasePokemonFactory(pokemonArray.toString(), pokemonList);
                Gdx.app.log("JSONRESP", pokemonArray.size() + "");
                BasePokemon p = basePokemonFactory.createBasePokemon(6);
                int x = 0;
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




    private void initCamera() {
        gameCam = new OrthographicCamera();
        controlsCam = new OrthographicCamera();
        controlsCam.setToOrtho(false, 1080, 1920);
        gamePort = new FitViewport(AnimeArena.V_WIDTH / AnimeArena.PPM, AnimeArena.V_HEIGHT / AnimeArena.PPM, gameCam);
        gamePort.apply();
    }



    @Override
    public void show() {

    }

    public void update(float dt) {
        handleInput(dt);
        gameCam.update();
        trainerAnimation.update(dt);
//        uniformTime -= dt;
//        if (uniformTime <= 0) {
//            uniformTime = 0;
//        }
//        battleAnimationBackground.update(dt);
//        battleAnimationBackground2.update(dt);
//        elapsedVs += dt;
//        if (elapsedVs >= 0.05) {
//            if (vsX == 400) {
//                vsX = 395;
//                vsY = 835;
//            } else {
//                vsX = 400;
//                vsY = 840;
//            }
//            elapsedVs = 0;
//        }
//        if (false) { //Increase Size
//            vsHeight += dt * 800;
//            vsWidth += dt * 800;
//            vsX -= dt * 200;
//            vsY -= dt * 200;
//        }
    }

    private void handleInput(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                uniformTime = 1;
                showTrainer = true;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
                showTrainer = false;
            }
        } else {
        }

    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Game Screen
        Gdx.gl.glViewport( 0,Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);


        //Controls Screen
        Gdx.gl.glViewport( 0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        game.getBatch().setProjectionMatrix(controlsCam.combined);
        game.getBatch().begin();



        game.getBatch().draw(texture, 0, 0);

        game.getBatch().draw(black,0,960);

//        battleAnimationBackground.render(delta, game.getBatch());
//        battleAnimationBackground2.render(delta, game.getBatch());
//        game.getBatch().draw(player, 0, 1000, 128 * 4.21875f, 64 * 4.21875f);
//        if (shaderProgram.isCompiled()) {
//            if (!showTrainer) {
//                game.getBatch().setShader(shaderProgram);
//            }
//        }
//        //shaderProgram.setUniformf("u_time", uniformTime);
//        game.getBatch().draw(opponent, 540, 1000, 128 * 4.21875f, 64 * 4.21875f);
//        game.getBatch().setShader(null);
//        game.getBatch().draw(vs, vsX, vsY, vsWidth, vsHeight);
//
//        game.getBatch().setShader(shaderProgram2);
//        shaderProgram2.setUniformf("u_time", uniformTime);
//        game.getBatch().draw(white,0,960);
        trainerAnimation.render(delta, game.getBatch());
        //game.getBatch().setShader(null);
        game.getBatch().end();

    }


    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        texture.dispose();

    }
}

