package com.anime.arena.screens;

import com.anime.arena.AnimeArena;
import com.anime.arena.animation.OpenBattleTransition;
import com.anime.arena.pokemon.*;
import com.anime.arena.skill.DamageSkill;
import com.anime.arena.skill.Skill;
import com.anime.arena.skill.SkillCategory;
import com.anime.arena.skill.SkillTarget;
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

public class PokemonBattleScreen implements Screen {

    private AnimeArena game;
    private Texture texture;
    private Texture black;
    private Texture userHealthBar;
    private Texture enemyHealthBar;
    private Texture greenHealth;
    private Texture redHealth;
    private Texture yellowHealth;
    private Texture battleMessage;
    private Texture ppBox;
    private Texture moveSelectedTexture;
    private Texture fightButtonBox;

    private Sprite pokemonSprite;
    private Sprite playerSprite;
    private BasePokemon currentPokemon;
    private BattlePokemon playerPokemon;
    private BattlePokemon enemyPokemon;

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

    private int currentPP;
    private int totalPP;

    private OpenBattleTransition openBattleTransition;
    private PlayScreen previousScreen;
    private int loadedPokemon; //0 not loaded, 1 = success, 2 = failed

    public PokemonBattleScreen(AnimeArena game, PlayScreen previousScreen, Pokemon wildPokemon) {
        this(game);
        this.basePokemonFactory = previousScreen.getBasePokemonFactory();
        this.previousScreen = previousScreen;
        this.party = previousScreen.getPlayer().getPokemonParty();
        this.enemyPokemon = new BattlePokemon(wildPokemon);
        getPokedex();
        currentMoveTypeSprite = createMoveTypeIcon(playerPokemon.getFirstMove().getMoveType());
        currentPP = playerPokemon.getFirstMove().getCurrentPP();
        totalPP = playerPokemon.getFirstMove().getMaxPP();
        //Update Seen for Pokedex
        previousScreen.getPlayer().getPokedex().updateSeen(wildPokemon.getConstantVariables().getDexNumber());
    }
    public PokemonBattleScreen(AnimeArena game) {
        this.game = game;
        this.loadedPokemon = 0;
        this.pokemonAtlas = new TextureAtlas("sprites/pokemon/PokemonFront.atlas");
        this.pokemonBackAtlas = new TextureAtlas("sprites/pokemon/PokemonBack.atlas");
        this.moveButtonAtlas = new TextureAtlas("battle/ui/movebuttons/MoveButtons.atlas");
        this.stageBannerAtlas = new TextureAtlas("battle/ui/stageBanners/StageBanner.atlas");
        this.pokemonTypeAtlas = new TextureAtlas("sprites/PokemonTypes.atlas");
        this.statusEffectAtlas = new TextureAtlas("battle/ui/statusEffects/StatusEffects.atlas");
        this.fightButtonAtlas = new TextureAtlas("battle/ui/fightbuttons/FightButtons.atlas");
        black = new Texture("animation/black.png");
        texture = new Texture("battle/background/safari.png");
        battleMessage = new Texture("battle/ui/battleMessage2.png");
        ppBox = new Texture("battle/ui/ppbox.png");
        moveSelectedTexture = new Texture("battle/ui/movebuttons/move-selected2.png");
        currentMoveTypeSprite = new Sprite(pokemonTypeAtlas.findRegion("normal"));
        currentMoveTypeSprite.setSize(112, 48);
        currentMoveTypeSprite.setPosition(868, 1091);
        userHealthBar = new Texture("battle/ui/userHealthBar.png");
        enemyHealthBar = new Texture("battle/ui/enemyHealthBar.png");
        greenHealth = new Texture("battle/ui/greenHealth.png");
        yellowHealth = new Texture("battle/ui/yellowHealth.png");
        redHealth = new Texture("battle/ui/redHealth.png");
        fightButtonBox = new Texture("battle/ui/movebuttonbox.png");

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

        initCamera();

        gameCam.position.set((AnimeArena.V_WIDTH / 2) / AnimeArena.PPM, (AnimeArena.V_HEIGHT / 2) / AnimeArena.PPM, 0);

        optionUI = 0; //Fight buttons
        moveOption = 0;
        openBattleTransition = new OpenBattleTransition();


    }

