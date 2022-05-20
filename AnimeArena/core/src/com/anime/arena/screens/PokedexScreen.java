package com.anime.arena.screens;

import com.anime.arena.AnimeArena;
import com.anime.arena.items.Bag;
import com.anime.arena.items.BagItem;
import com.anime.arena.items.Item;
import com.anime.arena.items.ItemFactory;
import com.anime.arena.pokedex.Pokedex;
import com.anime.arena.pokemon.BasePokemon;
import com.anime.arena.pokemon.BasePokemonFactory;
import com.anime.arena.pokemon.PokemonType;
import com.anime.arena.tools.DatabaseLoader;
import com.anime.arena.tools.TextFormater;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;

public class PokedexScreen implements Screen {

    private AnimeArena game;
    private Texture black;
    private OrthographicCamera gameCam;
    private OrthographicCamera controlsCam;
    private Viewport gamePort;

    private Screen previousScreen;
    private BasePokemonFactory pokemonFactory;

    private Sprite pokemonSprite;
    private Sprite backSprite;
    private Sprite smallSprite;
    private Sprite typeOne;
    private Sprite typeTwo;

    private Texture unknownPokemon;
    private Texture ownedIcon;
    private Texture seenIcon;
    private Texture arrow;

    private Texture selectTexture;

    private Texture menuScreen;
    private Texture listScreen;
    private Texture entryScreen;
    private Texture spritesScreen;
    private Texture nestScreen;

    private BitmapFont menuFont;
    private BitmapFont listFont;
    private BitmapFont nameFont;

    private int screenPosition; //0 menu, 1 list, 2 entry, 3 sprite, 4 nest
    private int menuPosition; //0 is for the National Dex option, 1 is exit
    private int listOffset;
    private int selectPosition;

    private int seenAmount;
    private int obtainedAmount;

    private List<BasePokemon> pokemonList;



    private TextureAtlas pokemonAtlas;
    private TextureAtlas pokemonTypeAtlas;
    private Pokedex pokedex;

    public static boolean ENABLE_POKEDEX = false;
    public PokedexScreen(AnimeArena game, Screen previousScreen, TextureAtlas pokemonAtlas, TextureAtlas pokemonTypeAtlas, Pokedex pokedex, DatabaseLoader dbLoader) {
        this.game = game;
        this.pokemonAtlas = pokemonAtlas;
        this.pokemonTypeAtlas = pokemonTypeAtlas;
        this.previousScreen = previousScreen;
        this.pokemonFactory = dbLoader.getBasePokemonFactory();
        this.pokedex = pokedex;
        this.pokemonList = new ArrayList<BasePokemon>();
        pokemonList.add(pokemonFactory.createBasePokemon(1));
        pokemonList.add(pokemonFactory.createBasePokemon(2));
        pokemonList.add(pokemonFactory.createBasePokemon(3));
        pokemonList.add(pokemonFactory.createBasePokemon(4));
        pokemonList.add(pokemonFactory.createBasePokemon(5));
        pokemonList.add(pokemonFactory.createBasePokemon(6));
        pokemonList.add(pokemonFactory.createBasePokemon(7));
        pokemonList.add(pokemonFactory.createBasePokemon(8));
        pokemonList.add(pokemonFactory.createBasePokemon(9));

        this.black = new Texture("animation/black.png");

        //Pokedex Screens
        this.menuScreen = new Texture("pokedex/pokedexmenubg.png");
        this.listScreen = new Texture("pokedex/pokedexbg.png");
        this.entryScreen = new Texture("pokedex/pokedexEntry.PNG");
        this.spritesScreen = new Texture("pokedex/pokedexForm.png");
        this.nestScreen = new Texture("pokedex/pokedexNest.png");

        //Pokedex Icons
        this.ownedIcon = new Texture("pokedex/pokedexOwned.png");
        this.seenIcon = new Texture("pokedex/pokedexSeen.png");
        this.arrow = new Texture("pokedex/selarrowwhite.png");
        this.unknownPokemon = new Texture("pokedex/000.png");

        this.selectTexture = new Texture("pokedex/pokedexSel.png");

        initFont();

        initCamera();

        initVariables();
        List<Integer> seenObtainedAmount = pokedex.getPokedexAmounts();
        this.seenAmount = seenObtainedAmount.get(0);
        this.obtainedAmount = seenObtainedAmount.get(1);

        updatePokemonSprite();
        gameCam.position.set((AnimeArena.V_WIDTH / 2) / AnimeArena.PPM, (AnimeArena.V_HEIGHT / 2) / AnimeArena.PPM, 0);

    }

