package com.anime.arena.screens;

import com.anime.arena.AnimeArena;
import com.anime.arena.pokemon.BasePokemon;
import com.anime.arena.pokemon.BasePokemonFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.*;

public class ScreenTemplate implements Screen {

    private AnimeArena game;
    private Texture black;
    private OrthographicCamera gameCam;
    private OrthographicCamera controlsCam;
    private Viewport gamePort;

    private Screen previousScreen;
    private BasePokemonFactory pokemonFactory;


    private BitmapFont menuFont;
    private BitmapFont listFont;
    private BitmapFont nameFont;

    private int screenPosition; //0 menu, 1 list, 2 entry, 3 sprite, 4 nest

    private int selectPosition;


    private List<BasePokemon> pokemonList;



    private TextureAtlas pokemonAtlas;
    private TextureAtlas pokemonTypeAtlas;

    public static boolean ENABLE_POKEDEX = true;
    public ScreenTemplate(AnimeArena game, Screen previousScreen, TextureAtlas pokemonAtlas, TextureAtlas pokemonTypeAtlas, BasePokemonFactory pokemonFactory) {
        this.game = game;
        this.pokemonAtlas = pokemonAtlas;
        this.pokemonTypeAtlas = pokemonTypeAtlas;
        this.previousScreen = previousScreen;
        this.pokemonFactory = pokemonFactory;
        this.pokemonList = new ArrayList<BasePokemon>();


        this.black = new Texture("animation/black.png");

        //Pokemon Screens




        initFont();

        initCamera();

        initVariables();

        gameCam.position.set((AnimeArena.V_WIDTH / 2) / AnimeArena.PPM, (AnimeArena.V_HEIGHT / 2) / AnimeArena.PPM, 0);

    }



    private void initVariables() {
        screenPosition = 0;
        selectPosition = 0;

    }

    private void initFont() {
        //List Font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pkmnems.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter2.size = 60;
        parameter2.color = Color.DARK_GRAY;
        parameter2.spaceY = 20;
        parameter2.spaceX = -2;

        parameter2.shadowColor = Color.GRAY;
        parameter2.shadowOffsetX = 1;
        parameter2.shadowOffsetY = 1;

        FreeTypeFontGenerator.FreeTypeFontParameter parameter3 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter3.size = 60;
        parameter3.color = Color.WHITE;
        parameter3.spaceY = 20;
        parameter3.spaceX = -2;

        parameter3.shadowColor = Color.BLACK;
        parameter3.shadowOffsetX = 4;
        parameter3.shadowOffsetY = 4;


        listFont = generator.generateFont(parameter2);
        menuFont = generator.generateFont(parameter3);
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

    }



    private void handleInput(float dt) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {

        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {

        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {

        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {

        } else if (Gdx.input.isKeyJustPressed((Input.Keys.X))) {

        } else if (Gdx.input.isKeyJustPressed((Input.Keys.Z))) {

        }

    }



    @Override
    public void render(float dt) {
        update(dt);
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Game Screen
        Gdx.gl.glViewport( 0,Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);


        //Controls Screen
        Gdx.gl.glViewport( 0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        game.getBatch().setProjectionMatrix(controlsCam.combined);
        game.getBatch().begin();

        game.getBatch().draw(black,0,960);
        drawScreen(game.getBatch());

        game.getBatch().end();
    }

    private void drawScreen(SpriteBatch batch) {
        if (screenPosition == 0) {

        } else if (screenPosition == 1) {

        } else if (screenPosition == 2) {

        }
    }




    @Override
    public void resize(int i, int i1) {

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
        black.dispose();
    }
}