    private void getPokedex() {

        currentPokemon = basePokemonFactory.createBasePokemon(1);
        Gdx.app.log("JSONRESP", enemyPokemon.getPokemon().getConstantVariables().getFormattedImage());
        pokemonSprite = new Sprite(pokemonAtlas.findRegion(enemyPokemon.getPokemon().getConstantVariables().getFormattedImage()));
        pokemonSprite.setSize(396, 396);
        pokemonSprite.setPosition(591,1490);
        playerPokemon = new BattlePokemon(party.get(0));
        playerPokemon.resetMoves();
        playerPokemon.getPokemon().getUniqueVariables().getMoves().add(createSkillTackle());
        playerPokemon.getPokemon().getUniqueVariables().getMoves().add(createSkillQuickAttack());
        playerPokemon.getPokemon().getUniqueVariables().getMoves().add(createSkillFlamethrower());
        playerSprite = new Sprite(pokemonBackAtlas.findRegion(playerPokemon.getPokemon().getConstantVariables().getFormattedImage()));
        playerSprite.setSize(396, 396);
        playerSprite.setPosition(122,1231);
        loadedPokemon = 2;
        initMoveSprites();

    }

    private Skill createSkillTackle() {
        String name = "Tackle";
        String description = "A physical attack in which the user charges and slams into the target with its whole body.";
        SkillCategory category = SkillCategory.PHYSICAL;
        int tacklePP = 35;
        int accuracy = 100;
        PokemonType moveType = PokemonType.NORMAL;
        SkillTarget target = SkillTarget.ENEMY;
        int subtype = 0;
        int speedPriority = 0;
        int basePower = 40;
        int criticalRate = 1;
        return new DamageSkill(name, description, category, tacklePP, tacklePP, accuracy, moveType, target, subtype,
                speedPriority, basePower, criticalRate);
    }

    private Skill createSkillFlamethrower() {
        String name = "Flamethrower";
        String description = "A physical attack in which the user charges and slams into the target with its whole body.";
        SkillCategory category = SkillCategory.SPECIAL;
        int tacklePP = 15;
        int accuracy = 100;
        PokemonType moveType = PokemonType.FIRE;
        SkillTarget target = SkillTarget.ENEMY;
        int subtype = 0;
        int speedPriority = 0;
        int basePower = 90;
        int criticalRate = 1;
        return new DamageSkill(name, description, category, tacklePP, tacklePP, accuracy, moveType, target, subtype,
                speedPriority, basePower, criticalRate);
    }

    private Skill createSkillQuickAttack() {
        String name = "Quick Attack";
        String description = "A physical attack in which the user charges and slams into the target with its whole body.";
        SkillCategory category = SkillCategory.PHYSICAL;
        int tacklePP = 30;
        int accuracy = 100;
        PokemonType moveType = PokemonType.NORMAL;
        SkillTarget target = SkillTarget.ENEMY;
        int subtype = 0;
        int speedPriority = 1;
        int basePower = 40;
        int criticalRate = 1;
        return new DamageSkill(name, description, category, tacklePP, tacklePP, accuracy, moveType, target, subtype,
                speedPriority, basePower, criticalRate);
    }

    private void initMoveSprites() {
        firstMoveSprite = playerPokemon.getFirstMove() != null ? createMoveSprite(playerPokemon.getFirstMove().getMoveType())
                : new Sprite(moveButtonAtlas.findRegion("empty"));
        firstMoveSprite.setSize(376, 84);
        firstMoveSprite.setPosition(20, 1080);

        secondMoveSprite = playerPokemon.getSecondMove() != null ? createMoveSprite(playerPokemon.getSecondMove().getMoveType())
                : new Sprite(moveButtonAtlas.findRegion("empty"));
        secondMoveSprite.setSize(376, 84);
        secondMoveSprite.setPosition(401, 1080);
        thirdMoveSprite = playerPokemon.getThirdMove() != null ? createMoveSprite(playerPokemon.getThirdMove().getMoveType())
                : new Sprite(moveButtonAtlas.findRegion("empty"));
        thirdMoveSprite.setSize(376, 84);
        thirdMoveSprite.setPosition(20, 991);
        fourthMoveSprite = playerPokemon.getFourthMove() != null ? createMoveSprite(playerPokemon.getFourthMove().getMoveType())
                : new Sprite(moveButtonAtlas.findRegion("empty"));
        fourthMoveSprite.setSize(376, 84);
        fourthMoveSprite.setPosition(401, 991);
    }