    private void updatePokemonSprite() {
        pokemonSprite = new Sprite(pokemonAtlas.findRegion(pokemonList.get(selectPosition).getFormattedImage()));
        pokemonSprite.setSize(396, 396);
        if (screenPosition == 2) {
            pokemonSprite.setPosition(25, 1400);
        } else {
            pokemonSprite.setPosition(50, 1330);
        }
        pokemonSprite.flip(true, false);

        typeOne = new Sprite(pokemonTypeAtlas.findRegion(getFirstType()));

        typeOne.setSize(typeOne.getWidth() * 2, typeOne.getHeight() * 2);
        if (pokemonList.get(selectPosition).getSecondType() == PokemonType.NONE) {
            typeTwo = null;
            typeOne.setPosition(775, 1560);
        } else {
            typeOne.setPosition(710, 1560);
            typeTwo = new Sprite(pokemonTypeAtlas.findRegion(getSecondType()));
            typeTwo.setPosition(850, 1560);
            typeTwo.setSize(typeTwo.getWidth() * 2, typeTwo.getHeight() * 2);
        }
    }

    private void initVariables() {
        menuPosition = 0;
        screenPosition = 0;
        listOffset = 0;
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

    private void shiftListUp() {
        pokemonList.set(0, pokemonList.get(1));
        pokemonList.set(1, pokemonList.get(2));
        pokemonList.set(2, pokemonList.get(3));
        pokemonList.set(3, pokemonList.get(4));
        pokemonList.set(4, pokemonList.get(5));
        pokemonList.set(5, pokemonList.get(6));
        pokemonList.set(6, pokemonList.get(7));
        pokemonList.set(7, pokemonList.get(8));
        pokemonList.set(8, pokemonFactory.createBasePokemon(9 + listOffset));
    }

    private void shiftListDown() {
        pokemonList.set(8, pokemonList.get(7));
        pokemonList.set(7, pokemonList.get(6));
        pokemonList.set(6, pokemonList.get(5));
        pokemonList.set(5, pokemonList.get(4));
        pokemonList.set(4, pokemonList.get(3));
        pokemonList.set(3, pokemonList.get(2));
        pokemonList.set(2, pokemonList.get(1));
        pokemonList.set(1, pokemonList.get(0));
        pokemonList.set(0, pokemonFactory.createBasePokemon(1 + listOffset));

    }

    private void updatePokemonList() {
        int startingNum = listOffset + 1;
        pokemonList.set(0, pokemonFactory.createBasePokemon(startingNum));
        pokemonList.set(1, pokemonFactory.createBasePokemon(startingNum + 1));
        pokemonList.set(2, pokemonFactory.createBasePokemon(startingNum + 2));
        pokemonList.set(3, pokemonFactory.createBasePokemon(startingNum + 3));
        pokemonList.set(4, pokemonFactory.createBasePokemon(startingNum + 4));
        pokemonList.set(5, pokemonFactory.createBasePokemon(startingNum + 5));
        pokemonList.set(6, pokemonFactory.createBasePokemon(startingNum + 6));
        pokemonList.set(7, pokemonFactory.createBasePokemon(startingNum + 7));
        pokemonList.set(8, pokemonFactory.createBasePokemon(startingNum + 8));
        updatePokemonSprite();
    }

    private void handleInput(float dt) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (screenPosition == 0) {
                menuPosition = 0;
            } else if (screenPosition == 1) {
                if (selectPosition > 0) {
                    selectPosition--;
                    updatePokemonSprite();
                } else if (selectPosition == 0 && listOffset > 0) {
                    listOffset--;
                    shiftListDown();
                    updatePokemonSprite();
                }
            } else if (screenPosition == 2) {
                int nextPokemon = pokedex.getPreviousDexNum(getCurrentDexPosition());
                if (nextPokemon != -1) {
                    if (nextPokemon < 879) {
                        selectPosition = 0;
                        listOffset = nextPokemon - 1;
                        updatePokemonList();
                    } else {
                        selectPosition = nextPokemon - 879;
                        updatePokemonList();
                    }
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (screenPosition == 0) {
                menuPosition = 1;
            } else if (screenPosition == 1) {
                if (selectPosition < 8) {
                    selectPosition++;
                    updatePokemonSprite();
                } else if (selectPosition == 8) {
                    if (pokemonFactory.hasDexNumber(9 + listOffset + 1)) {
                        listOffset++;
                        shiftListUp();
                        updatePokemonSprite();
                    }
                }
            } else if (screenPosition == 2) {
                int nextPokemon = pokedex.getNextDexNum(getCurrentDexPosition());
                if (nextPokemon != -1) {
                    if (nextPokemon < 879) {
                        selectPosition = 0;
                        listOffset = nextPokemon - 1;
                        updatePokemonList();
                    } else {
                        selectPosition = nextPokemon - 879;
                        listOffset = 878;
                        updatePokemonList();
                    }
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (screenPosition == 1) {
                if (getCurrentDexPosition() < 151) {
                    listOffset = 151;
                    selectPosition = 0;
                    updatePokemonList();
                } else if (getCurrentDexPosition() < 251) {
                    listOffset = 251;
                    selectPosition = 0;
                    updatePokemonList();
                } else if (getCurrentDexPosition() < 386) {
                    listOffset = 386;
                    selectPosition = 0;
                    updatePokemonList();
                } else if (getCurrentDexPosition() < 494) {
                    listOffset = 494;
                    selectPosition = 0;
                    updatePokemonList();
                } else if (getCurrentDexPosition() < 649) {
                    listOffset = 649;
                    selectPosition = 0;
                    updatePokemonList();
                } else if (getCurrentDexPosition() < 721) {
                    listOffset = 721;
                    selectPosition = 0;
                    updatePokemonList();
                } else if (getCurrentDexPosition() < 809) {
                    listOffset = 809;
                    selectPosition = 0;
                    updatePokemonList();
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (screenPosition == 1) {
                if (getCurrentDexPosition() <= 152) {
                    listOffset = 0;
                    selectPosition = 0;
                    updatePokemonList();
                } else if (getCurrentDexPosition() <= 252) {
                    listOffset = 151;
                    selectPosition = 0;
                    updatePokemonList();
                } else if (getCurrentDexPosition() <= 387) {
                    listOffset = 251;
                    selectPosition = 0;
                    updatePokemonList();
                } else if (getCurrentDexPosition() <= 495) {
                    listOffset = 386;
                    selectPosition = 0;
                    updatePokemonList();
                } else if (getCurrentDexPosition() <= 650) {
                    listOffset = 494;
                    selectPosition = 0;
                    updatePokemonList();
                } else if (getCurrentDexPosition() <= 722) {
                    listOffset = 649;
                    selectPosition = 0;
                    updatePokemonList();
                } else if (getCurrentDexPosition() <= 810) {
                    listOffset = 721;
                    selectPosition = 0;
                    updatePokemonList();
                } else if (getCurrentDexPosition() > 810) {
                    listOffset = 809;
                    selectPosition = 0;
                    updatePokemonList();
                }
            }
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.X))) {
            if (screenPosition == 0) {
                closePokedex();
            } else if (screenPosition == 1) {
                screenPosition = 0;
            } else if (screenPosition == 2) {
                screenPosition = 1;
                pokemonSprite.setPosition(50, 1330);
            }
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.Z))) {
            if (screenPosition == 0 && menuPosition == 0) {
                screenPosition = 1;
            } else if (screenPosition == 0 && menuPosition == 1) {
                closePokedex();
            } else if (screenPosition == 1) {
                if (displayPokemon(getCurrentDexPosition())) {
                    screenPosition = 2;
                    pokemonSprite.setPosition(25, 1400);
                }
            }
        }

    }

    private void closePokedex() {
        game.setScreen(previousScreen);
        this.dispose();
    }

    private int getPokemonID() {
        return screenPosition + listOffset;
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
            drawMenuScreen(batch);
        } else if (screenPosition == 1) {
            drawListScreen(batch);
        } else if (screenPosition == 2) {
            drawEntryScreen(batch);
        }
    }

