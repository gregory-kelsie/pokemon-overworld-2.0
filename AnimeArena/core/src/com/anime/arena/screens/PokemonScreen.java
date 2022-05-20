package com.anime.arena.screens;

import com.anime.arena.AnimeArena;
import com.anime.arena.items.Bag;
import com.anime.arena.items.BagItem;
import com.anime.arena.objects.Player;
import com.anime.arena.pokemon.*;
import com.anime.arena.tools.DatabaseLoader;
import com.anime.arena.tools.TextFormater;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

public class PokemonScreen implements Screen {

    private AnimeArena game;
    private Player player;
    private Texture black;
    private OrthographicCamera gameCam;
    private OrthographicCamera controlsCam;
    private Viewport gamePort;

    private Screen previousScreen;
    private BasePokemonFactory pokemonFactory;

    private String vertexShader;
    private String fragmentShader;
    private ShaderProgram shaderProgram;


    private BitmapFont regularFont;
    private BitmapFont abilityFont;
    private BitmapFont partyFont;
    private BitmapFont partyHealthFont;
    private BitmapFont partyLevelFont;

    private int screenPosition;
    private boolean inMiniMenu;
    private boolean inItemMenu;
    private int miniMenuPosition; //0 is summary, 1 is switch, 2 is item, 3 is quit
    private int summaryPosition; //0 is summary, 1 is stats, 2 is moves, 3 is ribbons

    //Textures
    private Texture partyBackground;
    private Texture partyWhiteTextBackground;
    private Texture summaryBackground;
    private Texture statsBackground;
    private Texture bottomBorder;
    private Texture statusBackground;
    private Texture partyCancelButton;
    private Texture emptyPanel;
    private Texture blankPanel;
    private Texture selectedPanel;
    private Texture selectedFaintPanel;
    private Texture faintPanel;
    private Texture switchPanel;
    private Texture switchSelectedPanel;
    private Texture switchFaintPanel;
    private Texture switchFaintSelectedPanel;
    private Texture partyHealthBar;
    private Texture partyLevel;
    private Texture greenHealthTexture;
    private Texture yellowHealthTexture;
    private Texture redHealthTexture;
    private Texture itemTexture;
    private Texture maleTexture;
    private Texture femaleTexture;
    private Texture miniMenuOption;
    private Texture miniMenuSelOption;
    private Texture white;
    private Texture expTexture;
    private Texture ratingS;
    private Texture ratingA;
    private Texture ratingB;
    private Texture ratingC;
    private Texture ratingD;
    private Texture ratingF;
    private Texture paralyzedTexture;
    private Texture poisonedTexture;
    private Texture frozenTexture;
    private Texture burnedTexture;
    private Texture sleepTexture;
    private Texture faintedTexture;


    //Sprites
    private Sprite firstPokemon;
    private Sprite secondPokemon;
    private Sprite thirdPokemon;
    private Sprite fourthPokemon;
    private Sprite fifthPokemon;
    private Sprite sixthPokemon;

    private Sprite selectedPokemon;
    private Sprite typeOne;
    private Sprite typeTwo;
    private Sprite summaryBall;


    private int mode; //0 is regular mode from the main menu, 1 is item mode from the bag (we're using an item on a pokemon)
    private int selectPosition;
    private int switchPokemon;
    private String abilityName;
    private String abilityDescription;


    private List<BasePokemon> pokemonList;



    private TextureAtlas pokemonAtlas;
    private TextureAtlas pokemonTypeAtlas;
    private TextureAtlas pokemonIconAtlas;

    private Bag bag;
    private BagItem item;

