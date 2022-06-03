package com.anime.arena.screens;

import com.anime.arena.AnimeArena;
import com.anime.arena.api.PokemonAPI;
import com.anime.arena.dto.PlayerProfile;
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
    private Texture abcOffTexture;
    private Texture numbersOffTexture;
    private Texture numbersOnTexture;
    private Texture backOffTexture;
    private Texture okOffTexture;

    private Texture backOnTexture;
    private Texture okOnTexture;

    private Texture selectedButton;
    private Texture button;
    private Texture loginTexture;
    private Texture exitTexture;

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
    private int keyboardPosition;

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

    private boolean loggingIn;

    public static final int TITLE_SCREEN = 0;
    public static final int KEYBOARD = 1;
    public static final int USERNAME = 0;
    public static final int PASSWORD = 1;
    public static final int LOGIN_BUTTON = 2;
    public static final int EXIT_BUTTON = 3;

    public static final int ON_KEYBOARD = 0;
    public static final int ON_BACK = 1;
    public static final int ON_OK = 2;

    private PokemonAPI pokemonAPI;

    public TitleScreen(AnimeArena game) {
        this.game = game;

        this.loggingIn = false;

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
        this.abcOffTexture = new Texture("title/abc-off.png");
        this.numbersOnTexture = new Texture("title/number-on.png");
        this.backOffTexture = new Texture("title/back-off.png");
        this.okOffTexture = new Texture("title/ok-off.png");
        this.backOnTexture = new Texture("title/back-on.png");
        this.okOnTexture = new Texture("title/ok-on.png");

        this.selectedButton = new Texture("title/selected-button.png");
        this.button = new Texture("title/button.png");
        this.loginTexture = new Texture("title/login.png");
        this.exitTexture = new Texture("title/exit.png");

        this.selectPosition = TITLE_SCREEN;
        this.textBoxPosition = USERNAME;

        this.username = "";
        this.password = "";

        this.keyboardInput = "";
        resetKeyboardPos();

        initFont();

        initCamera();

        initVariables();
        this.pokemonAPI = new PokemonAPI();
        gameCam.position.set((AnimeArena.V_WIDTH / 2) / AnimeArena.PPM, (AnimeArena.V_HEIGHT / 2) / AnimeArena.PPM, 0);

    }

    private void resetKeyboardPos() {
        this.keyboardPosX = 0;
        this.keyboardPosY = 0;
        this.keyboardPosition = ON_KEYBOARD;
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
        if (loggingIn) {
            if (!pokemonAPI.isFetchingResponse() && pokemonAPI.isLoggedIn()) {
                loggingIn = false;
                PlayerProfile p = pokemonAPI.getPlayerProfile();
                if (p != null) {
                    if (p.getStartedGame() == 0 || CharacterCreateScreen.DEBUG_CHARACTER_CREATE_SCREEN) {
                        //Go to create character screen
                        Gdx.app.log("update", "The player " + p.getUsername() + " will now create a character");
                        game.setScreen(new CharacterCreateScreen(game, p));
                    } else if (p.getStartedGame() == 1) {
                        //Go to the player's location in the game screen
                        Gdx.app.log("update", "The player " + p.getUsername() + " is now logging in");
                    } else {
                        Gdx.app.log("update", "The player profile startedGame flag is invalid: " + p.getStartedGame());
                    }
                }
            } else if (!pokemonAPI.isFetchingResponse()) {
                //loggingIn = false;
            }
        } else {
            handleInput(dt);
        }
    }



    private void handleInput(float dt) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (selectPosition == TITLE_SCREEN) {
                if (textBoxPosition == PASSWORD) {
                    textBoxPosition = USERNAME;
                } else if (textBoxPosition == LOGIN_BUTTON || textBoxPosition == EXIT_BUTTON) {
                    textBoxPosition = PASSWORD;
                }
            } else if (selectPosition == KEYBOARD) {
                if (keyboardPosY > 0) {
                    keyboardPosY--;
                } else if (keyboardPosY == 0 && keyboardPosition == ON_KEYBOARD && keyboardPosX >= 8 && keyboardPosX <= 10) {
                    keyboardPosition = ON_BACK;
                } else if (keyboardPosY == 0 && keyboardPosition == ON_KEYBOARD && keyboardPosX >= 11 && keyboardPosX <= 12) {
                    keyboardPosition = ON_OK;
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (selectPosition == TITLE_SCREEN) {
                if (textBoxPosition == USERNAME) {
                    textBoxPosition = PASSWORD;
                } else if (textBoxPosition == PASSWORD) {
                    textBoxPosition = LOGIN_BUTTON;
                }
            } else if (selectPosition == KEYBOARD) {
                if (keyboardPosition == ON_KEYBOARD && keyboardPosY < 4) {
                    keyboardPosY++;
                } else if (keyboardPosition == ON_BACK || keyboardPosition == ON_OK) {
                    keyboardPosX = keyboardPosition == ON_BACK ? 9 : 11;
                    keyboardPosition = ON_KEYBOARD;
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (selectPosition == KEYBOARD) {
                if (keyboardPosition == ON_KEYBOARD && keyboardPosX < 12) {
                    keyboardPosX++;
                } else if (keyboardPosition == ON_BACK) {
                    keyboardPosX = 11;
                    keyboardPosition = ON_OK;
                }
            } else if (selectPosition == TITLE_SCREEN) {
                if (textBoxPosition == LOGIN_BUTTON) {
                    textBoxPosition = EXIT_BUTTON;
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (selectPosition == KEYBOARD) {
                if (keyboardPosition == ON_KEYBOARD && keyboardPosX > 0) {
                    keyboardPosX--;
                } else if (keyboardPosition == ON_OK) {
                    keyboardPosition = ON_BACK;
                    keyboardPosX = 9;
                }
            } else if (selectPosition == TITLE_SCREEN) {
                if (textBoxPosition == EXIT_BUTTON) {
                    textBoxPosition = LOGIN_BUTTON;
                }
            }
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.X))) {
            if (selectPosition == KEYBOARD) {
                selectPosition = TITLE_SCREEN;
                resetKeyboardPos();

            }
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.Z))) {
            if (selectPosition == TITLE_SCREEN) {
                if (textBoxPosition == USERNAME) {
                    selectPosition = KEYBOARD;
                    keyboardInput = username;
                } else if (textBoxPosition == PASSWORD) {
                    selectPosition = KEYBOARD;
                    keyboardInput = password;
                } else if (textBoxPosition == LOGIN_BUTTON) {
                    if (username.length() > 0 && password.length() > 0) {
                        pokemonAPI.login(username, password);
                        loggingIn = true;
                    } else {
                        Gdx.app.log("handleInput", "Cannot log in with a blank username or password" + Gdx.app.getLogLevel());
                    }
                }
            } else if (selectPosition == KEYBOARD) {
                if (keyboardPosition == ON_KEYBOARD && keyboardInput.length() <= 15) {
                    keyboardInput += abcLayout[keyboardPosY][keyboardPosX];
                } else if (keyboardPosition == ON_BACK) {
                    if (keyboardInput.length() > 0) {
                        keyboardInput = keyboardInput.substring(0, keyboardInput.length() - 1);
                    }
                } else if (keyboardPosition == ON_OK) {
                    if (textBoxPosition == USERNAME) {
                        username = keyboardInput;
                    } else if (textBoxPosition == PASSWORD) {
                        password = keyboardInput;
                    }
                    selectPosition = TITLE_SCREEN;
                    resetKeyboardPos();
                }
            }
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.BACKSPACE))) {
            if (selectPosition == KEYBOARD) {
                if (keyboardInput.length() > 0) {
                    keyboardInput = keyboardInput.substring(0, keyboardInput.length() - 1);
                }
            }
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.ENTER))) {
            if (textBoxPosition == USERNAME) {
                username = keyboardInput;
            } else if (textBoxPosition == PASSWORD) {
                password = keyboardInput;
            }
            selectPosition = TITLE_SCREEN;
            resetKeyboardPos();
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
        } else {
            game.getBatch().draw(inputBox, 420, 1215, 514, 48);

        }
        if (textBoxPosition == PASSWORD) {
            game.getBatch().draw(selectedInputBox, 420, 1160, 514, 48);
        } else {
            game.getBatch().draw(inputBox, 420, 1160, 514, 48);
        }
        if (textBoxPosition == LOGIN_BUTTON) {
            game.getBatch().draw(selectedButton, 217, 1013, 320, 124);
        } else {
            game.getBatch().draw(button, 234, 1030, 286, 90);
        }
        if (textBoxPosition == EXIT_BUTTON) {
            game.getBatch().draw(selectedButton, 543, 1013, 320, 124);
        } else {
            game.getBatch().draw(button, 560, 1030, 286, 90);
        }



        game.getBatch().draw(loginTexture, 320, 1060, 100, 32);
        game.getBatch().draw(exitTexture, 660, 1060, 80, 32);

        menuFont.draw(game.getBatch(), username, 430, 1252);
        menuFont.draw(game.getBatch(), getMaskedInput(password), 430, 1195);



        if (selectPosition == KEYBOARD) {
            game.getBatch().draw(keyboardTexture, 28, 1056, 1024, 768);
            Texture backTexture;
            if (keyboardPosition == ON_BACK) {
                backTexture = backOnTexture;
            } else {
                backTexture = backOffTexture;
            }
            Texture okTexture;
            if (keyboardPosition == ON_OK) {
                okTexture = okOnTexture;
            } else {
                okTexture = okOffTexture;
            }
            game.getBatch().draw(backTexture, 648, 1493, 131, 79);
            game.getBatch().draw(okTexture, 794, 1493, 131, 79);
            if (textBoxPosition == USERNAME) {
                menuFont.draw(game.getBatch(), keyboardInput, 180, 1718);
            } else if (textBoxPosition == PASSWORD) {
                menuFont.draw(game.getBatch(), getMaskedInput(keyboardInput), 180, 1718);
            }
            int xPos = 163 + (keyboardPosX * 59);
            int yPos = 1400 - (keyboardPosY * 70);
            if (keyboardPosition  == ON_KEYBOARD) {
                game.getBatch().draw(keyboardIndicator, xPos, yPos, 64, 64);
            }
        }

        drawScreen(game.getBatch());

        game.getBatch().end();
    }

    private String getMaskedInput(String str) {
        return str.replaceAll(".", "*");
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