    private void drawMenuScreen(SpriteBatch batch) {
        batch.draw(menuScreen, 0, 960, 1080, 960);
        menuFont.draw(batch, "National Pok\u00e9dex", 150, 1470);
        menuFont.draw(batch, "Exit", 150, 1400);

        menuFont.draw(batch, "SEEN", 630, 1600);


        int seenOffset = 0;
        int obtainedOffset = 0;

        if (seenAmount < 100) {
            seenOffset = 1;
        } else if (seenAmount >= 100) {
            seenOffset = 2;
        }
        if (obtainedAmount < 100) {
            obtainedOffset = 1;
        } else if (obtainedAmount >= 100) {
            obtainedOffset = 2;
        }
        menuFont.draw(batch, Integer.toString(seenAmount), 670 - 15 * seenOffset, 1470);
        menuFont.draw(batch, "OBTAINED", 800, 1600);
        menuFont.draw(batch, Integer.toString(obtainedAmount), 870 - 15 * obtainedOffset, 1470);
        if (menuPosition == 0) {
            batch.draw(arrow, 100, 1428, arrow.getWidth() * 2, arrow.getHeight() * 2);
        } else {
            batch.draw(arrow, 100, 1358, arrow.getWidth() * 2, arrow.getHeight() * 2);
        }


    }

