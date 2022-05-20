package com.anime.arena.screens;

import com.anime.arena.AnimeArena;
import com.anime.arena.animation.BattleAnimationBackground;
import com.anime.arena.animation.OpenBattleTransition;
import com.anime.arena.animation.TrainerBattleAnimation;
import com.anime.arena.dto.UsernamePasswordDTO;
import com.anime.arena.pokemon.BasePokemon;
import com.anime.arena.pokemon.BasePokemonFactory;
import com.anime.arena.pokemon.Pokemon;
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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PokemonTestScreen implements Screen {

    private AnimeArena game;
    private Texture texture;

    private Texture maleTexture;
    private Texture femaleTexture;
    private Texture megaEvolutionIcon;
    private Texture dynamaxIcon;

    private Texture black;
    private Texture userHealthBar;
    private Texture expBar;
    private Texture expBarBg;
    private Texture enemyHealthBar;
    private Texture blueExp;
    private Texture greenHealth;
    private Texture redHealth;
    private Texture yellowHealth;
    private Texture battleMessage;
    private Texture textBox;
    private Texture ppBox;
    private Texture moveSelectedTexture;
    private Texture fightButtonBox;

    private Texture safeguard;
    private Texture stickyWeb;
    private Texture lightScreen;
    private Texture toxicSpikes;
    private Texture spikes;
    private Texture stealthRock;
    private Texture reflect;
    private Texture tailwind;
    private Texture mist;

    private Sprite pokemonSprite;
    private Sprite playerSprite;
    private BasePokemon currentPokemon;

    private OrthographicCamera gameCam;
    private OrthographicCamera controlsCam;
    private Viewport gamePort;

    private BasePokemonFactory basePokemonFactory;
    private TextureAtlas pokemonAtlas;
    private TextureAtlas pokemonBackAtlas;
    private TextureAtlas moveButtonAtlas;
    private TextureAtlas stageBannerAtlas;
    private TextureAtlas statusEffectAtlas;
    private TextureAtlas fightButtonAtlas;
    private TextureAtlas weatherAtlas;
    private TextureAtlas terrainAtlas;

    private TextureAtlas pokemonTypeAtlas;

    private Sprite firstMoveSprite;
    private Sprite secondMoveSprite;
    private Sprite thirdMoveSprite;
    private Sprite fourthMoveSprite;
    private Sprite currentMoveTypeSprite;

    private BitmapFont regularFont;
    private BitmapFont levelFont;
    private BitmapFont healthFont;
    private BitmapFont moveFont;

    private List<Pokemon> party;

    private int optionUI;
    private int moveOption;

    private OpenBattleTransition openBattleTransition;
    private PlayScreen previousScreen;
    private int loadedPokemon; //0 not loaded, 1 = success, 2 = failed

    public PokemonTestScreen(AnimeArena game, PlayScreen previousScreen) {
        this(game);
        this.previousScreen = previousScreen;
        this.party = previousScreen.getPlayer().getPokemonParty();
    }
    public PokemonTestScreen(AnimeArena game) {
        this.game = game;
        this.loadedPokemon = 0;
        this.pokemonAtlas = new TextureAtlas("sprites/pokemon/PokemonFront.atlas");
        this.pokemonBackAtlas = new TextureAtlas("sprites/pokemon/PokemonBack.atlas");
        this.moveButtonAtlas = new TextureAtlas("battle/ui/movebuttons/MoveButtons.atlas");
        this.stageBannerAtlas = new TextureAtlas("battle/ui/stageBanners/StageBanner.atlas");
        this.pokemonTypeAtlas = new TextureAtlas("sprites/PokemonTypes.atlas");
        this.statusEffectAtlas = new TextureAtlas("battle/ui/statusEffects/StatusEffects.atlas");
        this.fightButtonAtlas = new TextureAtlas("battle/ui/fightbuttons/FightButtons.atlas");
        this.terrainAtlas = new TextureAtlas("battle/ui/terrain/terrain.atlas");
        this.weatherAtlas = new TextureAtlas("battle/ui/weather/weather.atlas");
        black = new Texture("animation/black.png");
        texture = new Texture("battle/background/safari.png");
        maleTexture = new Texture("hud/party/male.png");
        femaleTexture = new Texture("hud/party/female.png");
        megaEvolutionIcon = new Texture("battle/ui/battleMegaEvoBox.png");
        dynamaxIcon = new Texture("battle/ui/icon_dynamax.png");
        battleMessage = new Texture("battle/ui/battleMessage2.png");
        textBox = new Texture("battle/ui/battleTextBox.png");
        ppBox = new Texture("battle/ui/ppbox.png");
        moveSelectedTexture = new Texture("battle/ui/movebuttons/move-selected2.png");
        currentMoveTypeSprite = new Sprite(pokemonTypeAtlas.findRegion("normal"));
        currentMoveTypeSprite.setSize(112, 48);
        currentMoveTypeSprite.setPosition(868, 1091);
        userHealthBar = new Texture("battle/ui/userHealthBar.png");
        expBar = new Texture("battle/ui/expBar.png");
        expBarBg = new Texture("battle/ui/expBarBg.png");
        enemyHealthBar = new Texture("battle/ui/enemyHealthBar.png");
        greenHealth = new Texture("battle/ui/greenHealth.png");
        yellowHealth = new Texture("battle/ui/yellowHealth.png");
        blueExp = new Texture("battle/ui/blueExp.png");
        redHealth = new Texture("battle/ui/redHealth.png");
        fightButtonBox = new Texture("battle/ui/movebuttonbox.png");

        //Field Icons
        safeguard = new Texture("battle/ui/fieldIcons/safeguard.png");
        stickyWeb = new Texture("battle/ui/fieldIcons/stickyweb.png");
        lightScreen = new Texture("battle/ui/fieldIcons/lightscreen.png");
        reflect = new Texture("battle/ui/fieldIcons/reflect.png");
        spikes = new Texture("battle/ui/fieldIcons/spikes.png");
        toxicSpikes = new Texture("battle/ui/fieldIcons/toxicspikes.png");
        mist = new Texture("battle/ui/fieldIcons/mist.png");
        tailwind = new Texture("battle/ui/fieldIcons/tailwind.png");
        stealthRock = new Texture("battle/ui/fieldIcons/stealthrocks.png");


        //Fonts

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pkmnems.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        parameter.borderWidth = 4;
        parameter.borderColor = Color.BLACK;
        parameter.color = Color.WHITE;
        parameter.spaceY = 20;
        parameter.spaceX = -4;

        FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PKMN RBYGSC.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter hpFontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        hpFontParam.size = 30;
        hpFontParam.borderWidth = 4;
        hpFontParam.borderColor = Color.BLACK;
        hpFontParam.color = Color.WHITE;
        hpFontParam.spaceY = 20;
        hpFontParam.spaceX = -4;

        FreeTypeFontGenerator moveGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pkmndp.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter moveParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        moveParameter.size = 36;
        moveParameter.borderWidth = 1;
        moveParameter.color = Color.BLACK;
        moveParameter.spaceY = 20;


        regularFont = generator.generateFont(parameter); // font size 12 pixels
        levelFont = generator.generateFont(parameter);
        healthFont = generator2.generateFont(hpFontParam);
        moveFont = moveGenerator.generateFont(moveParameter);
        getPokedex();

        initCamera();

        gameCam.position.set((AnimeArena.V_WIDTH / 2) / AnimeArena.PPM, (AnimeArena.V_HEIGHT / 2) / AnimeArena.PPM, 0);

        optionUI = 0; //Fight buttons
        moveOption = 0;
        openBattleTransition = new OpenBattleTransition();


    }

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
                currentPokemon = basePokemonFactory.createBasePokemon(1);
                Gdx.app.log("JSONRESP", currentPokemon.getFormattedImage());
                pokemonSprite = new Sprite(pokemonAtlas.findRegion(currentPokemon.getFormattedImage()));
                pokemonSprite.setSize(396, 396);
                pokemonSprite.setPosition(591,1490);
                BasePokemon playerPokemon = basePokemonFactory.createBasePokemon(250);
                playerSprite = new Sprite(pokemonBackAtlas.findRegion(playerPokemon.getFormattedImage()));
                playerSprite.setSize(396, 396);
                playerSprite.setPosition(122,1231);
                loadedPokemon = 2;
                firstMoveSprite = new Sprite(moveButtonAtlas.findRegion("normal"));
                firstMoveSprite.setSize(376, 84);
                firstMoveSprite.setPosition(20, 1080);

                secondMoveSprite = new Sprite(moveButtonAtlas.findRegion("grass"));
                secondMoveSprite.setSize(376, 84);
                secondMoveSprite.setPosition(401, 1080);
                thirdMoveSprite = new Sprite(moveButtonAtlas.findRegion("poison"));
                thirdMoveSprite.setSize(376, 84);
                thirdMoveSprite.setPosition(20, 991);
                fourthMoveSprite = new Sprite(moveButtonAtlas.findRegion("grass"));
                fourthMoveSprite.setSize(376, 84);
                fourthMoveSprite.setPosition(401, 991);
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("Failed LoadEvolution", t.getMessage());
                loadedPokemon = -1;
            }

            @Override
            public void cancelled() {
                Gdx.app.log("Cancelled", "cancelled");
            }
        });
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
                    int dexNum = jo.get("dex").getAsInt();
                    int pid = jo.get("pid").getAsInt();
                    if (pokemonList.containsKey(pid)) {
                        Gdx.app.log("Database Error", "Already contains PID: " + pid);
                    } else {
                        pokemonList.put(jo.get("pid").getAsInt(), jo);
                    }
                }
                basePokemonFactory = new BasePokemonFactory(pokemonArray.toString(), pokemonList);
                getEvolutions();
                Gdx.app.log("JSONRESP", pokemonArray.size() + "");

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
        Gdx.app.log("next", "line");
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
        if (openBattleTransition.isFinished()) {
            handleInput(dt);
        } else {
            openBattleTransition.update(dt);
        }

    }

    private void handleInput(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                currentPokemon = basePokemonFactory.createBasePokemon(currentPokemon.getDexNumber() + 1);
                Gdx.app.log("Pokemon Image", currentPokemon.getFormattedImage());
                currentPokemon.logEvolutionMethods();
                //pokemonSprite.getTexture().dispose();
                pokemonSprite = new Sprite(pokemonAtlas.findRegion(currentPokemon.getFormattedImage()));
                pokemonSprite.setSize(396, 396);
                pokemonSprite.setPosition(591,1490);
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                if (currentPokemon.getDexNumber() > 1) {
                    currentPokemon = basePokemonFactory.createBasePokemon(currentPokemon.getDexNumber() - 1);
                    pokemonSprite = new Sprite(pokemonAtlas.findRegion(currentPokemon.getFormattedImage()));
                    pokemonSprite.setSize(396, 396);
                    pokemonSprite.setPosition(591,1490);

                }

            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            if (moveOption == 0) {
                optionUI = 1;
            } else if (moveOption == 3) {
                if (previousScreen != null) {
                    game.setScreen(previousScreen);
                    previousScreen.stopBgm();
                    previousScreen.playMapBgm();
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            if (optionUI == 1) {
                optionUI = 0;
            }
        }  else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (optionUI == 0) {
                if (moveOption == 0) {
                    moveOption = 1;
                } else if (moveOption == 2) {
                    moveOption = 3;
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (optionUI == 0) {
                if (moveOption == 1) {
                    moveOption = 0;
                } else if (moveOption == 3) {
                    moveOption = 2;
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (optionUI == 0) {
                if (moveOption == 2) {
                    moveOption = 0;
                } else if (moveOption == 3) {
                    moveOption = 1;
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (optionUI == 0) {
                if (moveOption == 0) {
                    moveOption = 2;
                } else if (moveOption == 1) {
                    moveOption = 3;
                }
            }
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


        game.getBatch().draw(black,0,960);
        game.getBatch().draw(battleMessage, 0, 960, 1080, 305);
        game.getBatch().draw(texture, 0, 1205);




        if (pokemonSprite != null) {
            pokemonSprite.draw(game.getBatch());
            game.getBatch().draw(enemyHealthBar, 15, 1779);
            regularFont.draw(game.getBatch(), currentPokemon.getName(), 110, 1875);
            regularFont.draw(game.getBatch(), "Ho-Oh", 520, 1398);
            game.getBatch().draw(femaleTexture, 810, 1366, 28, 40);
            game.getBatch().draw(maleTexture, 400, 1843, 28, 40);
            game.getBatch().draw(megaEvolutionIcon, 60, 1843,44,48);
            //game.getBatch().draw(megaEvolutionIcon, 470, 1366,44,48);
            game.getBatch().draw(dynamaxIcon, 470, 1366,44,44);
            levelFont.draw(game.getBatch(), "100", 485, 1875);
            levelFont.draw(game.getBatch(), "18", 895, 1398);

            game.getBatch().draw(weatherAtlas.findRegion("rain"), 10, 1680, 116, 64);
            game.getBatch().draw(terrainAtlas.findRegion("none"), 10, 1610, 116, 64);

            game.getBatch().draw(spikes, 10, 1210, 36, 36);
            game.getBatch().draw(toxicSpikes, 46, 1210, 36, 36);
            game.getBatch().draw(safeguard, 82, 1210, 36, 36);
            game.getBatch().draw(mist, 118, 1210, 36, 36);
            game.getBatch().draw(lightScreen, 154, 1210, 36, 36);
            game.getBatch().draw(reflect, 190, 1210, 36, 36);
            game.getBatch().draw(tailwind, 226, 1210, 36, 36);
            game.getBatch().draw(stickyWeb, 262, 1210, 36, 36);
            game.getBatch().draw(stealthRock, 298, 1210, 36, 36);

            game.getBatch().draw(spikes, 1040, 1880, 36, 36);
            game.getBatch().draw(toxicSpikes, 1004, 1880, 36, 36);
            game.getBatch().draw(safeguard, 968, 1880, 36, 36);
            game.getBatch().draw(mist, 932, 1880, 36, 36);
            game.getBatch().draw(lightScreen, 896, 1880, 36, 36);
            game.getBatch().draw(reflect, 860, 1880, 36, 36);
            game.getBatch().draw(tailwind, 824, 1880, 36, 36);
            game.getBatch().draw(stickyWeb, 788, 1880, 36, 36);
            game.getBatch().draw(stealthRock, 752, 1880, 36, 36);
        }
        if (playerSprite != null) {

            playerSprite.draw(game.getBatch());

            drawHealthBars();
            drawBattleStages();
            drawStatusEffects();
            if (optionUI == 1) {
                drawMoves();
            } else if (optionUI == 0) {
                game.getBatch().draw(fightButtonBox, 20, 995,480, 168);
                if (moveOption == 0) {
                    game.getBatch().draw(fightButtonAtlas.findRegion("fight-selected"), 520, 1085, 252, 84);
                } else {
                    game.getBatch().draw(fightButtonAtlas.findRegion("fight"), 520, 1085, 252, 84);
                }
                if (moveOption == 1) {
                    game.getBatch().draw(fightButtonAtlas.findRegion("bag-selected"), 780, 1085, 252, 84);
                } else {
                    game.getBatch().draw(fightButtonAtlas.findRegion("bag"), 780, 1085, 252, 84);
                }
                if (moveOption == 2) {
                    game.getBatch().draw(fightButtonAtlas.findRegion("pokemon-selected"), 520, 990, 252, 84);
                } else {
                    game.getBatch().draw(fightButtonAtlas.findRegion("pokemon"), 520, 990, 252, 84);
                }
                if (moveOption == 3) {
                    game.getBatch().draw(fightButtonAtlas.findRegion("run-selected"), 780, 990, 252, 84);
                } else {
                    game.getBatch().draw(fightButtonAtlas.findRegion("run"), 780, 990, 252, 84);
                }
                moveFont.draw(game.getBatch(), "What will", 60, 1115);
                moveFont.draw(game.getBatch(), "Bulbasaur do?", 60, 1065);
            }
        }
        if (!openBattleTransition.isFinished()) {
            openBattleTransition.render(game.getBatch());
        }
        game.getBatch().end();
    }

    private void drawMoves() {


        //Draw Move Boxes
        firstMoveSprite.draw(game.getBatch());

        secondMoveSprite.draw(game.getBatch());
        thirdMoveSprite.draw(game.getBatch());
        fourthMoveSprite.draw(game.getBatch());

        //Draw PP Box
        game.getBatch().draw(ppBox, 802, 991, 246, 178);
        //Draw the current selected move type
        currentMoveTypeSprite.draw(game.getBatch());
        moveFont.draw(game.getBatch(), "PP: 35/35", 840, 1070);

        moveFont.draw(game.getBatch(), "Tackle", TextFormater.getMoveXPosition("Tackle", 200), 1135);
        moveFont.draw(game.getBatch(), "Vine Whip", TextFormater.getMoveXPosition("Vine Whip", 581), 1135);
        moveFont.draw(game.getBatch(), "Poisonpowder", TextFormater.getMoveXPosition("Poisonpowder", 200), 1046);
        moveFont.draw(game.getBatch(), "Razor Leaf", TextFormater.getMoveXPosition("Razor Leaf", 581), 1046);
        //Draw the Select Move Texture
        game.getBatch().draw(moveSelectedTexture, 20, 1080, 376, 84);

    }

    private void drawBlueExp(int currentExp, int requiredExp) {
        float expPercentage = (1.0f * currentExp) / requiredExp;
        expPercentage = 0.7f;
        game.getBatch().draw(blueExp, 573, 1266, Math.round(451 * ((expPercentage * 100.0) / 100.0)), 21);
    }

    private void drawHealthBars() {
        //Draw User Health Bar
        game.getBatch().draw(userHealthBar, 432,1266);

        game.getBatch().draw(expBarBg, 573, 1266);
        drawBlueExp(0, 10);
        game.getBatch().draw(expBar, 573, 1266);
        //Draw Health Amount
        healthFont.draw(game.getBatch(), "100", 885, 1329);
        if (2 >= 100) {
            healthFont.draw(game.getBatch(), "100", 740, 1329);
        } else if (2 >= 10) {
            healthFont.draw(game.getBatch(), "25", 775, 1329);
        } else {
            healthFont.draw(game.getBatch(), "2", 805, 1329);
        }

        //Draw Health Bars
        game.getBatch().draw(greenHealth, 711, 1337, Math.round(263 * ((1.0 * 100.0) / 100.0)), 15);
        game.getBatch().draw(redHealth, 221, 1817, Math.round(263 * ((0.1 * 100.0) / 100.0)), 15);
    }

    private void drawStatusEffects() {
        game.getBatch().draw(statusEffectAtlas.findRegion("poison"), 35, 1800, 88, 32);
        game.getBatch().draw(statusEffectAtlas.findRegion("burn"), 525, 1323, 88, 32);
    }

    private void drawBattleStages() {
        game.getBatch().draw(stageBannerAtlas.findRegion("atk"), 0, 1500, 88, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("firstStage"), 89, 1500, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("positive"), 125, 1500, 36, 32);

        game.getBatch().draw(stageBannerAtlas.findRegion("def"), 0, 1467, 88, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("firstStage"), 89, 1467, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("positive"), 125, 1467, 36, 32);

        game.getBatch().draw(stageBannerAtlas.findRegion("spatk"), 0, 1436, 88, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("firstStage"), 89, 1436, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("positive"), 125, 1436, 36, 32);

        game.getBatch().draw(stageBannerAtlas.findRegion("spdef"), 0, 1403, 88, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("firstStage"), 89, 1403, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("positive"), 125, 1403, 36, 32);

        game.getBatch().draw(stageBannerAtlas.findRegion("speed"), 0, 1371, 88, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("firstStage"), 89, 1371, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("positive"), 125, 1371, 36, 32);

        game.getBatch().draw(stageBannerAtlas.findRegion("accuracy"), 0, 1339, 88, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("firstStage"), 89, 1339, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("positive"), 125, 1339, 36, 32);

        game.getBatch().draw(stageBannerAtlas.findRegion("evasion"), 0, 1307, 88, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("firstStage"), 89, 1307, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("positive"), 125, 1307, 36, 32);


        game.getBatch().draw(stageBannerAtlas.findRegion("positive-f"), 922, 1800, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("firstStage"), 1047, 1800, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("atk"), 958, 1800, 88, 32);

        game.getBatch().draw(stageBannerAtlas.findRegion("positive-f"), 922, 1767, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("firstStage"), 1047, 1767, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("def"), 958, 1767, 88, 32);

        game.getBatch().draw(stageBannerAtlas.findRegion("positive-f"), 922, 1735, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("firstStage"), 1047, 1735, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("spatk"), 958, 1735, 88, 32);

        game.getBatch().draw(stageBannerAtlas.findRegion("positive-f"), 922, 1703, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("firstStage"), 1047, 1703, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("spdef"), 958, 1703, 88, 32);

        game.getBatch().draw(stageBannerAtlas.findRegion("positive-f"), 922, 1671, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("firstStage"), 1047, 1671, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("speed"), 958, 1671, 88, 32);

        game.getBatch().draw(stageBannerAtlas.findRegion("positive-f"), 922, 1639, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("firstStage"), 1047, 1639, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("accuracy"), 958, 1639, 88, 32);

        game.getBatch().draw(stageBannerAtlas.findRegion("positive-f"), 922, 1607, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("firstStage"), 1047, 1607, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion("evasion"), 958, 1607, 88, 32);

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