    public PokemonScreen(AnimeArena game, Screen previousScreen, Player player, TextureAtlas pokemonAtlas, TextureAtlas pokemonIconAtlas, TextureAtlas pokemonTypeAtlas, DatabaseLoader dbLoader) {
        this.game = game;
        this.player = player;
        this.pokemonAtlas = pokemonAtlas;
        this.pokemonIconAtlas = pokemonIconAtlas;
        this.pokemonTypeAtlas = pokemonTypeAtlas;
        this.previousScreen = previousScreen;
        this.pokemonFactory = dbLoader.getBasePokemonFactory();
        this.pokemonList = new ArrayList<BasePokemon>();

        this.vertexShader = Gdx.files.internal("shaders/default.vs").readString();
        this.fragmentShader = Gdx.files.internal("shaders/white.fs").readString();
        this.shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

        this.black = new Texture("animation/black.png");
        this.partyBackground = new Texture("hud/party/bg_B2W2.png");
        this.partyWhiteTextBackground = new Texture("hud/party/bw_choice.png");
        this.partyCancelButton = new Texture("hud/party/icon_cancel.png");
        this.emptyPanel = new Texture("hud/party/panel_rect.png");
        this.blankPanel = new Texture("hud/party/panel_blank.png");
        this.selectedPanel = new Texture("hud/party/panel_rect_sel.png");
        this.switchPanel = new Texture("hud/party/panel_round.png");
        this.switchSelectedPanel = new Texture("hud/party/panel_round_sel.png");
        this.switchFaintPanel = new Texture("hud/party/panel_round_faint.png");
        this.switchFaintSelectedPanel = new Texture("hud/party/panel_round_faint_sel.png");
        this.selectedFaintPanel = new Texture("hud/party/panel_rect_faint_sel.png");
        this.faintPanel = new Texture("hud/party/panel_rect_faint.png");
        this.partyHealthBar = new Texture("hud/party/overlay_hp_back.png");
        this.partyLevel = new Texture("hud/party/overlay_lv.png");
        this.greenHealthTexture = new Texture("hud/party/greenhealth.png");
        this.yellowHealthTexture = new Texture("hud/party/yellowhealth.png");
        this.redHealthTexture = new Texture("hud/party/redhealth.png");
        this.itemTexture = new Texture("hud/party/icon_item.png");
        this.poisonedTexture = new Texture("hud/party/poisoned.png");
        this.paralyzedTexture = new Texture("hud/party/paralyzed.png");
        this.burnedTexture = new Texture("hud/party/burned.png");
        this.frozenTexture = new Texture("hud/party/frozen.png");
        this.faintedTexture = new Texture("hud/party/fainted.png");
        this.sleepTexture = new Texture("hud/party/sleep.png");
        this.maleTexture = new Texture("hud/party/male.png");
        this.femaleTexture = new Texture("hud/party/female.png");
        this.miniMenuOption = new Texture("hud/party/icon_cancel_narrow.png");
        this.miniMenuSelOption = new Texture("hud/party/icon_cancel_narrow_sel.png");
        this.white = new Texture("animation/white.png");
        this.summaryBackground = new Texture("hud/party/background.png");
        this.statusBackground = new Texture("hud/party/status.png");
        this.bottomBorder = new Texture("hud/party/bottomBorder.png");
        this.expTexture = new Texture("hud/party/overlay_exp.png");
        this.statsBackground = new Texture("hud/party/stats.png");
        this.ratingS = new Texture("hud/party/RatingS.png");
        this.ratingA = new Texture("hud/party/RatingA.png");
        this.ratingB = new Texture("hud/party/RatingB.png");
        this.ratingC = new Texture("hud/party/RatingC.png");
        this.ratingD = new Texture("hud/party/RatingD.png");
        this.ratingF = new Texture("hud/party/RatingF.png");


        //Pokemon Screens




        initFont();

        initCamera();

        initVariables();

        refreshIconSprites();


        gameCam.position.set((AnimeArena.V_WIDTH / 2) / AnimeArena.PPM, (AnimeArena.V_HEIGHT / 2) / AnimeArena.PPM, 0);

    }

    private void refreshIconSprites() {
        if (hasPokemon(0)) {
            firstPokemon = new Sprite(pokemonIconAtlas.findRegion(player.getPokemonParty().get(0).getConstantVariables().getFormattedImage()));
            firstPokemon.setPosition(80, 1750);
            firstPokemon.setSize(firstPokemon.getWidth() * 4, firstPokemon.getHeight() * 4);
        }
        if (hasPokemon(1)) {
            secondPokemon = new Sprite(pokemonIconAtlas.findRegion(player.getPokemonParty().get(1).getConstantVariables().getFormattedImage()));
            secondPokemon.setPosition(602, 1710);
            secondPokemon.setSize(secondPokemon.getWidth() * 4, secondPokemon.getHeight() * 4);
        }
        if (hasPokemon(2)) {
            thirdPokemon = new Sprite(pokemonIconAtlas.findRegion(player.getPokemonParty().get(2).getConstantVariables().getFormattedImage()));
            thirdPokemon.setPosition(80, 1514);
            thirdPokemon.setSize(thirdPokemon.getWidth() * 4, thirdPokemon.getHeight() * 4);
        }
        if (hasPokemon(3)) {
            fourthPokemon = new Sprite(pokemonIconAtlas.findRegion(player.getPokemonParty().get(3).getConstantVariables().getFormattedImage()));
            fourthPokemon.setPosition(602, 1474);
            fourthPokemon.setSize(fourthPokemon.getWidth() * 4, fourthPokemon.getHeight() * 4);
        }
        if (hasPokemon(4)) {
            fifthPokemon = new Sprite(pokemonIconAtlas.findRegion(player.getPokemonParty().get(4).getConstantVariables().getFormattedImage()));
            fifthPokemon.setPosition(80, 1272);
            fifthPokemon.setSize(fifthPokemon.getWidth() * 4, fifthPokemon.getHeight() * 4);
        }
        if (hasPokemon(5)) {
            sixthPokemon = new Sprite(pokemonIconAtlas.findRegion(player.getPokemonParty().get(5).getConstantVariables().getFormattedImage()));
            sixthPokemon.setPosition(602, 1232);
            sixthPokemon.setSize(sixthPokemon.getWidth() * 4, sixthPokemon.getHeight() * 4);
        }
    }

    public PokemonScreen(AnimeArena game, Screen previousScreen, Player player, TextureAtlas pokemonAtlas, TextureAtlas pokemonIconAtlas,
                         TextureAtlas pokemonTypeAtlas, DatabaseLoader dbLoader, Bag bag, BagItem item) {
        this(game, previousScreen, player, pokemonAtlas, pokemonIconAtlas, pokemonTypeAtlas, dbLoader);
        this.mode = 1;
        this.bag = bag;
        this.item = item;
    }

    private boolean hasPokemon(int panelNumber) {
        if (player.getPokemonParty().size() > panelNumber) {
            return true;
        }
        return false;
    }




    private void initVariables() {
        screenPosition = 0;
        selectPosition = 0;
        inMiniMenu = false;
        miniMenuPosition = 0;
        inItemMenu = false;
        summaryPosition = 0;
        switchPokemon = -1;
    }

