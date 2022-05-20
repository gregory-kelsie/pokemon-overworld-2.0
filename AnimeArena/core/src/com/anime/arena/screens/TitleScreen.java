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

public class TitleScreen implements Screen {

    private AnimeArena game;
    private Texture black;
    private Texture background;
    private Texture cloudTexture;
    private Texture pokemonLogo;
    private Texture onlineVersionLogo;
    private Texture usernamePasswordTexture;
    private Texture inputBox;
    private Texture selectedInputBox;
    private Texture keyboardTexture;
    private Texture keyboardIndicator;

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
    private int textBoxPosition;

    private String[][] abcLayout = {{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"},
            {"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"},
            {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m"},
            {"n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"},
            {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", ",", " "}};

    private int keyboardPosX;
    private int keyboardPosY;

    private String keyboardInput;

    private String username;
    private String password;

    public static final int TITLE_SCREEN = 0;
    public static final int KEYBOARD = 1;
    public static final int USERNAME = 0;
    public static final int PASSWORD = 1;


    public TitleScreen(AnimeArena game) {
        this.game = game;



        this.black = new Texture("animation/black.png");
        this.background = new Texture("title/background.png");
        this.cloudTexture = new Texture("title/clouds.png");
        this.pokemonLogo = new Texture("title/pokemon-logo.png");
        this.onlineVersionLogo = new Texture("title/online-version.png");
        this.usernamePasswordTexture = new Texture("title/username-password.png");
        this.inputBox = new Texture("title/username-input.png");
        this.selectedInputBox = new Texture("title/selected-username-input.png");
        this.keyboardTexture = new Texture("title/keyboard.png");
        this.keyboardIndicator = new Texture("title/indicator.png");

        this.selectPosition = TITLE_SCREEN;
        this.textBoxPosition = USERNAME;

        this.username = "";
        this.password = "";

        this.keyboardInput = "";
        resetKeyboardPos();

        initFont();

        initCamera();

        initVariables();

        gameCam.position.set((AnimeArena.V_WIDTH / 2) / AnimeArena.PPM, (AnimeArena.V_HEIGHT / 2) / AnimeArena.PPM, 0);

    }

    private void resetKeyboardPos() {
        this.keyboardPosX = 0;
        this.keyboardPosY = 0;
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
        parameter3.color = Color.BLACK;
        parameter3.spaceY = 20;
        parameter3.spaceX = -2;

        parameter3.shadowColor = Color.GRAY;
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
            if (selectPosition == TITLE_SCREEN) {
                textBoxPosition = (textBoxPosition == PASSWORD) ? USERNAME : textBoxPosition;
            } else if (selectPosition == KEYBOARD) {
                if (keyboardPosY > 0) {
                    keyboardPosY--;
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (selectPosition == TITLE_SCREEN) {
                textBoxPosition = (textBoxPosition == USERNAME) ? PASSWORD : textBoxPosition;
            }else if (selectPosition == KEYBOARD) {
                if (keyboardPosY < 4) {
                    keyboardPosY++;
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (selectPosition == KEYBOARD) {
                if (keyboardPosX < 12) {
                    keyboardPosX++;
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (selectPosition == KEYBOARD) {
                if (keyboardPosX > 0) {
                    keyboardPosX--;
                }
            }
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.X))) {
            if (selectPosition == KEYBOARD) {
                selectPosition = TITLE_SCREEN;
            }
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.Z))) {
            if (selectPosition == TITLE_SCREEN) {
                selectPosition = KEYBOARD;
                if (textBoxPosition == USERNAME) {
                    keyboardInput = username;
                } else {
                    keyboardInput = password;
                }
            } else if (selectPosition == KEYBOARD) {
                keyboardInput += abcLayout[keyboardPosY][keyboardPosX];
            }
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
        game.getBatch().draw(background, 0, 960, 1080, 960);
        game.getBatch().draw(cloudTexture, 0, 960, 1080, 960);
        game.getBatch().draw(pokemonLogo, 154, 1530, 772, 296);
        game.getBatch().draw(onlineVersionLogo, 299, 1470,482, 56);
        game.getBatch().draw(usernamePasswordTexture, 160, 1160, 228, 98);
        if (textBoxPosition == USERNAME) {
            game.getBatch().draw(selectedInputBox, 420, 1215, 514, 48);
            game.getBatch().draw(inputBox, 420, 1160, 514, 48);
        } else {
            game.getBatch().draw(inputBox, 420, 1215, 514, 48);
            game.getBatch().draw(selectedInputBox, 420, 1160, 514, 48);
        }

        if (selectPosition == KEYBOARD) {
            game.getBatch().draw(keyboardTexture, 28, 1056, 1024, 768);
            menuFont.draw(game.getBatch(), keyboardInput, 180, 1718);
            int xPos = 163 + (keyboardPosX * 59);
            int yPos = 1400 - (keyboardPosY * 70);
            game.getBatch().draw(keyboardIndicator, xPos, yPos, 64, 64);
        }

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