    private Sprite createMoveSprite(PokemonType type) {
        if (type == PokemonType.BUG) {
            return new Sprite(moveButtonAtlas.findRegion("bug"));
        } else if (type == PokemonType.DARK) {
            return new Sprite(moveButtonAtlas.findRegion("dark"));
        } else if (type == PokemonType.NORMAL) {
            return new Sprite(moveButtonAtlas.findRegion("normal"));
        } else if (type == PokemonType.DRAGON) {
            return new Sprite(moveButtonAtlas.findRegion("dragon"));
        } else if (type == PokemonType.ELECTRIC) {
            return new Sprite(moveButtonAtlas.findRegion("electric"));
        } else if (type == PokemonType.FAIRY) {
            return new Sprite(moveButtonAtlas.findRegion("fairy"));
        } else if (type == PokemonType.FIGHTING) {
            return new Sprite(moveButtonAtlas.findRegion("fighting"));
        } else if (type == PokemonType.FIRE) {
            return new Sprite(moveButtonAtlas.findRegion("fire"));
        } else if (type == PokemonType.FLYING) {
            return new Sprite(moveButtonAtlas.findRegion("flying"));
        } else if (type == PokemonType.GHOST) {
            return new Sprite(moveButtonAtlas.findRegion("ghost"));
        } else if (type == PokemonType.GRASS) {
            return new Sprite(moveButtonAtlas.findRegion("grass"));
        } else if (type == PokemonType.GROUND) {
            return new Sprite(moveButtonAtlas.findRegion("ground"));
        } else if (type == PokemonType.ICE) {
            return new Sprite(moveButtonAtlas.findRegion("ice"));
        } else if (type == PokemonType.POISON) {
            return new Sprite(moveButtonAtlas.findRegion("poison"));
        } else if (type == PokemonType.PSYCHIC) {
            return new Sprite(moveButtonAtlas.findRegion("psychic"));
        } else if (type == PokemonType.ROCK) {
            return new Sprite(moveButtonAtlas.findRegion("rock"));
        } else if (type == PokemonType.STEEL) {
            return new Sprite(moveButtonAtlas.findRegion("steel"));
        } else if (type == PokemonType.WATER) {
            return new Sprite(moveButtonAtlas.findRegion("water"));
        } else if (type == PokemonType.NONE) {
            return new Sprite(moveButtonAtlas.findRegion("none"));
        }
        return new Sprite(moveButtonAtlas.findRegion("empty"));
    }