    private void initFont() {
        //Party Font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pkmndp.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter2.size = 48;
        parameter2.color = Color.WHITE;
        parameter2.spaceY = 20;
        parameter2.spaceX = -1;

        parameter2.shadowColor = Color.GRAY;
        parameter2.shadowOffsetX = 1;
        parameter2.shadowOffsetY = 1;

        FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pkmnems.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter3 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter3.size = 48;
        parameter3.color = Color.WHITE;
        parameter3.spaceY = 20;
        parameter3.spaceX = -2;

        parameter3.shadowColor = Color.GRAY;
        parameter3.shadowOffsetX = 1;
        parameter3.shadowOffsetY = 1;

        FreeTypeFontGenerator.FreeTypeFontParameter parameter4 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter4.size = 36;
        parameter4.color = Color.WHITE;
        parameter4.spaceY = 20;
        parameter4.spaceX = 0;

        parameter4.shadowColor = Color.BLACK;
        parameter4.shadowOffsetX = 4;
        parameter4.shadowOffsetY = 4;

        FreeTypeFontGenerator generator25 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pkmnem.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter25 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter25.size = 48;
        parameter25.color = Color.BLACK;
        parameter25.spaceY = 20;
        parameter25.spaceX = 0;
        parameter25.borderColor = Color.BLACK;

        parameter25.shadowColor = Color.GRAY;
        parameter25.shadowOffsetX = 1;
        parameter25.shadowOffsetY = 1;

        FreeTypeFontGenerator.FreeTypeFontParameter abilityParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        abilityParameter.size = 40;
        abilityParameter.color = Color.BLACK;
        abilityParameter.spaceY = 16;
        abilityParameter.spaceX = 0;
        abilityParameter.borderColor = Color.BLACK;

        abilityParameter.shadowColor = Color.GRAY;
        abilityParameter.shadowOffsetX = 1;
        abilityParameter.shadowOffsetY = 1;


        regularFont = generator25.generateFont(parameter25);
        abilityFont = generator25.generateFont(abilityParameter);
        partyFont = generator.generateFont(parameter2);
        partyHealthFont = generator2.generateFont(parameter3);
        partyLevelFont = generator2.generateFont(parameter4);
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

    private boolean hasUsedAnItem() {
        if (mode == 2 || mode == 3) {
            return true;
        }
        return false;
    }



    private void handleInput(float dt) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (!hasUsedAnItem()) {
                if (screenPosition == 0 && !inMiniMenu) {
                    if (selectPosition == 2) {
                        selectPosition = 0;
                    } else if (selectPosition == 3) {
                        selectPosition = 1;
                    } else if (selectPosition == 4) {
                        selectPosition = 2;
                    } else if (selectPosition == 5) {
                        selectPosition = 3;
                    }
                } else if (screenPosition == 0 && inMiniMenu) {
                    if (miniMenuPosition == 0) {
                        miniMenuPosition = 3; // QUIT
                    } else {
                        miniMenuPosition--;
                    }
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (!hasUsedAnItem()) {
                if (screenPosition == 0 && !inMiniMenu) {
                    if (selectPosition == 0 && hasPokemon(2)) {
                        selectPosition = 2;
                    } else if (selectPosition == 1 && hasPokemon(3)) {
                        selectPosition = 3;
                    } else if (selectPosition == 2 && hasPokemon(4)) {
                        selectPosition = 4;
                    } else if (selectPosition == 3 && hasPokemon(5)) {
                        selectPosition = 5;
                    }
                } else if (screenPosition == 0 && inMiniMenu) {
                    if (miniMenuPosition == 3) {
                        miniMenuPosition = 0; // QUIT
                    } else {
                        miniMenuPosition++;
                    }
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (!hasUsedAnItem()) {
                if (screenPosition == 0 && !inMiniMenu) {
                    if (selectPosition == 0 && hasPokemon(1)) {
                        selectPosition = 1;
                    } else if (selectPosition == 2 && hasPokemon(3)) {
                        selectPosition = 3;
                    } else if (selectPosition == 4 && hasPokemon(5)) {
                        selectPosition = 5;
                    }
                } else if (screenPosition == 1) {
                    if (summaryPosition == 0) {
                        summaryPosition = 1;
                    }
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (!hasUsedAnItem()) {
                if (screenPosition == 0 && !inMiniMenu) {
                    if (selectPosition == 1) {
                        selectPosition = 0;
                    } else if (selectPosition == 3) {
                        selectPosition = 2;
                    } else if (selectPosition == 5) {
                        selectPosition = 4;
                    }
                } else if (screenPosition == 1) {
                    if (summaryPosition == 1) {
                        summaryPosition = 0;
                    }
                }
            }
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.X))) {
            if (hasUsedAnItem()) {
                closeScreen();
            }
            if (screenPosition == 0 && inMiniMenu) {
                closeMiniMenu();
            } else if (screenPosition == 1) {
                exitSummary();
            } else {
                closeScreen();
            }
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.Z))) {
            if (hasUsedAnItem()) {
                closeScreen();
            }
            if (screenPosition == 0 && !inMiniMenu && mode == 0 && switchPokemon == -1) {
                inMiniMenu = true;
            } else if(screenPosition == 0 && !inMiniMenu && mode == 0 && switchPokemon != -1) {
                Pokemon secondSwitchPokemon = player.getPokemonParty().get(selectPosition);
                player.getPokemonParty().set(selectPosition, player.getPokemonParty().get(switchPokemon));
                player.getPokemonParty().set(switchPokemon, secondSwitchPokemon);
                refreshIconSprites();
                switchPokemon = -1;
            } else if (screenPosition == 0 && mode == 1) {
                boolean result = item.getItem().use(player, getPokemon(), pokemonFactory);
                if (result) {
                    mode = 3;
                    bag.useItem(item);
                } else {
                    mode = 2;
                }
                refreshIconSprites();
            } else if (screenPosition == 0 && inMiniMenu && !inItemMenu && miniMenuPosition == 2) {
                inItemMenu = true;
                miniMenuPosition = 0;
            } else if (screenPosition == 0 && inMiniMenu && miniMenuPosition == 3) {
                closeMiniMenu();
            } else if (screenPosition == 0 && inMiniMenu && !inItemMenu && miniMenuPosition == 0) {
                closeMiniMenu();
                initSummary();
            } else if (screenPosition == 0 && inMiniMenu && !inItemMenu && miniMenuPosition == 1) {
                closeMiniMenu();
                switchPokemon = selectPosition;
            }
        }

    }

    private void exitSummary() {
        screenPosition = 0;
        summaryPosition = 0;
        abilityName = "";
        abilityDescription = "";
    }

    private void initSummary() {
        screenPosition = 1;
        List<String> abilityInfo = PokemonUtils.getAbilityInformation(pokemonFactory, getUniquePokemon().getAbility());
        abilityName = abilityInfo.get(0);
        abilityDescription = TextFormater.formatText(abilityInfo.get(1), 36.0);
        if (player.getPokemonParty().size() > 0) {
            summaryBall = new Sprite(new Texture("hud/party/summaryball00.png"));
            summaryBall.setSize(summaryBall.getWidth() * 2, summaryBall.getHeight() * 2);
            summaryBall.setPosition(675, 1725);
            selectedPokemon = new Sprite(pokemonAtlas.findRegion(getBasePokemon().getFormattedImage()));
            selectedPokemon.setSize(396, 396);
            selectedPokemon.setPosition(670, 1255);
            typeOne = new Sprite(pokemonTypeAtlas.findRegion(getFirstType()));
            typeOne.setPosition(345, 1550);
            typeOne.setSize(typeOne.getWidth() * 2, typeOne.getHeight() * 2);
            if (getBasePokemon().getSecondType() == PokemonType.NONE) {
                typeTwo = null;
            } else {
                typeTwo = new Sprite(pokemonTypeAtlas.findRegion(getSecondType()));
                typeTwo.setPosition(475, 1550);
                typeTwo.setSize(typeTwo.getWidth() * 2, typeTwo.getHeight() * 2);
            }
        }
    }

    private String getFirstType() {
        return PokemonType.toString(getBasePokemon().getFirstType()).toLowerCase();
    }

    private String getSecondType() {
        return PokemonType.toString(getBasePokemon().getSecondType()).toLowerCase();
    }

    private BasePokemon getBasePokemon() {
        if (player.getPokemonParty().size() > 0) {
            return player.getPokemonParty().get(selectPosition).getConstantVariables();
        }
        return null;
    }

    private Pokemon getPokemon() {
        if (player.getPokemonParty().size() > 0) {
            return player.getPokemonParty().get(selectPosition);
        }
        return null;
    }

    public UniquePokemon getUniquePokemon() {
        if (player.getPokemonParty().size() > 0) {
            return player.getPokemonParty().get(selectPosition).getUniqueVariables();
        }
        return null;
    }

    private void closeMiniMenu() {
        inMiniMenu = false;
        inItemMenu = false;
        miniMenuPosition = 0;
    }

    public void closeScreen() {
        game.setScreen(previousScreen);
        this.dispose();
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
        if (inMiniMenu) {
            drawMiniMenu(game.getBatch());
        }

        game.getBatch().end();
    }

    private void drawMiniMenu(SpriteBatch batch) {
        //Draw the white background with transparency over the entire screen.
        batch.setShader(shaderProgram);
        shaderProgram.setUniformf("u_time", 0.3f);
        batch.draw(white, 0, 960);
        batch.setShader(null);
        //Draw the options in the mini menu

        if (miniMenuPosition == 3) {
            batch.draw(miniMenuSelOption, 700, 980, 380, 100);
        } else {
            batch.draw(miniMenuOption, 700, 980, 380, 100);
        }
        if (miniMenuPosition == 2) {
            batch.draw(miniMenuSelOption, 700, 1083, 380, 100);
        } else {
            batch.draw(miniMenuOption, 700, 1083, 380, 100);
        }
        if (miniMenuPosition == 1) {
            batch.draw(miniMenuSelOption, 700, 1186, 380, 100);
        } else {
            batch.draw(miniMenuOption, 700, 1186, 380, 100);
        }
        if (miniMenuPosition == 0) {
            batch.draw(miniMenuSelOption, 700, 1289, 380, 100);
        } else {
            batch.draw(miniMenuOption, 700, 1289, 380, 100);
        }
        if (!inItemMenu) {
            partyFont.draw(batch, "SUMMARY", 740, 1354);
            partyFont.draw(batch, "SWITCH", 740, 1251);
            partyFont.draw(batch, "ITEM", 740, 1148);
        } else {
            partyFont.draw(batch, "GIVE", 740, 1354);
            partyFont.draw(batch, "TAKE", 740, 1251);
            partyFont.draw(batch, "MOVE", 740, 1148);
        }
        partyFont.draw(batch, "QUIT", 740, 1045);
    }

    private void drawScreen(SpriteBatch batch) {
        if (screenPosition == 0) {
            drawPartyScreen(batch);
        } else if (screenPosition == 1) {
            if (summaryPosition == 0) {
                drawSummaryScreen(batch);
            } else if (summaryPosition == 1) {
                drawStatScreen(batch);
            }
        } else if (screenPosition == 2) {

        }
    }

    private void drawStatScreen(SpriteBatch batch) {
        batch.draw(summaryBackground,0, 960, 1080, 960);
        batch.draw(statsBackground, 0, 1080, 1080, 806);
        batch.draw(bottomBorder, 0, 960, 1080, 120);
        partyFont.draw(batch, "STATS", 60, 1853);
        partyFont.draw(batch, "HP", 135, 1710);
        partyFont.draw(batch, "Attack", 40, 1605);
        partyFont.draw(batch, "Defense", 40, 1540);
        partyFont.draw(batch, "Sp. Atk", 40, 1470);
        partyFont.draw(batch, "Sp. Def", 40, 1405);
        partyFont.draw(batch, "Speed", 40, 1340);
        partyFont.draw(batch, "Ability", 80, 1260);
        if (selectedPokemon != null) {
            selectedPokemon.draw(batch);
            regularFont.draw(batch, getBasePokemon().getName(), 740, 1765);
            regularFont.draw(batch, Integer.toString(getUniquePokemon().getLevel()), 730, 1700);
            summaryBall.draw(batch);
            batch.draw(maleTexture,1010, 1735,maleTexture.getWidth() * 4, maleTexture.getHeight() * 4);
            regularFont.draw(batch, "Item", 640, 1190);
            regularFont.draw(batch, "None", 620, 1135);

            regularFont.draw(batch, getPokemon().getCurrentHealth() + "/" + getPokemon().getHealthStat(), 300, 1710);
            regularFont.draw(batch, Integer.toString(getPokemon().getAttackStat()), 330, 1605);
            regularFont.draw(batch, Integer.toString(getPokemon().getDefenseStat()), 330, 1540);
            regularFont.draw(batch, Integer.toString(getPokemon().getSpecialAttackStat()), 330, 1470);
            regularFont.draw(batch, Integer.toString(getPokemon().getSpecialDefenseStat()), 330, 1405);
            regularFont.draw(batch, Integer.toString(getPokemon().getSpeedStat()), 330, 1340);

            regularFont.draw(batch, Integer.toString(getUniquePokemon().getEvs()[0]), 500, 1710);
            regularFont.draw(batch, Integer.toString(getUniquePokemon().getEvs()[1]), 500, 1605);
            regularFont.draw(batch, Integer.toString(getUniquePokemon().getEvs()[2]), 500, 1540);
            regularFont.draw(batch, Integer.toString(getUniquePokemon().getEvs()[3]), 500, 1470);
            regularFont.draw(batch, Integer.toString(getUniquePokemon().getEvs()[4]), 500, 1405);
            regularFont.draw(batch, Integer.toString(getUniquePokemon().getEvs()[5]), 500, 1340);

            regularFont.draw(batch, Integer.toString(getUniquePokemon().getIvs()[0]), 600, 1710);
            drawRating(batch, getUniquePokemon().getIvs()[0], 240, 1675);
            regularFont.draw(batch, Integer.toString(getUniquePokemon().getIvs()[1]), 600, 1605);
            drawRating(batch, getUniquePokemon().getIvs()[1], 240, 1570);
            regularFont.draw(batch, Integer.toString(getUniquePokemon().getIvs()[2]), 600, 1540);
            drawRating(batch, getUniquePokemon().getIvs()[2], 240, 1500);
            regularFont.draw(batch, Integer.toString(getUniquePokemon().getIvs()[3]), 600, 1470);
            drawRating(batch, getUniquePokemon().getIvs()[3], 240, 1440);
            regularFont.draw(batch, Integer.toString(getUniquePokemon().getIvs()[4]), 600, 1405);
            drawRating(batch, getUniquePokemon().getIvs()[4], 240, 1370);
            regularFont.draw(batch, Integer.toString(getUniquePokemon().getIvs()[5]), 600, 1340);
            drawRating(batch, getUniquePokemon().getIvs()[5], 240, 1300);
            batch.draw(greenHealthTexture, 405, 1637,195, 8);

            regularFont.draw(batch, abilityName, 330, 1260);
            abilityFont.draw(batch, abilityDescription, 10, 1205);

        }
    }


    private void drawRating(SpriteBatch batch, int iv, int xPosition, int yPosition) {
        if (iv == 31) {
            batch.draw(ratingS, xPosition, yPosition, ratingS.getWidth() * 2, ratingS.getHeight() * 2);
        } else if (iv >= 25 && iv < 31) {
            batch.draw(ratingA, xPosition, yPosition, ratingS.getWidth() * 2, ratingS.getHeight() * 2);
        } else if (iv < 25 && iv >= 17) {
            batch.draw(ratingB, xPosition, yPosition, ratingS.getWidth() * 2, ratingS.getHeight() * 2);
        } else if (iv < 17 && iv >= 10) {
            batch.draw(ratingC, xPosition, yPosition, ratingS.getWidth() * 2, ratingS.getHeight() * 2);
        } else if (iv < 10 && iv >= 5) {
            batch.draw(ratingD, xPosition, yPosition, ratingS.getWidth() * 2, ratingS.getHeight() * 2);
        } else {
            batch.draw(ratingF, xPosition, yPosition, ratingS.getWidth() * 2, ratingS.getHeight() * 2);
        }
    }

    private void drawSummaryScreen(SpriteBatch batch) {
        batch.draw(summaryBackground,0, 960, 1080, 960);
        batch.draw(statusBackground, 0, 1080, 1080, 806);
        batch.draw(bottomBorder, 0, 960, 1080, 120);
        batch.draw(expTexture, 293, 1118, 277, 11);
        partyFont.draw(batch, "POK\u00e9MON INFO", 60, 1853);
        partyFont.draw(batch, "Dex No.", 70, 1720);
        partyFont.draw(batch, "Species", 70, 1655);
        partyFont.draw(batch, "Type", 70, 1590);
        partyFont.draw(batch, "Nature", 70, 1520);
        partyFont.draw(batch, "OT", 70, 1455);
        partyFont.draw(batch, "Exp. Points", 70, 1390);
        partyFont.draw(batch, "To Next Lv", 70, 1260);

        regularFont.draw(batch, "Item", 640, 1190);
        regularFont.draw(batch, "None", 620, 1135);

        if (selectedPokemon != null) {
            selectedPokemon.draw(batch);
            regularFont.draw(batch, PokemonUtils.getDexNumberString(getBasePokemon().getDexNumber()), 345, 1720);
            regularFont.draw(batch, getBasePokemon().getName(), 345, 1655);
            regularFont.draw(batch, Nature.toString(getUniquePokemon().getNature()), 345, 1520);
            regularFont.draw(batch, "Red", 345, 1455);
            regularFont.draw(batch, getBasePokemon().getName(), 740, 1765);
            regularFont.draw(batch, Integer.toString(getUniquePokemon().getLevel()), 730, 1700);
            regularFont.draw(batch, Integer.toString(player.getPokemonParty().get(selectPosition).getTotalExp()), 345, 1320);
            regularFont.draw(batch, Integer.toString(player.getPokemonParty().get(selectPosition).getNextLevelExp() - (int) player.getPokemonParty().get(selectPosition).getUniqueVariables().getCurrentExp()), 345, 1185);
            typeOne.draw(batch);
            if (typeTwo != null) {
                typeTwo.draw(batch);
            }
            summaryBall.draw(batch);
            batch.draw(maleTexture,1010, 1735,maleTexture.getWidth() * 4, maleTexture.getHeight() * 4);

        }

    }

    private String getBottomTextboxString() {
        if (mode == 0) {
            return "Choose a Pok\u00e9mon.";
        } else if (mode == 2) {
            return "It won't have any effect.";
        } else if (mode == 3) {
            return "The " + item.getItem().getName() + " was used.";
        }
        else {
            return "Use " + item.getItem().getName() + " on which Pok\u00e9mon?";
        }
    }

    private void drawPokemonPanel(Pokemon p, SpriteBatch batch, int x, int y, boolean selected, int pokemonPosition) {
        Texture panelTexture;
        if (switchPokemon == pokemonPosition) {
            if (selected && p.isFainted()) {
                panelTexture = switchFaintSelectedPanel;
            } else if (selected) {
                panelTexture = switchSelectedPanel;
            } else if (!selected && !p.isFainted()) {
                panelTexture = switchSelectedPanel;
            } else {
                panelTexture = switchFaintSelectedPanel;
            }
        } else {
            if (selected && p.isFainted()) {
                panelTexture = selectedFaintPanel;
            } else if (selected) {
                panelTexture = selectedPanel;
            } else if (p.isFainted()) {
                panelTexture = faintPanel;
            } else {
                panelTexture = emptyPanel;
            }
        }
        batch.draw(panelTexture, x, y, panelTexture.getWidth() * 2, panelTexture.getHeight() * 2);
    }


    private void drawPartyScreen(SpriteBatch batch) {
        batch.draw(partyBackground,0, 960, 1080, 960);
        batch.draw(partyWhiteTextBackground, 0, 960, 700, 140);
        regularFont.draw(batch, getBottomTextboxString(), 55, 1045);
        if (!inMiniMenu) {
            batch.draw(partyCancelButton, 700, 980, 380, 100);
        }
        if (selectPosition == 0) {
            drawPokemonPanel(player.getPokemonParty().get(0), batch, 20, 1690, true, 0);
        } else if (hasPokemon(0)) {
            drawPokemonPanel(player.getPokemonParty().get(0), batch, 20, 1690, false, 0);
        } else {
            batch.draw(blankPanel, 20, 1690, emptyPanel.getWidth() * 2, emptyPanel.getHeight() * 2);
        }
        if (selectPosition == 1) {
            drawPokemonPanel(player.getPokemonParty().get(1), batch, 542, 1650, true, 1);
        } else if (hasPokemon(1)) {
            drawPokemonPanel(player.getPokemonParty().get(1), batch, 542, 1650, false, 1);
        } else {
            batch.draw(blankPanel, 542, 1650, emptyPanel.getWidth() * 2, emptyPanel.getHeight() * 2);
        }
        if (selectPosition == 2) {
            drawPokemonPanel(player.getPokemonParty().get(2), batch, 20, 1454, true, 2);
        } else if (hasPokemon(2)) {
            drawPokemonPanel(player.getPokemonParty().get(2), batch, 20, 1454, false, 2);
        } else {
            batch.draw(blankPanel, 20, 1454, emptyPanel.getWidth() * 2, emptyPanel.getHeight() * 2);
        }
        if (selectPosition == 3) {
            drawPokemonPanel(player.getPokemonParty().get(3), batch, 542, 1414, true, 3);
        } else if (hasPokemon(3)) {
            drawPokemonPanel(player.getPokemonParty().get(3), batch, 542, 1414, false, 3);
        } else {
            batch.draw(blankPanel, 542, 1414, emptyPanel.getWidth() * 2, emptyPanel.getHeight() * 2);
        }
        if (selectPosition == 4) {
            drawPokemonPanel(player.getPokemonParty().get(4), batch, 20, 1212, true, 4);
        } else if (hasPokemon(4)) {
            drawPokemonPanel(player.getPokemonParty().get(4), batch, 20, 1212, false, 4);
        } else {
            batch.draw(blankPanel, 20, 1212, emptyPanel.getWidth() * 2, emptyPanel.getHeight() * 2);
        }
        if (selectPosition == 5) {
            drawPokemonPanel(player.getPokemonParty().get(5), batch, 542, 1172, true, 5);
        } else if (hasPokemon(5)) {
            drawPokemonPanel(player.getPokemonParty().get(5), batch, 542, 1172, false, 5);
        } else {
            batch.draw(blankPanel, 542, 1172, emptyPanel.getWidth() * 2, emptyPanel.getHeight() * 2);
        }
        batch.draw(partyHealthBar, 200, 1750, partyHealthBar.getWidth() * 2, partyHealthBar.getHeight() * 2);

        batch.draw(partyLevel, 65, 1720, partyLevel.getWidth() * 2, partyLevel.getHeight() * 2);
        for (int i = 0; i < 6; i++) {
            if (hasPokemon(i)) {
                drawPokemonPanel(i, batch);
            }
        }
        //overlay_lv
    }

    private int getHealthBarWidth(Pokemon p) {
        return (int)Math.round(192.0 * ((float)p.getCurrentHealth() / p.getHealthStat()));
    }

    private Texture getHealthTexture(Pokemon p) {
        float f = (float)p.getCurrentHealth() / p.getHealthStat();
        if (f > 0.5) {
            return greenHealthTexture;
        } else if (f >= 0.15) {
            return yellowHealthTexture;
        } else {
            return redHealthTexture;
        }
    }

    private Texture getStatusTexture(UniquePokemon pokemon) {
        if (pokemon.getCurrentHealth() == 0) {
            return faintedTexture;
        }
        if (pokemon.getStatus() != StatusCondition.STATUS_FREE) {
            if (pokemon.getStatus() == StatusCondition.PARALYSIS) {
                return paralyzedTexture;
            } else if (pokemon.getStatus() == StatusCondition.POISON) {
                return poisonedTexture;
            } else if (pokemon.getStatus() == StatusCondition.BURN) {
                return burnedTexture;
            } else if (pokemon.getStatus() == StatusCondition.FROZEN) {
                return frozenTexture;
            } else if (pokemon.getStatus() == StatusCondition.SLEEP) {
                return sleepTexture;
            }
        }
        return null;
    }

    private void drawStatus(int partyPosition, SpriteBatch batch, int x, int y) {
        Texture statusTexture = getStatusTexture(player.getPokemonParty().get(partyPosition).getUniqueVariables());
        if (statusTexture != null) {
            batch.draw(statusTexture, x, y, statusTexture.getWidth() * 2, statusTexture.getHeight() * 2);
        }
    }

    private void drawPokemonPanel(int panelNumber, SpriteBatch batch) {
        if (panelNumber == 0) { //80, 1750 for the sprite on the first panel other panels should be 602
            firstPokemon.draw(batch);
            partyFont.draw(batch, player.getPokemonParty().get(0).getConstantVariables().getName(), 205, 1830);
            partyHealthFont.draw(batch, player.getPokemonParty().get(0).getCurrentHealth() + " / " + player.getPokemonParty().get(0).getHealthStat(), 295, 1745);
            partyLevelFont.draw(batch, player.getPokemonParty().get(0).getUniqueVariables().getLevel() + "", 115, 1745);
            batch.draw(itemTexture, 156, 1758, itemTexture.getWidth() * 2, itemTexture.getHeight() * 2);
            drawStatus(0, batch, 160, 1720);
            batch.draw(maleTexture, 467, 1810, maleTexture.getWidth() * 4, maleTexture.getHeight() * 4);
            batch.draw(getHealthTexture(player.getPokemonParty().get(0)), 264, 1762, getHealthBarWidth(player.getPokemonParty().get(0)), 8);
        } else if (panelNumber == 1) {
            secondPokemon.draw(batch);
            partyFont.draw(batch, player.getPokemonParty().get(1).getConstantVariables().getName(), 727, 1790);
            partyHealthFont.draw(batch, player.getPokemonParty().get(1).getCurrentHealth() + " / " + player.getPokemonParty().get(1).getHealthStat(), 817, 1705);
            partyLevelFont.draw(batch, player.getPokemonParty().get(1).getUniqueVariables().getLevel() + "", 637, 1705);
            batch.draw(itemTexture, 678, 1718, itemTexture.getWidth() * 2, itemTexture.getHeight() * 2);
            drawStatus(1, batch, 682, 1680);
            batch.draw(maleTexture, 986, 1770, maleTexture.getWidth() * 4, maleTexture.getHeight() * 4);
            batch.draw(partyLevel, 587, 1680, partyLevel.getWidth() * 2, partyLevel.getHeight() * 2);
            batch.draw(partyHealthBar, 722, 1710, partyHealthBar.getWidth() * 2, partyHealthBar.getHeight() * 2);
            batch.draw(getHealthTexture(player.getPokemonParty().get(1)), 786, 1722, getHealthBarWidth(player.getPokemonParty().get(1)), 8);
        } else if (panelNumber == 2) {
            thirdPokemon.draw(batch);
            partyFont.draw(batch, player.getPokemonParty().get(2).getConstantVariables().getName(), 205, 1594);
            partyHealthFont.draw(batch, player.getPokemonParty().get(2).getCurrentHealth() + " / " + player.getPokemonParty().get(2).getHealthStat(), 295, 1509);
            partyLevelFont.draw(batch, player.getPokemonParty().get(2).getUniqueVariables().getLevel() + "", 115, 1509);
            batch.draw(itemTexture, 156, 1522, itemTexture.getWidth() * 2, itemTexture.getHeight() * 2);
            drawStatus(2, batch, 160, 1484);
            batch.draw(maleTexture, 467, 1574, maleTexture.getWidth() * 4, maleTexture.getHeight() * 4);
            batch.draw(partyLevel, 65, 1484, partyLevel.getWidth() * 2, partyLevel.getHeight() * 2);
            batch.draw(partyHealthBar, 200, 1514, partyHealthBar.getWidth() * 2, partyHealthBar.getHeight() * 2);
            batch.draw(getHealthTexture(player.getPokemonParty().get(2)), 264, 1526, getHealthBarWidth(player.getPokemonParty().get(2)), 8);
        } else if (panelNumber == 3) {
            fourthPokemon.draw(batch);
            partyFont.draw(batch, player.getPokemonParty().get(3).getConstantVariables().getName(), 727, 1554);
            partyHealthFont.draw(batch, player.getPokemonParty().get(3).getCurrentHealth() + " / " + player.getPokemonParty().get(3).getHealthStat(), 817, 1469);
            partyLevelFont.draw(batch, player.getPokemonParty().get(3).getUniqueVariables().getLevel() + "", 637, 1469);
            batch.draw(itemTexture, 678, 1482, itemTexture.getWidth() * 2, itemTexture.getHeight() * 2);
            drawStatus(3, batch, 682, 1444);
            batch.draw(maleTexture, 986, 1534, maleTexture.getWidth() * 4, maleTexture.getHeight() * 4);
            batch.draw(partyLevel, 587, 1444, partyLevel.getWidth() * 2, partyLevel.getHeight() * 2);
            batch.draw(partyHealthBar, 722, 1474, partyHealthBar.getWidth() * 2, partyHealthBar.getHeight() * 2);
            batch.draw(getHealthTexture(player.getPokemonParty().get(3)), 786, 1486, getHealthBarWidth(player.getPokemonParty().get(3)), 8);
        } else if (panelNumber == 4) {
            fifthPokemon.draw(batch);
            partyFont.draw(batch, player.getPokemonParty().get(4).getConstantVariables().getName(), 205, 1352);
            partyHealthFont.draw(batch, player.getPokemonParty().get(4).getCurrentHealth() + " / " + player.getPokemonParty().get(4).getHealthStat(), 295, 1267);
            partyLevelFont.draw(batch, player.getPokemonParty().get(4).getUniqueVariables().getLevel() + "", 115, 1267);
            batch.draw(itemTexture, 156, 1280, itemTexture.getWidth() * 2, itemTexture.getHeight() * 2);
            drawStatus(4, batch, 160, 1242);
            batch.draw(maleTexture, 467, 1332, maleTexture.getWidth() * 4, maleTexture.getHeight() * 4);
            batch.draw(partyLevel, 65, 1242, partyLevel.getWidth() * 2, partyLevel.getHeight() * 2);
            batch.draw(partyHealthBar, 200, 1272, partyHealthBar.getWidth() * 2, partyHealthBar.getHeight() * 2);
            batch.draw(getHealthTexture(player.getPokemonParty().get(4)), 264, 1284, getHealthBarWidth(player.getPokemonParty().get(4)), 8);
        } else if (panelNumber == 5) {
            sixthPokemon.draw(batch);
            partyFont.draw(batch, player.getPokemonParty().get(5).getConstantVariables().getName(), 727, 1312);
            partyHealthFont.draw(batch, player.getPokemonParty().get(5).getCurrentHealth() + " / " + player.getPokemonParty().get(5).getHealthStat(), 817, 1227);
            partyLevelFont.draw(batch, player.getPokemonParty().get(5).getUniqueVariables().getLevel() + "", 637, 1227);
            batch.draw(itemTexture, 678, 1240, itemTexture.getWidth() * 2, itemTexture.getHeight() * 2);
            drawStatus(5, batch, 682, 1202);
            batch.draw(femaleTexture, 986, 1292, maleTexture.getWidth() * 4, maleTexture.getHeight() * 4);
            batch.draw(partyLevel, 587, 1202, partyLevel.getWidth() * 2, partyLevel.getHeight() * 2);
            batch.draw(partyHealthBar, 722, 1232, partyHealthBar.getWidth() * 2, partyHealthBar.getHeight() * 2);
            batch.draw(getHealthTexture(player.getPokemonParty().get(5)), 786, 1244, getHealthBarWidth(player.getPokemonParty().get(5)), 8);
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
        partyBackground.dispose();
        sleepTexture.dispose();
        poisonedTexture.dispose();
        paralyzedTexture.dispose();
        burnedTexture.dispose();
        frozenTexture.dispose();
        faintedTexture.dispose();
    }
}

