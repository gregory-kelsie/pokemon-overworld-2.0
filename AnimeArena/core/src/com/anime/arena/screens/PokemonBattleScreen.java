package com.anime.arena.screens;

import com.anime.arena.AnimeArena;
import com.anime.arena.animation.AbilityTransition;
import com.anime.arena.animation.OpenBattleTransition;
import com.anime.arena.battle.BattleStateManager;
import com.anime.arena.battle.InitState;
import com.anime.arena.battle.SendOutPokemonState;
import com.anime.arena.battle.ui.BattleTextBox;
import com.anime.arena.battle.ui.PlayerBattleAnimation;
import com.anime.arena.battle.ui.SendOutPokeball;
import com.anime.arena.battle.ui.UIComponent;
import com.anime.arena.field.Field;
import com.anime.arena.field.WeatherType;
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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private Texture textBox;
    private Texture maleTexture;
    private Texture femaleTexture;
    private Texture megaEvolutionIcon;
    private Texture dynamaxIcon;
    private Texture expBar;
    private Texture expBarBg;
    private Texture blueExp;
    private Texture userHealthBar;
    private Texture enemyHealthBar;
    private Texture greenHealth;
    private Texture redHealth;
    private Texture yellowHealth;
    private Texture battleMessage;
    private Texture ppBox;
    private Texture moveSelectedTexture;
    private Texture fightButtonBox;
    private Texture playerAbilityMessageTexture;
    private Texture opponentAbilityMessageTexture;

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
    private TextureAtlas pokemonIconAtlas;
    private TextureAtlas pokemonBackAtlas;
    private TextureAtlas moveButtonAtlas;
    private TextureAtlas stageBannerAtlas;
    private TextureAtlas statusEffectAtlas;
    private TextureAtlas fightButtonAtlas;
    private TextureAtlas categoryAtlas;
    private TextureAtlas pokemonTypeAtlas;
    private TextureAtlas bodyAtlas;
    private TextureAtlas pokeballAtlas;
    private TextureAtlas weatherAtlas;
    private TextureAtlas terrainAtlas;

    private Sprite firstMoveSprite;
    private Sprite secondMoveSprite;
    private Sprite thirdMoveSprite;
    private Sprite fourthMoveSprite;
    private Sprite currentMoveTypeSprite;
    private Sprite currentCategorySprite;

    //Field Icons
    private Texture safeguard;
    private Texture stickyWeb;
    private Texture lightScreen;
    private Texture toxicSpikes;
    private Texture spikes;
    private Texture stealthRock;
    private Texture reflect;
    private Texture tailwind;
    private Texture mist;

    private BitmapFont regularFont;
    private BitmapFont abilityMessageFont;
    private BitmapFont levelFont;
    private BitmapFont healthFont;
    private BitmapFont moveFont;
    private BattleTextBox battleTextBox;
    private List<Pokemon> party;
    private Field field;

    private int optionUI;
    private int moveOption;

    private int currentPP;
    private int totalPP;

    //Battle Variables
    private float enemyHealthPanelOffset;
    private float enemySpriteYOffset;
    private float playerHealthPanelOffset;


    private BattleStateManager bsm;
    private OpenBattleTransition openBattleTransition;
    private PlayerBattleAnimation playerBattleAnimation;
    private AbilityTransition abilityTransition;
    private AbilityTransition opponentAbilityTransition;
    private SendOutPokeball pokeballAnimation;

    private PlayScreen previousScreen;
    private int loadedPokemon; //0 not loaded, 1 = success, 2 = failed
    private enum SwitchState  {NONE, CALL_BACK, REMOVE_PLAYER_SPRITE, ANNOUNCE_NEW_POKEMON, THROW_BALL, DISPLAY_NEW_POKEMON, ANIMATE_HEALTH_BAR, DONE};
    //UI Visibility Variables
    private static final boolean DISPLAY_BATTLE_STAGES = true;
    private PokemonBattleScreen.SwitchState currentSwitchState;

    private boolean tempFaintingVariable;
    private int battlePokemonPosition; //which position in the party are they
    private int switchedPokemonPartyPosition;
    private SendOutPokemonState.SendOutPokemonReason sendOutPokemonReason;
    private final int enemyPokemonFinalX = 591;
    private final int enemyPokemonFinalY = 1490;
    private final float FINAL_ENEMY_HEALTH_PANEL_OFFSET = 577.0f;
    private final float FINAL_PLAYER_HEALTH_PANEL_OFFSET = 648.0f;
    private final float FINAL_ENEMY_Y_FAINTED_POSITION = 1320.0f;
    private final float REGULAR_ENEMY_Y_POSITION = 1490.0f;
    public PokemonBattleScreen(AnimeArena game, PlayScreen previousScreen, Pokemon wildPokemon, String battleBackground) {
        this(game);
        initBattleBackground(battleBackground);
        this.basePokemonFactory = previousScreen.getBasePokemonFactory();
        this.previousScreen = previousScreen;
        this.party = previousScreen.getPlayer().getPokemonParty();
        this.enemyPokemon = new BattlePokemon(wildPokemon, pokemonAtlas, false);
        getPokedex();
        field = new Field();
        currentMoveTypeSprite = createMoveTypeIcon(playerPokemon.getFirstMove().getMoveType());
        currentCategorySprite = new Sprite(categoryAtlas.findRegion("physical"));
        currentCategorySprite.setSize(500, 500);
        currentCategorySprite.setPosition(100, 100);
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



        initCamera();

        gameCam.position.set((AnimeArena.V_WIDTH / 2) / AnimeArena.PPM, (AnimeArena.V_HEIGHT / 2) / AnimeArena.PPM, 0);

        initBattleVariables();

        openBattleTransition = new OpenBattleTransition();
        initTextures();
        initFonts();
        initStartingAnimation();


    }

    private void initBattleBackground(String battleBackground) {
        if (battleBackground.equals("default")) {
            texture = new Texture("battle/background/gym.png");
        } else if (battleBackground.equals("route")) {
            texture = new Texture("battle/background/route.png");
        } else {
            texture = new Texture("battle/background/gym.png");
        }
    }

    private void initBattleVariables() {
        this.tempFaintingVariable = false;
        this.loadedPokemon = 0;
        this.switchedPokemonPartyPosition = 0; //No one switching atm
        this.sendOutPokemonReason = null;
        this.currentSwitchState = PokemonBattleScreen.SwitchState.NONE;

        enemyHealthPanelOffset = 0;
        enemySpriteYOffset = 0;
        playerHealthPanelOffset = 0;

        optionUI = 0; //Fight buttons
        moveOption = 0;
    }

    private void initBattlePokemonPosition() {
        for (int i = 0; i < party.size(); i++) {
            if (!party.get(i).isFainted()) {
                battlePokemonPosition = i;
            }
        }
    }

    private void initTextures() {
        this.pokemonAtlas = new TextureAtlas("sprites/pokemon/PokemonFront.atlas");
        this.pokemonBackAtlas = new TextureAtlas("sprites/pokemon/PokemonBack.atlas");
        this.pokemonIconAtlas = new TextureAtlas("sprites/pokemon/PokemonIcon.atlas");
        this.moveButtonAtlas = new TextureAtlas("battle/ui/movebuttons/MoveButtons.atlas");
        this.stageBannerAtlas = new TextureAtlas("battle/ui/stageBanners/StageBanner.atlas");
        this.pokemonTypeAtlas = new TextureAtlas("sprites/PokemonTypes.atlas");
        this.statusEffectAtlas = new TextureAtlas("battle/ui/statusEffects/StatusEffects.atlas");
        this.fightButtonAtlas = new TextureAtlas("battle/ui/fightbuttons/FightButtons.atlas");
        this.terrainAtlas = new TextureAtlas("battle/ui/terrain/terrain.atlas");
        this.weatherAtlas = new TextureAtlas("battle/ui/weather/weather.atlas");
        this.bodyAtlas = new TextureAtlas("sprites/player/battle/base/Base.atlas");
        this.pokeballAtlas = new TextureAtlas("battle/ui/pokeballs/pokeball/Greatball.atlas");
        this.categoryAtlas = new TextureAtlas("battle/ui/category/Category.atlas");

        black = new Texture("animation/black.png");
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
        currentCategorySprite = new Sprite(categoryAtlas.findRegion("physical"));
        currentCategorySprite.setSize(500, 500);
        currentCategorySprite.setPosition(100, 100);
        userHealthBar = new Texture("battle/ui/userHealthBar.png");
        expBar = new Texture("battle/ui/expBar.png");
        expBarBg = new Texture("battle/ui/expBarBg.png");
        enemyHealthBar = new Texture("battle/ui/enemyHealthBar.png");
        greenHealth = new Texture("battle/ui/greenHealth.png");
        yellowHealth = new Texture("battle/ui/yellowHealth.png");
        blueExp = new Texture("battle/ui/blueExp.png");
        redHealth = new Texture("battle/ui/redHealth.png");
        fightButtonBox = new Texture("battle/ui/movebuttonbox.png");
        playerAbilityMessageTexture = new Texture("battle/ui/abilityMessagePlayer.png");
        opponentAbilityMessageTexture = new Texture("battle/ui/abilityMessageOpponent.png");

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
    }

    private void initFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pkmnems.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        parameter.borderWidth = 4;
        parameter.borderColor = Color.BLACK;
        parameter.color = Color.WHITE;
        parameter.spaceY = 20;
        parameter.spaceX = -4;

        FreeTypeFontGenerator.FreeTypeFontParameter abilityMessageParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        abilityMessageParam.size = 40;
        abilityMessageParam.borderWidth = 4;
        abilityMessageParam.borderColor = Color.BLACK;
        abilityMessageParam.color = Color.WHITE;
        abilityMessageParam.spaceY = 20;
        abilityMessageParam.spaceX = -4;

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
        abilityMessageFont = generator.generateFont(abilityMessageParam);
        levelFont = generator.generateFont(parameter);
        healthFont = generator2.generateFont(hpFontParam);
        moveFont = moveGenerator.generateFont(moveParameter);
    }

    private void getPokedex() {

        currentPokemon = basePokemonFactory.createBasePokemon(1);
        Gdx.app.log("JSONRESP", enemyPokemon.getPokemon().getConstantVariables().getFormattedImage());
        pokemonSprite = new Sprite(pokemonAtlas.findRegion(enemyPokemon.getPokemon().getConstantVariables().getFormattedImage()));
        pokemonSprite.setSize(396, 396);
        pokemonSprite.setPosition(591,1490);
        initBattlePokemonPosition();
        playerPokemon = new BattlePokemon(party.get(battlePokemonPosition), pokemonBackAtlas, true);
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
        return new DamageSkill(0, name, description, category, tacklePP, tacklePP, accuracy, moveType, target, subtype,
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
        return new DamageSkill(0, name, description, category, tacklePP, tacklePP, accuracy, moveType, target, subtype,
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
        return new DamageSkill(0, name, description, category, tacklePP, tacklePP, accuracy, moveType, target, subtype,
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

    /**
     * Initialize the starting animation where the player throws out their first pokemon
     */
    private void initStartingAnimation() {
        openBattleTransition = new OpenBattleTransition();
        abilityTransition = new AbilityTransition(true);
        opponentAbilityTransition = new AbilityTransition(false);
        playerBattleAnimation = new PlayerBattleAnimation(bodyAtlas, "male", "base-light");
        playerBattleAnimation.setSize(350, 392);
        playerBattleAnimation.setX(1080);
        playerBattleAnimation.setY(1208);

        pokeballAnimation = new SendOutPokeball(pokeballAtlas);
        pokeballAnimation.setSize(54, 84);
        pokeballAnimation.setX(20);
        pokeballAnimation.setY(1200);
    }

    private void updatePlayerSprites() {
        Skill firstMove = playerPokemon.getFirstMove();
        Skill secondMove = playerPokemon.getSecondMove() != null ? playerPokemon.getSecondMove() : null;
        Skill thirdMove = playerPokemon.getThirdMove() != null ? playerPokemon.getThirdMove() : null;
        Skill fourthMove = playerPokemon.getFourthMove() != null ? playerPokemon.getFourthMove() : null;

        firstMoveSprite = new Sprite(moveButtonAtlas.findRegion(firstMove.getMoveType().toString().toLowerCase()));
        firstMoveSprite.setSize(376, 84);
        firstMoveSprite.setPosition(20, 1080);

        updateMoveUI(playerPokemon.getFirstMove());

        if (secondMove != null) {
            secondMoveSprite = new Sprite(moveButtonAtlas.findRegion(secondMove.getMoveType().toString().toLowerCase()));
        } else {
            secondMoveSprite = new Sprite(moveButtonAtlas.findRegion("empty"));
        }
        secondMoveSprite.setSize(376, 84);
        secondMoveSprite.setPosition(401, 1080);

        if (thirdMove != null) {
            thirdMoveSprite = new Sprite(moveButtonAtlas.findRegion(thirdMove.getMoveType().toString().toLowerCase()));
        } else {
            thirdMoveSprite = new Sprite(moveButtonAtlas.findRegion("empty"));
        }
        thirdMoveSprite.setSize(376, 84);
        thirdMoveSprite.setPosition(20, 991);

        if (fourthMove != null) {
            fourthMoveSprite = new Sprite(moveButtonAtlas.findRegion(fourthMove.getMoveType().toString().toLowerCase()));
        } else {
            fourthMoveSprite = new Sprite(moveButtonAtlas.findRegion("empty"));
        }
        fourthMoveSprite.setSize(376, 84);
        fourthMoveSprite.setPosition(401, 991);
    }

    private void updateMoveUI(Skill s) {
        if (s != null) {
            currentPP = s.getCurrentPP();
            totalPP = s.getMaxPP();
            currentMoveTypeSprite = createMoveTypeIcon(s.getMoveType());
            currentCategorySprite = createCategoryIcon(s.getCategory());
        }
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

    public Sprite createCategoryIcon(SkillCategory category) {
        Sprite s;
        if (category == SkillCategory.PHYSICAL) {
            s = new Sprite(categoryAtlas.findRegion("physical"));
        } else if (category == SkillCategory.SPECIAL) {
            s = new Sprite(categoryAtlas.findRegion("special"));
        } else {
            s = s = new Sprite(categoryAtlas.findRegion("misc"));
        }
        s.setSize(112, 48);
        s.setPosition(868, 1010);
        return s;
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

    public BasePokemonFactory getBasePokemonFactory() {
        return basePokemonFactory;
    }

    @Override
    public void show() {

    }

    private void initializeEnemyPokemonSpritePosition(float dt) {
        float newX = enemyPokemon.getSprite().getX() + (dt * 550);
        enemyPokemon.getSprite().setX(Math.min(enemyPokemonFinalX, newX));
    }

    private boolean isEnemyPokemonSpriteInPosition() {
        return enemyPokemon.getSprite().getX() == enemyPokemonFinalX;
    }

    /**
     * When the enemy health panel is sliding in on battle start. The position of the panel needs to get updated.
     * @param dt
     */
    private void updateEnemyHealthPanelOffset(float dt) {
        if (enemyHealthPanelOffset < FINAL_ENEMY_HEALTH_PANEL_OFFSET) {
            enemyHealthPanelOffset += (dt * 1200);
            enemyHealthPanelOffset = Math.min(FINAL_ENEMY_HEALTH_PANEL_OFFSET, enemyHealthPanelOffset);
        }
    }

    private void updateFaintingEnemySpritePosition(float dt) {
        float newY = enemyPokemon.getSprite().getY() - (dt * 1000);
        enemyPokemon.getSprite().setY(Math.max(FINAL_ENEMY_Y_FAINTED_POSITION, newY));
    }

    private void updatePlayerHealthPanelOffset(float dt) {
        if (playerHealthPanelOffset < FINAL_PLAYER_HEALTH_PANEL_OFFSET) {
            playerHealthPanelOffset += (dt * 1200);
            playerHealthPanelOffset = Math.min(FINAL_PLAYER_HEALTH_PANEL_OFFSET, playerHealthPanelOffset);
        }
    }

    private void initializeAbilityTransition() {
        abilityTransition.setUserName("Helioptile");
        abilityTransition.setAbilityName("Intimidate");
        abilityTransition.popupAbilityDisplay(0.15f);
    }

    private void initializeFirstAbilityEffect() {
        battleTextBox = new BattleTextBox("Persian's attack fell!");
        battleTextBox.setFinishType(BattleTextBox.BattleTextBoxFinish.DELAY);
    }

    private void initializeSecondAbilityEffect() {
        battleTextBox = new BattleTextBox("Helioptile's accuracy fell!");
        battleTextBox.setFinishType(BattleTextBox.BattleTextBoxFinish.DELAY);
    }

    private void initializeCounterAbilityTransition() {
        opponentAbilityTransition.setUserName("Persian");
        opponentAbilityTransition.setAbilityName("Clear Body");
        opponentAbilityTransition.popupAbilityDisplay(0.0f);
    }

    private void initializeCounterAbilityEffect() {
        battleTextBox = new BattleTextBox("Persian's stats were not lowered!");
        battleTextBox.setFinishType(BattleTextBox.BattleTextBoxFinish.DELAY);
    }

    private void updateOpponentSendOutAbility(float dt) {
        boolean hasCounterAbility = false;
        boolean hasSendOutAbility = false;
        if (hasSendOutAbility) {
            if (!opponentAbilityTransition.isDisplaying()) {
                initializeCounterAbilityTransition(); //TODO: Change to use opponent pokemon data
            } else if (opponentAbilityTransition.isDisplaying()) {
                boolean isAlreadyPoppedUp = opponentAbilityTransition.isDonePoppingUp();
                opponentAbilityTransition.update(dt);
                if (opponentAbilityTransition.isDonePoppingUp()) {
                    if (!hasCounterAbility) { //If the opponent doesn't have a counter ability, use the ability and then remove it
                        if (!isAlreadyPoppedUp) {
                            initializeSecondAbilityEffect();
                        }
                        battleTextBox.update(dt);
                        //Remove the ability popup when the text box for the effect is finished
                        if (battleTextBox.isFinished()) {
                            opponentAbilityTransition.removeAbilityDisplay();
                        }
                    } else {
                        //Remove the ability popup when there's a counter ability
                        opponentAbilityTransition.removeAbilityDisplay();
                    }
                }
                if (opponentAbilityTransition.isDoneRemovingPopup()) {
                    if (hasCounterAbility) {
                        if (!abilityTransition.isDisplaying()) {
                            initializeAbilityTransition(); //TODO: Change to use user pokemon data
                        } else if (abilityTransition.isDisplaying()) {
                            boolean isUserAbilityAlreadyPoppedUp = abilityTransition.isDonePoppingUp();
                            abilityTransition.update(dt);
                            if (abilityTransition.isDonePoppingUp()) {
                                if (!isUserAbilityAlreadyPoppedUp) {
                                    initializeFirstAbilityEffect();
                                }
                                battleTextBox.update(dt);
                                //Remove the counter ability popup when the text box for the effect is finished
                                if (battleTextBox.isFinished()) {
                                    abilityTransition.removeAbilityDisplay();
                                }
                            }
                            if (abilityTransition.isDoneRemovingPopup()) {
                                //Close the text box when the ability popup has been fully removed from the screen
                                battleTextBox.triggerClosingTextBox();
                                loadedPokemon = 6;
                            }
                        }
                    } else {
                        loadedPokemon = 6;
                    }
                }
            }
        } else {
            loadedPokemon = 6;
        }
    }

    private void updatePlayerSendOutAbility(float dt) {
        boolean hasCounterAbility = false;
        boolean hasSendOutAbility = false;
        if (hasSendOutAbility) {
            if (!abilityTransition.isDisplaying()) {
                initializeAbilityTransition();
            } else if (abilityTransition.isDisplaying()) {
                boolean isAlreadyPoppedUp = abilityTransition.isDonePoppingUp();
                abilityTransition.update(dt);
                if (abilityTransition.isDonePoppingUp()) {
                    if (!hasCounterAbility) { //If the opponent doesn't have a counter ability, use the ability and then remove it
                        if (!isAlreadyPoppedUp) {
                            initializeFirstAbilityEffect();
                        }
                        battleTextBox.update(dt);
                        //Remove the ability popup when the text box for the effect is finished
                        if (battleTextBox.isFinished()) {
                            abilityTransition.removeAbilityDisplay();
                        }
                    } else {
                        //Remove the ability popup when there's a counter ability
                        abilityTransition.removeAbilityDisplay();
                    }
                }
                if (abilityTransition.isDoneRemovingPopup()) {
                    if (hasCounterAbility) {
                        if (!opponentAbilityTransition.isDisplaying()) {
                            initializeCounterAbilityTransition();
                        } else if (opponentAbilityTransition.isDisplaying()) {
                            boolean isOpponentAbilityAlreadyPoppedUp = opponentAbilityTransition.isDonePoppingUp();
                            opponentAbilityTransition.update(dt);
                            if (opponentAbilityTransition.isDonePoppingUp()) {
                                if (!isOpponentAbilityAlreadyPoppedUp) {
                                    initializeCounterAbilityEffect();
                                }
                                battleTextBox.update(dt);
                                //Remove the counter ability popup when the text box for the effect is finished
                                if (battleTextBox.isFinished()) {
                                    opponentAbilityTransition.removeAbilityDisplay();
                                }
                            }
                            if (opponentAbilityTransition.isDoneRemovingPopup()) {
                                //Close the text box when the ability popup has been fully removed from the screen
                                battleTextBox.triggerClosingTextBox();
                                completeFirstSendOutAbility();
                            }
                        }
                    } else {
                        completeFirstSendOutAbility();
                    }
                }
            }
        } else {
            completeFirstSendOutAbility();
        }
    }

    private void completeFirstSendOutAbility() {
        loadedPokemon = 5;
        abilityTransition.resetTransition();
        opponentAbilityTransition.resetTransition();
        battleTextBox.reset();
    }

    private void executeMainUpdate(float dt) {
        handleInput(dt);
        if (bsm != null) {
            bsm.update(dt);
            if (bsm.isReturningToFightOptions()) {
                moveOption = 0;
                optionUI = 0;
                updateMoveUI(playerPokemon.getFirstMove());
                bsm.setReturningToFightOptions(false);
            } else if (bsm.isExitingBattle()) {
                if (previousScreen != null) {
                    previousScreen.removeBlackScreen();
                    game.setScreen(previousScreen);
                    previousScreen.stopBgm();
                    previousScreen.playMapBgm();
                }
            }
        }
    }

    public void update(float dt) {
        if (openBattleTransition.isFinished()) {
            if (loadedPokemon < 3) {
                updatePlayerSprites();
                BasePokemon enemyBasePokemon = enemyPokemon.getPokemon().getConstantVariables();
                pokemonSprite = new Sprite(pokemonAtlas.findRegion(enemyBasePokemon.getFormattedImage()));
                enemyPokemon.getSprite().setSize(396, 396);
                enemyPokemon.getSprite().setPosition(-350, REGULAR_ENEMY_Y_POSITION);

                bsm = new BattleStateManager(this, party, playerPokemon, playerPokemon.getFirstMove(), enemyPokemon, enemyPokemon.getFirstMove(), field);
                bsm.setState(new SendOutPokemonState(bsm, playerPokemon, enemyPokemon, field));
                loadedPokemon = 3;
                battleTextBox = new BattleTextBox("A wild " + enemyPokemon.getName() + " appeared!");
                playerBattleAnimation.start();
            }
            if (loadedPokemon == 3) {
                playerBattleAnimation.update(dt);
                if (!isEnemyPokemonSpriteInPosition() && playerBattleAnimation.getState() < 1) {
                    initializeEnemyPokemonSpritePosition(dt);
                } else if (playerBattleAnimation.getState() == 1) {
                    handleInput(dt);
                    battleTextBox.update(dt);
                    updateEnemyHealthPanelOffset(dt);
                    if (enemyHealthPanelOffset == FINAL_ENEMY_HEALTH_PANEL_OFFSET && battleTextBox.isFinished()) {
                        //When clicked, change the text box from A wild x appeared to Go x!!
                        battleTextBox = new BattleTextBox("Go " + playerPokemon.getName() + "!!");
                        battleTextBox.setFinishType(BattleTextBox.BattleTextBoxFinish.TRIGGER);
                        playerBattleAnimation.setState(2);
                    }
                } else if (playerBattleAnimation.getState() == 2) {
                    battleTextBox.update(dt);
                    if (playerBattleAnimation.isAnimationFinished()) {
                        pokeballAnimation.update(dt);
                        if (playerBattleAnimation.getX() <= -350 && pokeballAnimation.isFinished()) {
                            playerBattleAnimation.setState(3);
                            updatePlayerHealthPanelOffset(dt);
                            if (playerHealthPanelOffset == FINAL_PLAYER_HEALTH_PANEL_OFFSET) {
                                loadedPokemon = 4;
                                playerBattleAnimation.setState(4);
                            }
                        }
                    }
                } else if (playerBattleAnimation.getState() == 3) {
                    updatePlayerHealthPanelOffset(dt);
                    if (playerHealthPanelOffset == FINAL_PLAYER_HEALTH_PANEL_OFFSET) {
                        loadedPokemon = 4;
                        playerBattleAnimation.setState(4);
                    }
                } else if (playerBattleAnimation.getState() == 4) {
                    loadedPokemon = 4;
                }
            }
            if (loadedPokemon == 4) { //Player Ability Send Out
                updatePlayerSendOutAbility(dt);
            }
            if (loadedPokemon == 5) { //Enemy Ability Send Out
                updateOpponentSendOutAbility(dt);
            }
            if (loadedPokemon == 6) {
                executeMainUpdate(dt);
                if (tempFaintingVariable) {
                    updateFaintingEnemySpritePosition(dt);
                }
            }
            //handleInput(dt);
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
            if (bsm == null || (bsm != null && bsm.getCurrentState() == null) || playerBattleAnimation.getState() == 1) {
                if (loadedPokemon < 4) {
                    if (playerBattleAnimation.getState() == 1) {
                        battleTextBox.clicked();
                    }
                } else {
                    if (optionUI == 1) {
                        if (moveOption == 0) {
                            bsm = new BattleStateManager(this, party, playerPokemon, playerPokemon.getFirstMove(), enemyPokemon, enemyPokemon.getFirstMove(), field);
                            bsm.setState(new InitState(bsm, playerPokemon, playerPokemon.getFirstMove(), enemyPokemon, enemyPokemon.getFirstMove()));
                            playerPokemon.getFirstMove().decreasePP();
                        } else if (moveOption == 1 && playerPokemon.getSecondMove() != null) {
                            bsm = new BattleStateManager(this, party, playerPokemon, playerPokemon.getSecondMove(), enemyPokemon, enemyPokemon.getFirstMove(), field);
                            bsm.setState(new InitState(bsm, playerPokemon, playerPokemon.getSecondMove(), enemyPokemon, enemyPokemon.getFirstMove()));
                            playerPokemon.getSecondMove().decreasePP();
                        } else if (moveOption == 2 && playerPokemon.getThirdMove() != null) {
                            bsm = new BattleStateManager(this, party, playerPokemon, playerPokemon.getThirdMove(), enemyPokemon, enemyPokemon.getFirstMove(), field);
                            bsm.setState(new InitState(bsm, playerPokemon, playerPokemon.getThirdMove(), enemyPokemon, enemyPokemon.getFirstMove()));
                            playerPokemon.getThirdMove().decreasePP();
                        } else if (moveOption == 3 && playerPokemon.getFourthMove() != null) {
                            bsm = new BattleStateManager(this, party, playerPokemon, playerPokemon.getFourthMove(), enemyPokemon, enemyPokemon.getFirstMove(), field);
                            bsm.setState(new InitState(bsm, playerPokemon, playerPokemon.getFourthMove(), enemyPokemon, enemyPokemon.getFirstMove()));
                            playerPokemon.getFourthMove().decreasePP();
                        }
                    } else if (optionUI == 0) {
                        if (moveOption == 0) {
                            optionUI = 1;
                        } else if (moveOption == 3) {
                            if (!tempFaintingVariable) {
                                tempFaintingVariable = true;
                            }
                            if (previousScreen != null) {
                                previousScreen.removeBlackScreen();
                                game.setScreen(previousScreen);
                                previousScreen.stopBgm();
                                previousScreen.playMapBgm();
                            }
                        } else if (moveOption == 2) {
                            PokemonScreen pokemonScreen = new PokemonScreen(game, this, party, pokemonAtlas, pokemonIconAtlas, pokemonTypeAtlas, basePokemonFactory, SourceScreen.BATTLE);
                            pokemonScreen.setBattlePokemonPosition(battlePokemonPosition);
                            game.setScreen(pokemonScreen);
                        }
                    }
                }
            } else if (bsm.getCurrentState() != null) {
                UIComponent uiComponent = bsm.getCurrentState().getUiComponent();
                if (uiComponent != null) {
                    uiComponent.click();
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

    private void drawMegaEvolutionIcons() {
        //game.getBatch().draw(megaEvolutionIcon, 60, 1843,44,48);
        //game.getBatch().draw(megaEvolutionIcon, 470, 1366,44,48);

    }

    private void drawDynamaxIcons() {
        //game.getBatch().draw(dynamaxIcon, 470, 1366,44,44);
    }

    private void drawPokemonLevels() {
        levelFont.draw(game.getBatch(), Integer.toString(enemyPokemon.getPokemon().getUniqueVariables().getLevel()), 485, 1875);
        levelFont.draw(game.getBatch(), Integer.toString(playerPokemon.getPokemon().getUniqueVariables().getLevel()), 895, 1398);
    }

    private void drawWeatherAndTerrainIcons() {
        if (bsm.getField().getWeatherType() == WeatherType.RAIN) {
            game.getBatch().draw(weatherAtlas.findRegion("rain"), 10, 1680, 116, 64);
        } else if (bsm.getField().getWeatherType() == WeatherType.SUN) {
            game.getBatch().draw(weatherAtlas.findRegion("sunny"), 10, 1680, 116, 64);
        } else if (bsm.getField().getWeatherType() == WeatherType.SAND) {
            game.getBatch().draw(weatherAtlas.findRegion("sandstorm"), 10, 1680, 116, 64);
        } else if (bsm.getField().getWeatherType() == WeatherType.HAIL) {
            game.getBatch().draw(weatherAtlas.findRegion("hail"), 10, 1680, 116, 64);
        }

//        game.getBatch().draw(terrainAtlas.findRegion("none"), 10, 1610, 116, 64);
    }

    private void drawPlayerFieldIcons() {
//        game.getBatch().draw(spikes, 10, 1210, 36, 36);
//        game.getBatch().draw(toxicSpikes, 46, 1210, 36, 36);
//        game.getBatch().draw(safeguard, 82, 1210, 36, 36);
//        game.getBatch().draw(mist, 118, 1210, 36, 36);lk
//        game.getBatch().draw(lightScreen, 154, 1210, 36, 36);
//        game.getBatch().draw(reflect, 190, 1210, 36, 36);
//        game.getBatch().draw(tailwind, 226, 1210, 36, 36);
//        game.getBatch().draw(stickyWeb, 262, 1210, 36, 36);
//        game.getBatch().draw(stealthRock, 298, 1210, 36, 36);
    }

    private void drawOpponentFieldIcons() {
//        game.getBatch().draw(spikes, 1040, 1880, 36, 36);
//        game.getBatch().draw(toxicSpikes, 1004, 1880, 36, 36);
//        game.getBatch().draw(safeguard, 968, 1880, 36, 36);
//        game.getBatch().draw(mist, 932, 1880, 36, 36);
//        game.getBatch().draw(lightScreen, 896, 1880, 36, 36);
//        game.getBatch().draw(reflect, 860, 1880, 36, 36);
//        game.getBatch().draw(tailwind, 824, 1880, 36, 36);
//        game.getBatch().draw(stickyWeb, 788, 1880, 36, 36);
//        game.getBatch().draw(stealthRock, 752, 1880, 36, 36);
    }

    private void drawGenderIcons() {
        game.getBatch().draw(femaleTexture, 810, 1366, 28, 40);
        game.getBatch().draw(maleTexture, 400, 1843, 28, 40);
    }

    private void drawPokemonNames(String playerName, String enemyName) {
        regularFont.draw(game.getBatch(), enemyName, 110, 1875);
        regularFont.draw(game.getBatch(), playerName, 520, 1398);
    }

    private void drawBlueExp(int currentExp, int requiredExp) {
        float expPercentage = (1.0f * currentExp) / requiredExp;
        //expPercentage = 0.7f;
        game.getBatch().draw(blueExp, 1221 - playerHealthPanelOffset, 1266, Math.round(451 * ((expPercentage * 100.0) / 100.0)), 21);
    }

    private void drawHealthBars(BattlePokemon playerPokemon, BattlePokemon enemyPokemon) {
        //Draw User Health Bar
        game.getBatch().draw(userHealthBar, 432,1266); //648 shift

        game.getBatch().draw(expBarBg, 573, 1266);
        drawBlueExp((int)playerPokemon.getPokemon().getUniqueVariables().getCurrentExp(), playerPokemon.getPokemon().getNextLevelExp());
        game.getBatch().draw(expBar, 573, 1266);
        //Draw Health Amount
        int playerCurrentHealth = (int) Math.round(playerPokemon.getAnimationHealth());
        healthFont.draw(game.getBatch(), Integer.toString(playerPokemon.getPokemon().getHealthStat()), 885, 1329);
        if (playerCurrentHealth >= 100) {
            healthFont.draw(game.getBatch(), Integer.toString(playerCurrentHealth), 740, 1329);
        } else if (playerCurrentHealth >= 10) {
            healthFont.draw(game.getBatch(), Integer.toString(playerCurrentHealth), 775, 1329);
        } else {
            healthFont.draw(game.getBatch(), Integer.toString(playerCurrentHealth), 805, 1329);
        }

        //Draw Health Bars
        double playerHealthPercentage = getPercentageHealth(playerPokemon);
        double enemyHealthPercentage = getPercentageHealth(enemyPokemon);
        game.getBatch().draw(getHealthTexture(playerHealthPercentage), 711, 1337, Math.round(263 * (playerHealthPercentage / 100.0)), 15);
        game.getBatch().draw(getHealthTexture(enemyHealthPercentage), 221, 1817, Math.round(263 * (enemyHealthPercentage / 100.0)), 15);
    }

    private double getPercentageHealth(BattlePokemon pokemon) {
        return ((1.0 * pokemon.getAnimationHealth()) / (1.0 * pokemon.getPokemon().getHealthStat())) * 100.0;
    }


    private Texture getHealthTexture(double healthPercentage) {
        if (healthPercentage >= 50.0) {
            return greenHealth;
        } else if (healthPercentage >= 25.0) {
            return yellowHealth;
        } else {
            return redHealth;
        }
    }

    private void drawStatusEffects() {
        StatusCondition enemyStatus = enemyPokemon.getPokemon().getUniqueVariables().getStatus();
        StatusCondition playerStatus = playerPokemon.getPokemon().getUniqueVariables().getStatus();
        if (isDisplayableStatus(enemyStatus)) {
            game.getBatch().draw(getStatus(enemyStatus), 35, 1800, 88, 32);
        }
        if (isDisplayableStatus(playerStatus)) {
            game.getBatch().draw(getStatus(playerStatus), 525, 1323, 88, 32);
        }
    }

    private boolean isDisplayableStatus(StatusCondition status) {
        if (status == StatusCondition.BURN || status == StatusCondition.FROZEN || status == StatusCondition.POISON ||
                status == StatusCondition.PARALYSIS || status == StatusCondition.SLEEP) {
            return true;
        }
        return false;
    }

    private TextureRegion getStatus(StatusCondition statusCondition) {
        if (statusCondition == StatusCondition.BURN) {
            return statusEffectAtlas.findRegion("burn");
        } else if (statusCondition == StatusCondition.POISON) {
            return statusEffectAtlas.findRegion("poison");
        } else if (statusCondition == StatusCondition.FROZEN) {
            return statusEffectAtlas.findRegion("frozen");
        } else if (statusCondition == StatusCondition.SLEEP) {
            return statusEffectAtlas.findRegion("sleep");
        } else if (statusCondition == StatusCondition.PARALYSIS) {
            return statusEffectAtlas.findRegion("paralysis");
        }
        return null;
    }

    //healthPanel, healthBar, statusCondition, gender, name, level
    private void drawEnemyHealthPanel(BattlePokemon enemyPokemon) {
        game.getBatch().draw(enemyHealthBar, -562 + enemyHealthPanelOffset, 1779); // move 562 + 15 = move left 577 pixels
        double enemyHealthPercentage = getPercentageHealth(enemyPokemon);
        game.getBatch().draw(getHealthTexture(enemyHealthPercentage), -356 + enemyHealthPanelOffset, 1817, Math.round(263 * (enemyHealthPercentage / 100.0)), 15);
        StatusCondition enemyStatus = enemyPokemon.getPokemon().getUniqueVariables().getStatus();
        if (isDisplayableStatus(enemyStatus)) {
            game.getBatch().draw(getStatus(enemyStatus), -542 + enemyHealthPanelOffset, 1800, 88, 32);
        }
        game.getBatch().draw(maleTexture, -177 + enemyHealthPanelOffset, 1843, 28, 40);
        regularFont.draw(game.getBatch(), enemyPokemon.getName(), -467 + enemyHealthPanelOffset, 1875);
        levelFont.draw(game.getBatch(), Integer.toString(enemyPokemon.getPokemon().getUniqueVariables().getLevel()), -92 + enemyHealthPanelOffset, 1875);
    }

    private void drawPlayerHealthPanel(BattlePokemon userPokemon) {
        game.getBatch().draw(userHealthBar, 1080 - playerHealthPanelOffset,1266); //648 shift
        game.getBatch().draw(expBarBg, 1221 - playerHealthPanelOffset, 1266);
        drawBlueExp((int)userPokemon.getPokemon().getUniqueVariables().getCurrentExp(), userPokemon.getPokemon().getNextLevelExp());
        game.getBatch().draw(expBar, 1221 - playerHealthPanelOffset, 1266);
        //Draw Health Amount
        int playerCurrentHealth = (int) Math.round(userPokemon.getAnimationHealth());
        healthFont.draw(game.getBatch(), Integer.toString(userPokemon.getPokemon().getHealthStat()), 1533 - playerHealthPanelOffset, 1329);
        if (playerCurrentHealth >= 100) {
            healthFont.draw(game.getBatch(), Integer.toString(playerCurrentHealth), 1388 - playerHealthPanelOffset, 1329);
        } else if (playerCurrentHealth >= 10) {
            healthFont.draw(game.getBatch(), Integer.toString(playerCurrentHealth), 1423 - playerHealthPanelOffset, 1329);
        } else {
            healthFont.draw(game.getBatch(), Integer.toString(playerCurrentHealth), 1453 - playerHealthPanelOffset, 1329);
        }
        double playerHealthPercentage = getPercentageHealth(userPokemon);
        game.getBatch().draw(getHealthTexture(playerHealthPercentage), 1359 - playerHealthPanelOffset, 1337, Math.round(263 * (playerHealthPercentage / 100.0)), 15);
        StatusCondition playerStatus = playerPokemon.getPokemon().getUniqueVariables().getStatus();
        if (isDisplayableStatus(playerStatus)) {
            game.getBatch().draw(getStatus(playerStatus), 1173 - playerHealthPanelOffset, 1323, 88, 32);
        }
        regularFont.draw(game.getBatch(), userPokemon.getName(), 1168 - playerHealthPanelOffset, 1398);
        //draw gender
        game.getBatch().draw(femaleTexture, 1458 - playerHealthPanelOffset, 1366, 28, 40);
        levelFont.draw(game.getBatch(), Integer.toString(playerPokemon.getPokemon().getUniqueVariables().getLevel()), 1543 - playerHealthPanelOffset, 1398);
    }

    private void drawFightOptions() {
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
        moveFont.draw(game.getBatch(), playerPokemon.getName() + " do?", 60, 1065);
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

        if (loadedPokemon < 3) {
            game.getBatch().draw(textBox, 0, 960, 1080, 260);
        } else if (loadedPokemon >= 3 && loadedPokemon < 6) {
            playerBattleAnimation.draw(game.getBatch());
            game.getBatch().draw(textBox, 0, 960, 1080, 260);
            if (pokemonSprite != null) {
                enemyPokemon.draw(game.getBatch());
                drawEnemyHealthPanel(enemyPokemon);
            }
            if (playerBattleAnimation.getState() == 1) {
                battleTextBox.render(game.getBatch());
            } else if (playerBattleAnimation.getState() == 2) {
                battleTextBox.render(game.getBatch());
                if (playerBattleAnimation.isAnimationFinished()) {
                    pokeballAnimation.draw(game.getBatch());
                }
            } else if (playerBattleAnimation.getState() == 3) {
                battleTextBox.render(game.getBatch());
                playerPokemon.draw(game.getBatch());
                drawPlayerHealthPanel(playerPokemon);

            }
            if (loadedPokemon > 3) {
                if (pokemonSprite != null) {
                    pokemonSprite.draw(game.getBatch());
                    game.getBatch().draw(enemyHealthBar, 15, 1779);
                    drawPokemonNames(playerPokemon.getName(), enemyPokemon.getName());
                    drawGenderIcons();
                    drawMegaEvolutionIcons();
                    drawDynamaxIcons();
                    drawPokemonLevels();
                    drawWeatherAndTerrainIcons();
                    drawPlayerFieldIcons();
                    drawOpponentFieldIcons();
                }

                playerPokemon.draw(game.getBatch());
                drawHealthBars(playerPokemon, enemyPokemon);
                drawBattleStages();
                drawStatusEffects();

                if (abilityTransition.isDisplaying()) {
                    abilityTransition.render(game.getBatch());
                    battleTextBox.render(game.getBatch());
                }
                if (opponentAbilityTransition.isDisplaying()) {
                    opponentAbilityTransition.render(game.getBatch());
                    battleTextBox.render(game.getBatch());
                }
            }
        } else if (loadedPokemon == 6) { //battling or switching
            if (pokemonSprite != null) {
                if (pokemonSprite.getY() != FINAL_ENEMY_Y_FAINTED_POSITION) {
                    enemyPokemon.draw(game.getBatch());
                }
                game.getBatch().draw(enemyHealthBar, 15, 1779);
                drawPokemonNames(playerPokemon.getName(), enemyPokemon.getName());
                drawGenderIcons();
                drawMegaEvolutionIcons();
                drawDynamaxIcons();
                drawPokemonLevels();
                drawWeatherAndTerrainIcons();
                drawPlayerFieldIcons();
                drawOpponentFieldIcons();
            }
            playerPokemon.draw(game.getBatch());
            drawHealthBars(playerPokemon, enemyPokemon);
            drawBattleStages();
            drawStatusEffects();
            if (optionUI == 1) {
                drawMoves();
            } else if (optionUI == 0) {
                drawFightOptions();
            }

            if (abilityTransition.isDisplaying()) {
                abilityTransition.render(game.getBatch());
            }

            if (bsm != null && bsm.hasState()) {
                game.getBatch().draw(textBox, 0, 960, 1080, 260);
            }
            if (bsm != null) {
                UIComponent uiComponent = bsm.getUIComponent();
                if (uiComponent != null) {
                    uiComponent.render(game.getBatch());
                }
            }
    }
// OLD
//
//        if (pokemonSprite != null) {
//            pokemonSprite.draw(game.getBatch());
//            game.getBatch().draw(enemyHealthBar, 15, 1779);
//            regularFont.draw(game.getBatch(), enemyPokemon.getPokemon().getConstantVariables().getName(), 110, 1875);
//            regularFont.draw(game.getBatch(), playerPokemon.getPokemon().getConstantVariables().getName(), 520, 1398);
//            levelFont.draw(game.getBatch(), Integer.toString(enemyPokemon.getPokemon().getUniqueVariables().getLevel()), 485, 1875);
//            levelFont.draw(game.getBatch(), Integer.toString(playerPokemon.getPokemon().getUniqueVariables().getLevel()), 895, 1398);
//
//        }
//        if (playerSprite != null) {
//
//            playerSprite.draw(game.getBatch());
//
//            drawHealthBars();
//            //drawBattleStages();
//            drawStatusEffects();
//            if (optionUI == 1) {
//                drawMoves();
//            } else if (optionUI == 0) {
//                game.getBatch().draw(fightButtonBox, 20, 995,480, 168);
//                if (moveOption == 0) {
//                    game.getBatch().draw(fightButtonAtlas.findRegion("fight-selected"), 520, 1085, 252, 84);
//                } else {
//                    game.getBatch().draw(fightButtonAtlas.findRegion("fight"), 520, 1085, 252, 84);
//                }
//                if (moveOption == 1) {
//                    game.getBatch().draw(fightButtonAtlas.findRegion("bag-selected"), 780, 1085, 252, 84);
//                } else {
//                    game.getBatch().draw(fightButtonAtlas.findRegion("bag"), 780, 1085, 252, 84);
//                }
//                if (moveOption == 2) {
//                    game.getBatch().draw(fightButtonAtlas.findRegion("pokemon-selected"), 520, 990, 252, 84);
//                } else {
//                    game.getBatch().draw(fightButtonAtlas.findRegion("pokemon"), 520, 990, 252, 84);
//                }
//                if (moveOption == 3) {
//                    game.getBatch().draw(fightButtonAtlas.findRegion("run-selected"), 780, 990, 252, 84);
//                } else {
//                    game.getBatch().draw(fightButtonAtlas.findRegion("run"), 780, 990, 252, 84);
//                }
//                moveFont.draw(game.getBatch(), "What will", 60, 1115);
//                moveFont.draw(game.getBatch(), playerPokemon.getPokemon().getConstantVariables().getName() + " do?", 60, 1065);
//            }
//        }
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

    private String getStageString(int stage) {
        if (stage == 1) {
            return "firstStage";
        } else if (stage == 2) {
            return "secondStage";
        } else if (stage == 3) {
            return "thirdStage";
        } else if (stage == 4) {
            return "fourthStage";
        } else if (stage == 5) {
            return "fifthStage";
        } else if (stage == 6) {
            return "sixthStage";
        } else if (stage == -1) {
            return "firstStage-n";
        } else if (stage == -2) {
            return "secondStage-n";
        } else if (stage == -3) {
            return "thirdStage-n";
        } else if (stage == -4) {
            return "fourthStage-n";
        } else if (stage == -5) {
            return "fifthStage-n";
        } else if (stage == -6) {
            return "sixthStage-n";
        } else {
            return "";
        }
    }


    private int getUserBattleStageY(int battleStageCount) {
        if (battleStageCount == 0) {
            return 1500;
        } else if (battleStageCount == 1) {
            return 1467;
        } else if (battleStageCount == 2) {
            return 1436;
        } else if (battleStageCount == 3) {
            return 1403;
        } else if (battleStageCount == 4) {
            return 1371;
        } else if (battleStageCount == 5) {
            return 1339;
        } else {
            return 1307;
        }
    }

    private int getEnemyBattleStageY(int battleStageCount) {
        if (battleStageCount == 0) {
            return 1800;
        } else if (battleStageCount == 1) {
            return 1767;
        } else if (battleStageCount == 2) {
            return 1735;
        } else if (battleStageCount == 3) {
            return 1703;
        } else if (battleStageCount == 4) {
            return 1671;
        } else if (battleStageCount == 5) {
            return 1639;
        } else {
            return 1607;
        }
    }

    private String getPositiveNegative(int stage) {
        if (stage > 0) {
            return "positive";
        } else if (stage < 0) {
            return "negative";
        } else {
            return "";
        }
    }


    private String getPositiveNegativeEnemy(int stage) {
        if (stage > 0) {
            return "positive-f";
        } else if (stage < 0) {
            return "negative-f";
        } else {
            return "";
        }
    }

    private void drawBattleStage(String stageType, int stage, int battleStageCount) {
        String stageStr = getStageString(stage);
        int battleStageBannerY = getUserBattleStageY(battleStageCount);
        String posNeg = getPositiveNegative(stage);
        game.getBatch().draw(stageBannerAtlas.findRegion(stageType), 0, battleStageBannerY, 88, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion(stageStr), 89, battleStageBannerY, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion(posNeg), 125, battleStageBannerY, 36, 32);
    }

    private void drawEnemyBattleStage(String stageType, int stage, int battleStageCount) {
        String stageStr = getStageString(stage);
        int battleStageBannerY = getEnemyBattleStageY(battleStageCount);
        String posNeg = getPositiveNegativeEnemy(stage);
        game.getBatch().draw(stageBannerAtlas.findRegion(stageType), 958, battleStageBannerY, 88, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion(stageStr), 1047, battleStageBannerY, 36, 32);
        game.getBatch().draw(stageBannerAtlas.findRegion(posNeg), 922, battleStageBannerY, 36, 32);
    }

    private void drawBattleStages() {
        if (DISPLAY_BATTLE_STAGES) {
            int battleStageCount = 0;
            if (playerPokemon.getAttackStage() != 0) {
                drawBattleStage("atk", playerPokemon.getAttackStage(), battleStageCount);
                battleStageCount++;
            }
            if (playerPokemon.getDefenseStage() != 0) {
                drawBattleStage("def", playerPokemon.getDefenseStage(), battleStageCount);
                battleStageCount++;
            }
            if (playerPokemon.getSpecialAttackStage() != 0) {
                drawBattleStage("spatk", playerPokemon.getSpecialAttackStage(), battleStageCount);
                battleStageCount++;
            }
            if (playerPokemon.getSpecialDefenseStage() != 0) {
                drawBattleStage("spdef", playerPokemon.getSpecialDefenseStage(), battleStageCount);
                battleStageCount++;
            }
            if (playerPokemon.getSpeedStage() != 0) {
                drawBattleStage("speed", playerPokemon.getSpeedStage(), battleStageCount);
                battleStageCount++;
            }
            if (playerPokemon.getAccuracyStage() != 0) {
                drawBattleStage("accuracy", playerPokemon.getAccuracyStage(), battleStageCount);
                battleStageCount++;
            }
            if (playerPokemon.getEvasionStage() != 0) {
                drawBattleStage("evasion", playerPokemon.getEvasionStage(), battleStageCount);
                battleStageCount++;
            }

            battleStageCount = 0;
            if (enemyPokemon.getAttackStage() != 0) {
                drawEnemyBattleStage("atk", enemyPokemon.getAttackStage(), battleStageCount);
                battleStageCount++;
            }
            if (enemyPokemon.getDefenseStage() != 0) {
                drawEnemyBattleStage("def", enemyPokemon.getDefenseStage(), battleStageCount);
                battleStageCount++;
            }
            if (enemyPokemon.getSpecialAttackStage() != 0) {
                drawEnemyBattleStage("spatk", enemyPokemon.getSpecialAttackStage(), battleStageCount);
                battleStageCount++;
            }
            if (enemyPokemon.getSpecialDefenseStage() != 0) {
                drawEnemyBattleStage("spdef", enemyPokemon.getSpecialDefenseStage(), battleStageCount);
                battleStageCount++;
            }
            if (enemyPokemon.getSpeedStage() != 0) {
                drawEnemyBattleStage("speed", enemyPokemon.getSpeedStage(), battleStageCount);
                battleStageCount++;
            }
            if (enemyPokemon.getAccuracyStage() != 0) {
                drawEnemyBattleStage("accuracy", enemyPokemon.getAccuracyStage(), battleStageCount);
                battleStageCount++;
            }
            if (enemyPokemon.getEvasionStage() != 0) {
                drawEnemyBattleStage("evasion", enemyPokemon.getEvasionStage(), battleStageCount);
                battleStageCount++;
            }
        }
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