    private void drawEntryScreen(SpriteBatch batch) {
        batch.draw(entryScreen, 0, 960, 1080, 960);
        pokemonSprite.draw(batch);
        BasePokemon selectedPokemon = pokemonList.get(selectPosition);
        menuFont.draw(batch, getDexNumberString(selectedPokemon.getDexNumber()) + " " + selectedPokemon.getName(), 532, 1800);


        listFont.draw(batch, "HT", 670, 1500);

        listFont.draw(batch, "m", 960, 1500);
        listFont.draw(batch, "WT", 670, 1420);

        listFont.draw(batch, "kg", 960, 1420);

        if (ENABLE_POKEDEX || pokedex.hasObtained(selectedPokemon.getDexNumber())) {
            batch.draw(ownedIcon, 435, 1740, ownedIcon.getWidth() * 3, ownedIcon.getHeight() * 3);
            listFont.draw(batch, selectedPokemon.getClassification() + " Pok\u00e9mon", 440, 1715);
            listFont.draw(batch, Double.toString(selectedPokemon.getWeight()), 800, 1420);
            listFont.draw(batch, Double.toString(selectedPokemon.getHeight()), 800, 1500);
            listFont.draw(batch, TextFormater.formatText(selectedPokemon.getDescription()), 10, 1300);
            typeOne.draw(batch);
            if (typeTwo != null) {
                typeTwo.draw(batch);
            }
        } else {
            listFont.draw(batch, "????? Pok\u00e9mon", 440, 1715);
            listFont.draw(batch, "????.?", 800, 1420);
            listFont.draw(batch, "????.?", 800, 1500);
        }
    }

    private String getFirstType() {
        return PokemonType.toString(pokemonList.get(selectPosition).getFirstType()).toLowerCase();
    }

    private String getSecondType() {
        return PokemonType.toString(pokemonList.get(selectPosition).getSecondType()).toLowerCase();
    }

    private int getCurrentDexPosition() {
        return listOffset + selectPosition + 1;
    }

    private void drawListScreen(SpriteBatch batch) {
        batch.draw(listScreen, 0, 960, 1080, 960);

        drawSelector(batch);



        //Render Top Pokemon
        int num = listOffset + 1;

        for (int i = 0; i < 9; i++) {
            if (displayPokemon(i + listOffset + 1)) {
                if (pokedex.hasObtained(i + listOffset + 1)) {
                    batch.draw(ownedIcon, 493, 1737 - (i * 80), ownedIcon.getWidth() * 3, ownedIcon.getHeight() * 3);
                } else {
                    batch.draw(seenIcon, 493, 1737 - (i * 80), seenIcon.getWidth() * 3, ownedIcon.getWidth() * 3);
                }
                listFont.draw(batch, getKnownPokemonString(i), 592, 1795 - (i * 80));
            } else {
                listFont.draw(batch, getUnknownPokemonString(i + listOffset + 1), 592, 1795 - (i * 80));
            }

        }

        if (displayPokemon(getCurrentDexPosition()) || pokedex.hasSeen(getCurrentDexPosition())) {
            listFont.draw(batch, pokemonList.get(selectPosition).getName(), 100, 1800);
            pokemonSprite.draw(batch);
        } else {
            batch.draw(unknownPokemon, 50, 1330, 396, 396);
        }
        listFont.draw(batch, "SEEN:", 130, 1250);
        listFont.draw(batch,Integer.toString(seenAmount), 400, 1250);
        listFont.draw(batch, "OWNED:", 130, 1150);
        listFont.draw(batch,Integer.toString(obtainedAmount), 400, 1150);



    }

    private boolean displayPokemon(int dexNum) {
        if (!ENABLE_POKEDEX) {
            if (pokedex.hasObtained(dexNum) || pokedex.hasSeen(dexNum)) {
                return true;
            }
            return false;
        }
        return true;
    }

    private void drawSelector(SpriteBatch batch) {
        batch.draw(selectTexture, 482, 1730 - selectPosition * 80, selectTexture.getWidth() * 2 + 30, selectTexture.getHeight() * 2 + 13);
    }

    private String getDexNumberString(int num) {
        if (num < 10) {
            return "00" + num;
        } else if (num < 100) {
            return "0" + num;
        }
        return Integer.toString(num);
    }

    private String getUnknownPokemonString(int num) {
        String str = getDexNumberString(num);
        return str + "-----------";
    }

    private String getKnownPokemonString(int i) {
        String str = getDexNumberString(listOffset + 1 + i);
        return str + " " + pokemonList.get(i).getName();
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
        ownedIcon.dispose();
        seenIcon.dispose();
        menuScreen.dispose();
        nestScreen.dispose();
        entryScreen.dispose();
        spritesScreen.dispose();
        listScreen.dispose();
        arrow.dispose();
        menuFont.dispose();
        listFont.dispose();
//        nameFont.dispose();
        unknownPokemon.dispose();
        selectTexture.dispose();
    }
}