    public Sprite createMoveTypeIcon(PokemonType type) {
        Sprite s;
        if (type == PokemonType.BUG) {
            s = new Sprite(pokemonTypeAtlas.findRegion("bug"));
        } else if (type == PokemonType.DARK) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("dark"));
        } else if (type == PokemonType.NORMAL) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("normal"));
        } else if (type == PokemonType.DRAGON) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("dragon"));
        } else if (type == PokemonType.ELECTRIC) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("electric"));
        } else if (type == PokemonType.FAIRY) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("fairy"));
        } else if (type == PokemonType.FIGHTING) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("fighting"));
        } else if (type == PokemonType.FIRE) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("fire"));
        } else if (type == PokemonType.FLYING) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("flying"));
        } else if (type == PokemonType.GHOST) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("ghost"));
        } else if (type == PokemonType.GRASS) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("grass"));
        } else if (type == PokemonType.GROUND) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("ground"));
        } else if (type == PokemonType.ICE) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("ice"));
        } else if (type == PokemonType.POISON) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("poison"));
        } else if (type == PokemonType.PSYCHIC) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("psychic"));
        } else if (type == PokemonType.ROCK) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("rock"));
        } else if (type == PokemonType.STEEL) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("steel"));
        } else if (type == PokemonType.WATER) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("water"));
        } else if (type == PokemonType.NONE) {
            s =  new Sprite(pokemonTypeAtlas.findRegion("unknown"));
        } else {
            s =  new Sprite(pokemonTypeAtlas.findRegion("blank"));
        }
        s.setSize(112, 48);
        s.setPosition(868, 1091);
        return s;
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
                    previousScreen.toggleBlackScreen();
                    game.setScreen(previousScreen);
                    previousScreen.stopBgm();
                    previousScreen.playMapBgm();
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            if (optionUI == 1) {
                optionUI = 0;
                moveOption = 0;
                currentPP = playerPokemon.getFirstMove().getCurrentPP();
                totalPP = playerPokemon.getFirstMove().getMaxPP();
                currentMoveTypeSprite = createMoveTypeIcon(playerPokemon.getFirstMove().getMoveType());
            }
        }  else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (optionUI == 0) {
                if (moveOption == 0) {
                    moveOption = 1;
                } else if (moveOption == 2) {
                    moveOption = 3;
                }
            } else if (optionUI == 1) {
                if (moveOption == 0 && playerPokemon.getSecondMove() != null) {
                    moveOption = 1;
                    currentMoveTypeSprite = createMoveTypeIcon(playerPokemon.getSecondMove().getMoveType());
                    currentPP = playerPokemon.getSecondMove().getCurrentPP();
                    totalPP = playerPokemon.getSecondMove().getMaxPP();
                } else if (moveOption == 2 && playerPokemon.getFourthMove() != null) {
                    moveOption = 3;
                    currentMoveTypeSprite = createMoveTypeIcon(playerPokemon.getFourthMove().getMoveType());
                    currentPP = playerPokemon.getFourthMove().getCurrentPP();
                    totalPP = playerPokemon.getFourthMove().getMaxPP();
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (optionUI == 0) {
                if (moveOption == 1) {
                    moveOption = 0;
                } else if (moveOption == 3) {
                    moveOption = 2;
                }
            } else if (optionUI == 1) {
                if (moveOption == 1) {
                    moveOption = 0;
                    currentMoveTypeSprite = createMoveTypeIcon(playerPokemon.getFirstMove().getMoveType());
                    currentPP = playerPokemon.getFirstMove().getCurrentPP();
                    totalPP = playerPokemon.getFirstMove().getMaxPP();
                } else if (moveOption == 3) {
                    moveOption = 2;
                    currentMoveTypeSprite = createMoveTypeIcon(playerPokemon.getThirdMove().getMoveType());
                    currentPP = playerPokemon.getThirdMove().getCurrentPP();
                    totalPP = playerPokemon.getThirdMove().getMaxPP();
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (optionUI == 0) {
                if (moveOption == 2) {
                    moveOption = 0;
                } else if (moveOption == 3) {
                    moveOption = 1;
                }
            } else if (optionUI == 1) {
                if (moveOption == 2) {
                    moveOption = 0;
                    currentMoveTypeSprite = createMoveTypeIcon(playerPokemon.getFirstMove().getMoveType());
                    currentPP = playerPokemon.getFirstMove().getCurrentPP();
                    totalPP = playerPokemon.getFirstMove().getMaxPP();
                } else if (moveOption == 3) {
                    moveOption = 1;
                    currentMoveTypeSprite = createMoveTypeIcon(playerPokemon.getSecondMove().getMoveType());
                    currentPP = playerPokemon.getSecondMove().getCurrentPP();
                    totalPP = playerPokemon.getSecondMove().getMaxPP();
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (optionUI == 0) {
                if (moveOption == 0) {
                    moveOption = 2;
                } else if (moveOption == 1) {
                    moveOption = 3;
                }
            } else if (optionUI == 1) {
                if (moveOption == 0 && playerPokemon.getThirdMove() != null) {
                    moveOption = 2;
                    currentMoveTypeSprite = createMoveTypeIcon(playerPokemon.getThirdMove().getMoveType());
                    currentPP = playerPokemon.getThirdMove().getCurrentPP();
                    totalPP = playerPokemon.getThirdMove().getMaxPP();
                } else if (moveOption == 1 && playerPokemon.getFourthMove() != null) {
                    moveOption = 3;
                    currentMoveTypeSprite = createMoveTypeIcon(playerPokemon.getFourthMove().getMoveType());
                    currentPP = playerPokemon.getFourthMove().getCurrentPP();
                    totalPP = playerPokemon.getFourthMove().getMaxPP();
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
            regularFont.draw(game.getBatch(), enemyPokemon.getPokemon().getConstantVariables().getName(), 110, 1875);
            regularFont.draw(game.getBatch(), playerPokemon.getPokemon().getConstantVariables().getName(), 520, 1398);
            levelFont.draw(game.getBatch(), Integer.toString(enemyPokemon.getPokemon().getUniqueVariables().getLevel()), 485, 1875);
            levelFont.draw(game.getBatch(), Integer.toString(playerPokemon.getPokemon().getUniqueVariables().getLevel()), 895, 1398);

        }
        if (playerSprite != null) {

            playerSprite.draw(game.getBatch());

            drawHealthBars();
            //drawBattleStages();
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
                moveFont.draw(game.getBatch(), playerPokemon.getPokemon().getConstantVariables().getName() + " do?", 60, 1065);
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

        moveFont.draw(game.getBatch(), "PP: " + currentPP + "/" + totalPP, 840, 1070);

        moveFont.draw(game.getBatch(), playerPokemon.getFirstMove().getName(), TextFormater.getMoveXPosition(playerPokemon.getFirstMove().getName(), 200), 1135);
        if (playerPokemon.getSecondMove() != null) {
            moveFont.draw(game.getBatch(), playerPokemon.getSecondMove().getName(), TextFormater.getMoveXPosition(playerPokemon.getSecondMove().getName(), 581), 1135);
            if (playerPokemon.getThirdMove() != null) {
                moveFont.draw(game.getBatch(), playerPokemon.getThirdMove().getName(), TextFormater.getMoveXPosition(playerPokemon.getThirdMove().getName(), 200), 1046);
                if (playerPokemon.getFourthMove() != null) {
                    moveFont.draw(game.getBatch(), playerPokemon.getFourthMove().getName(), TextFormater.getMoveXPosition(playerPokemon.getFourthMove().getName(), 581), 1046);
                }
            }
        }
        //Draw the Select Move Texture
        if (moveOption == 0) {
            game.getBatch().draw(moveSelectedTexture, 20, 1080, 376, 84);
        } else if (moveOption == 1) {
            game.getBatch().draw(moveSelectedTexture, 401, 1080, 376, 84);
        } else if (moveOption == 2) {
            game.getBatch().draw(moveSelectedTexture, 20, 991, 376, 84);
        } else if (moveOption == 3) {
            game.getBatch().draw(moveSelectedTexture, 401, 991, 376, 84);
        }

    }

    private void drawHealthBars() {
        int playerCurrentHealth = playerPokemon.getPokemon().getUniqueVariables().getCurrentHealth();
        int playerTotalHealth = playerPokemon.getPokemon().getHealthStat();
        //Draw User Health Bar
        game.getBatch().draw(userHealthBar, 432,1266);
        //Draw Health Amount
        healthFont.draw(game.getBatch(), Integer.toString(playerTotalHealth), 885, 1329);
        if (playerCurrentHealth >= 100) {
            healthFont.draw(game.getBatch(), Integer.toString(playerCurrentHealth), 740, 1329);
        } else if (playerCurrentHealth >= 10) {
            healthFont.draw(game.getBatch(), Integer.toString(playerCurrentHealth), 775, 1329);
        } else {
            healthFont.draw(game.getBatch(), Integer.toString(playerCurrentHealth), 805, 1329);
        }

        //Draw Health Bars
        float playerHealthRatio = ((float)playerPokemon.getPokemon().getCurrentHealth() / playerPokemon.getPokemon().getHealthStat());
        float enemyHealthRatio = ((float)enemyPokemon.getPokemon().getCurrentHealth() / enemyPokemon.getPokemon().getHealthStat());
        Texture playerHealthTexture;
        Texture enemyHealthTexture;
        if (playerHealthRatio > 0.5) {
            playerHealthTexture = greenHealth;
        } else if (playerHealthRatio >= 0.15) {
            playerHealthTexture = yellowHealth;
        } else {
            playerHealthTexture = redHealth;
        }

        if (enemyHealthRatio > 0.5) {
            enemyHealthTexture = greenHealth;
        } else if (enemyHealthRatio >= 0.15) {
            enemyHealthTexture = yellowHealth;
        } else {
            enemyHealthTexture = redHealth;
        }
            game.getBatch().draw(playerHealthTexture, 711, 1337, Math.round(263 * playerHealthRatio), 15);

        game.getBatch().draw(enemyHealthTexture, 221, 1817, Math.round(263 * enemyHealthRatio), 15);
    }

    private void drawStatusEffects() {
        //game.getBatch().draw(statusEffectAtlas.findRegion("poison"), 35, 1800, 88, 32);
        if (playerPokemon.getPokemon().getUniqueVariables().getStatus() == StatusCondition.POISON) {
            game.getBatch().draw(statusEffectAtlas.findRegion("poison"), 525, 1323, 88, 32);
        } else if (playerPokemon.getPokemon().getUniqueVariables().getStatus() == StatusCondition.BURN) {
            game.getBatch().draw(statusEffectAtlas.findRegion("burn"), 525, 1323, 88, 32);
        } else if (playerPokemon.getPokemon().getUniqueVariables().getStatus() == StatusCondition.PARALYSIS) {
            game.getBatch().draw(statusEffectAtlas.findRegion("paralysis"), 525, 1323, 88, 32);
        } else if (playerPokemon.getPokemon().getUniqueVariables().getStatus() == StatusCondition.FROZEN) {
            game.getBatch().draw(statusEffectAtlas.findRegion("frozen"), 525, 1323, 88, 32);
        } else if (playerPokemon.getPokemon().getUniqueVariables().getStatus() == StatusCondition.SLEEP) {
            game.getBatch().draw(statusEffectAtlas.findRegion("sleep"), 525, 1323, 88, 32);
        }
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

