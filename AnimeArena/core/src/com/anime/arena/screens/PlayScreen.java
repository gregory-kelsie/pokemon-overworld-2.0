package com.anime.arena.screens;

import com.anime.arena.AnimeArena;
import com.anime.arena.animation.FadeInAnimation;
import com.anime.arena.animation.FadeOutAnimation;
import com.anime.arena.animation.MapHeader;
import com.anime.arena.dto.PlayerProfile;
import com.anime.arena.emojis.EmojiHandler;
import com.anime.arena.interactions.*;
import com.anime.arena.items.ItemFactory;
import com.anime.arena.mart.PokeMart;
import com.anime.arena.objects.*;
import com.anime.arena.pokemon.BasePokemonFactory;
import com.anime.arena.pokemon.Pokemon;
import com.anime.arena.tools.DatabaseLoader;
import com.anime.arena.tools.OrthogonalTileSpriteRenderer;
import com.anime.arena.tools.PokemonMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.List;

public class PlayScreen implements Screen {

    private AnimeArena game;
    private Texture texture;

    private OrthographicCamera gameCam;
    private OrthographicCamera controlsCam;
    private Viewport gamePort;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private PokemonMap pokemonMap;
    private OrthogonalTileSpriteRenderer renderer;



    private int mapWidth;
    private int mapHeight;
    private int tileWidth;
    private int tileHeight;
    private TextureAtlas atlas;
    private TextureAtlas npcAtlas;
    private TextureAtlas bodyAtlas;
    private TextureAtlas swimmingAtlas;
    private HashMap<String, TextureAtlas> hairAtlases;
    private TextureAtlas topAtlas;
    private TextureAtlas bottomAtlas;
    private TextureAtlas bagAtlas;
    private TextureAtlas pokemonTypeAtlas;

    private TextureAtlas berryAtlas;

    private TextureAtlas pokemonAtlas;
    private TextureAtlas pokemonBackAtlas;
    private TextureAtlas pokemonIconAtlas;

    private TextureAtlas emojiAtlas;

    private Player player;
    private EmojiHandler emojiHandler;

    private Event event;
    private TextBoxFactory tbf;

    //HUD Textures
    private Texture textBox;
    private Texture textBoxTriangle;
    private Texture mapNameBoxTexture;
    private BitmapFont regularFont;
    private BitmapFont menuFont;
    private BitmapFont loadingFont;

    //Map Objects
    private List<TreeObject> trees;
    private List<ItemObject> items;
    private List<BerryObject> berries;

    //General Sound Effects
    private Sound selectSound;
    private Sound jumpSound;
    private Sound cutSound;
    private Music bumpSound;
    private Sound grassStepSound;
    private Sound menuOpenSound;
    private Sound menuCloseSound;
    private Sound menuSelectSound;

    //Background Music
    private Music bgm;
    private Music mapBgm;
    private Music wildPokemonBgm;

    private CustomPlayer customPlayer;
    private PlayerProfile playerProfile;

    //Quest Textures
    private Texture questStart;
    private Texture questComplete;
    private float questPopupElapsedTime;
    private int questPopupState;
    private int questPopupX;

    //Controller Textures
    private Texture dpad;
    private Texture panelTexture;
    private Texture textureA;
    private Texture textureB;
    private Texture startButton;
    private Texture selectButton;

    //Menu Textures
    private Texture menuTop;
    private Texture menuBottom;
    private Texture menuBody;
    private Texture pokedexIcon;
    private Texture pokemonIcon;
    private Texture bagIcon;
    private Texture gearIcon;
    private Texture idIcon;
    private Texture saveIcon;
    private Texture optionsIcon;

    private Texture pokedexIconS;
    private Texture pokemonIconS;
    private Texture bagIconS;
    private Texture gearIconS;
    private Texture idIconS;
    private Texture saveIconS;
    private Texture optionsIconS;
    private Texture exitIcon;
    private Texture menuSelector;

    //Shaders
    private ShaderProgram fadeOutShader;
    private String vertexShader = Gdx.files.internal("shaders/default.vs").readString();
    private String fragmentShader = Gdx.files.internal("shaders/black.fs").readString();

    //Transitions
    private FadeInAnimation fadeInAnimation;
    private FadeOutAnimation fadeOutAnimation;
    private boolean drawBlackScreen;
    private Texture blackTexture;

    //Map Name Variables
    private MapHeader mapHeader;
    private boolean displayMapName;

    private DatabaseLoader dbLoader;
    private int loadStatus; //0 = loading, 1 = just finished, 2 = done

    //Menu Variables
    private int menuPosition; //0 is top
    private boolean isMenuOpen;

    private PokeMart mart;

    public PlayScreen(AnimeArena game) {
        this.game = game;
        texture = new Texture("badlogic.jpg");
        initSounds();
        initShaders();
        initHudTextures();
        initFont();
        initMenu();
        initOverworldSpriteSheets();
        initPokemonSpriteSheets();
        initCamera();
        loadPokemonFromDB();


        emojiAtlas = new TextureAtlas("sprites/Emoji.atlas");
        emojiHandler = new EmojiHandler(emojiAtlas);
        tbf = new TextBoxFactory(this);



    }

    public PlayScreen(AnimeArena game, PlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
        this.game = game;
        texture = new Texture("badlogic.jpg");
        initSounds();
        initShaders();
        initHudTextures();
        initFont();
        initMenu();
        initOverworldSpriteSheets();
        initPokemonSpriteSheets();
        initCamera();
        loadPokemonFromDB();


        emojiAtlas = new TextureAtlas("sprites/Emoji.atlas");
        emojiHandler = new EmojiHandler(emojiAtlas);
        tbf = new TextBoxFactory(this);

    }

    public Camera getGameCam() {
        return gameCam;
    }

    private void initMenu() {
        isMenuOpen = false;
        menuPosition = 0;
    }



    private void loadPokemonFromDB() {
        dbLoader = new DatabaseLoader();
        dbLoader.start();
        loadStatus = 0;
    }

    /**
     * Ensure that the camera is locked to only showing the map.
     * When the player is close to an edge, the camera should freeze.
     */
    public void adjustOffscreenCamera() {
        if (player.getX() < AnimeArena.V_WIDTH / 2 / AnimeArena.PPM - 16) {
            gameCam.position.x = AnimeArena.V_WIDTH / 2 / AnimeArena.PPM;
        } else if (player.getX() > (pokemonMap.getMapWidth() * pokemonMap.getTileWidth()) / AnimeArena.PPM - AnimeArena.V_WIDTH / 2 / AnimeArena.PPM) {
            gameCam.position.x = (pokemonMap.getMapWidth() * pokemonMap.getTileWidth()) / AnimeArena.PPM - AnimeArena.V_WIDTH / 2 / AnimeArena.PPM;
        }

        if (player.getY() < AnimeArena.V_HEIGHT / 2 / AnimeArena.PPM - 6) {
            gameCam.position.y = AnimeArena.V_HEIGHT / 2 / AnimeArena.PPM;
        } else if (player.getY() > (pokemonMap.getMapHeight() * pokemonMap.getTileHeight()) / AnimeArena.PPM - ((AnimeArena.V_HEIGHT / 2) / AnimeArena.PPM)) {
            gameCam.position.y = (pokemonMap.getMapHeight() * pokemonMap.getTileHeight()) / AnimeArena.PPM - ((AnimeArena.V_HEIGHT / 2) / AnimeArena.PPM);
        }
    }

    private void initShaders() {
        vertexShader = Gdx.files.internal("shaders/default.vs").readString();
        fragmentShader = Gdx.files.internal("shaders/white.fs").readString();
        fadeOutShader = new ShaderProgram(vertexShader, fragmentShader);

        fadeInAnimation = new FadeInAnimation(fadeOutShader);
        fadeOutAnimation = new FadeOutAnimation(fadeOutShader);
    }

    private void initHudTextures() {
        questStart = new Texture("hud/newQuest.png");
        questComplete = new Texture("hud/questComplete.png");
        questPopupElapsedTime = 0;
        questPopupState = 0;
        questPopupX = 1080; //To a max 1080 and a min of 523

        mapHeader = new MapHeader();

        dpad = new Texture("hud/dpad.png");
        panelTexture = new Texture("hud/panel.png");
        textureA = new Texture("hud/Switch_A.png");
        textureB = new Texture("hud/Switch_B.png");
        startButton = new Texture("hud/start.png");
        selectButton = new Texture("hud/select.png");

        menuTop = new Texture("hud/menu/bgTop.png");
        menuBottom = new Texture("hud/menu/bgBtm.png");
        menuBody = new Texture("hud/menu/bgMid.png");
        pokedexIcon = new Texture("hud/menu/pokedexA.png");
        pokemonIcon = new Texture("hud/menu/pokemonA.png");
        bagIcon = new Texture("hud/menu/bagA.png");
        gearIcon = new Texture("hud/menu/gearAm.png");
        idIcon = new Texture("hud/menu/trainercardA.png");
        saveIcon = new Texture("hud/menu/saveA.png");
        optionsIcon = new Texture("hud/menu/optionsA.png");

        pokedexIconS = new Texture("hud/menu/pokedexB.png");
        pokemonIconS = new Texture("hud/menu/pokemonB.png");
        bagIconS = new Texture("hud/menu/bagB.png");
        gearIconS = new Texture("hud/menu/gearBm.png");
        idIconS = new Texture("hud/menu/trainercardB.png");
        saveIconS = new Texture("hud/menu/saveBm.png");
        optionsIconS = new Texture("hud/menu/optionsB.png");

        exitIcon = new Texture("hud/menu/exitA.png");
        menuSelector = new Texture("hud/menu/selector.png");

        blackTexture = new Texture("animation/black.png");
    }

    private void initFont() {
        //Menu Font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pkmnems.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter2.size = 60;
        parameter2.color = Color.DARK_GRAY;
        parameter2.spaceY = 20;
        parameter2.spaceX = -2;

        parameter2.shadowColor = Color.GRAY;
        parameter2.shadowOffsetX = 1;
        parameter2.shadowOffsetY = 1;

        menuFont = generator.generateFont(parameter2);

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 96;
        parameter.borderWidth = 4;
        parameter.borderColor = Color.BLACK;
        parameter.color = Color.WHITE;
        parameter.spaceY = 20;
        parameter.spaceX = -4;
        loadingFont = generator.generateFont(parameter);
    }

    private void initSounds() {
        //Init general sounds that occur often in the overworld
        selectSound = Gdx.audio.newSound(Gdx.files.internal("audio/SE/select.wav"));
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("audio/SE/jump.wav"));
        cutSound = Gdx.audio.newSound(Gdx.files.internal("audio/SE/Cut.ogg"));
        bumpSound = Gdx.audio.newMusic(Gdx.files.internal("audio/SE/bump.wav"));
        grassStepSound = Gdx.audio.newSound(Gdx.files.internal("audio/SE/grass.wav"));

        menuOpenSound = Gdx.audio.newSound(Gdx.files.internal("audio/SE/menuOpen.wav"));
        menuCloseSound = Gdx.audio.newSound(Gdx.files.internal("audio/SE/menuClose.wav"));
        menuSelectSound = Gdx.audio.newSound(Gdx.files.internal("audio/SE/menuSelect.wav"));
        //Init bgm


        wildPokemonBgm = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/WildJohto.ogg"));


    }

    private void initOverworldSpriteSheets() {
        atlas = new TextureAtlas("sprites/PlayableCharacters.atlas");
        npcAtlas = new TextureAtlas("sprites/npcs/NPC.atlas");
//        bodyAtlas = new TextureAtlas("sprites/player/compare/Bodies.atlas");
        bodyAtlas = new TextureAtlas("sprites/player/Bodies.atlas");
        swimmingAtlas = new TextureAtlas("sprites/player/swimming.atlas");
        topAtlas = new TextureAtlas("sprites/player/Upper.atlas");
        bottomAtlas = new TextureAtlas("sprites/player/Lower.atlas");
        hairAtlases = new HashMap<String, TextureAtlas>();
        hairAtlases.put("MaleHair1", new TextureAtlas("sprites/player/MaleHair1.atlas"));
        hairAtlases.put("MaleHair2", new TextureAtlas("sprites/player/MaleHair2.atlas"));
        hairAtlases.put("MaleHair3", new TextureAtlas("sprites/player/MaleHair3.atlas"));
        hairAtlases.put("MaleHair4", new TextureAtlas("sprites/player/MaleHair4.atlas"));
        hairAtlases.put("FemaleHair1", new TextureAtlas("sprites/player/FemaleHair1.atlas"));
        hairAtlases.put("FemaleHair2", new TextureAtlas("sprites/player/FemaleHair2.atlas"));
        hairAtlases.put("FemaleHair3", new TextureAtlas("sprites/player/FemaleHair3.atlas"));
        hairAtlases.put("FemaleHair4", new TextureAtlas("sprites/player/FemaleHair4.atlas"));
        berryAtlas = new TextureAtlas("sprites/berries.atlas");

    }

    private void initPokemonSpriteSheets() {
        this.pokemonAtlas = new TextureAtlas("sprites/pokemon/PokemonFront.atlas");
        this.pokemonBackAtlas = new TextureAtlas("sprites/pokemon/PokemonBack.atlas");
        this.pokemonIconAtlas = new TextureAtlas("sprites/pokemon/PokemonIcon.atlas");
        this.pokemonTypeAtlas = new TextureAtlas("sprites/PokemonTypes.atlas");
    }

    public TextureAtlas getPokemonAtlas() {
        return pokemonAtlas;
    }

    public TextureAtlas getPokemonTypesAtlas() { return pokemonTypeAtlas; }

    public TextureAtlas getPokemonIconAtlas() { return pokemonIconAtlas; }

    public TextureAtlas getEmojiAtlas() {
        return emojiAtlas;
    }

    public TextureAtlas getPokemonBackAtlas() {
        return pokemonBackAtlas;
    }

    private void initCamera() {
        gameCam = new OrthographicCamera();
        controlsCam = new OrthographicCamera();
        controlsCam.setToOrtho(false, 1080, 1920);
        gamePort = new FitViewport(AnimeArena.V_WIDTH / AnimeArena.PPM, AnimeArena.V_HEIGHT / AnimeArena.PPM, gameCam);
        gamePort.apply();
    }

    public void toggleBlackScreen() {
        drawBlackScreen = !drawBlackScreen;
    }

    private void loadPlayerFromDB() {
        //TODO: Load player information from database: Map, X, Y Coordinates, EventFlags, Pokemon Party, General Information (Name, Outfit), Bag
        initMap();
        loadPlayer();
        loadCustomPlayer();
    }

    private void loadCustomPlayer() {
        //String body = "flannery";
        String body = "male-dark";
        String hairType = "MaleHair4";
        String hairColour = "cyan";
        String top = "t-shirt-white";
        String bottom = "shorts-white";
        PlayerOutfit outfit = new PlayerOutfit();
        outfit.setBodyType(body);
        outfit.setHairType(hairType);
        outfit.setHairColour(hairColour);
        outfit.setTop(top);
        outfit.setBottom(bottom);
        customPlayer = new CustomPlayer(outfit);
        OutfitFactory outfitFactory = new OutfitFactory(this, outfit);
        customPlayer.setBody(outfitFactory.createBody());
        customPlayer.setSwimmingBody(outfitFactory.createSwimmingBody());
        customPlayer.setHair(outfitFactory.createHair());
        customPlayer.setTop(outfitFactory.createTop());
        customPlayer.setBottom(outfitFactory.createBottom());
        customPlayer.setBag(outfitFactory.createBag());
        customPlayer.initSpritePosition(298, 113);
    }

    private void loadPlayer() {
        player = new Player(this, this.playerProfile, pokemonMap, gameCam);
        player.getBag().addItem(dbLoader.getItemFactory(), 1, 10);
        player.getBag().addItem(dbLoader.getItemFactory(), 2, 10);
        player.getBag().addItem(dbLoader.getItemFactory(), 3, 10);
        player.getBag().addItem(dbLoader.getItemFactory(), 4, 10);
        player.getBag().addItem(dbLoader.getItemFactory(), 5, 10);
        player.getBag().addItem(dbLoader.getItemFactory(), 6, 10);
        player.getBag().addItem(dbLoader.getItemFactory(), 7, 10);
        player.getBag().addItem(dbLoader.getItemFactory(), 8, 10);
        player.getBag().addItem(dbLoader.getItemFactory(), 9, 10);
        player.getBag().addItem(dbLoader.getItemFactory(), 10, 10);
        player.getBag().addItem(dbLoader.getItemFactory(), 11, 10);
        player.getBag().addItem(dbLoader.getItemFactory(), 12, 10);
        player.getBag().addItem(dbLoader.getItemFactory(), 13, 10);
        player.getBag().addItem(dbLoader.getItemFactory(), 20, 10);
        player.getBag().addItem(dbLoader.getItemFactory(), 32, 10);

//        player.setX(-5);
//        player.setY(0);
        OutfitFactory outfitFactory = new OutfitFactory(this, player.getOutfit());
        player.setBody(outfitFactory.createBody());
        player.setSwimmingBody(outfitFactory.createSwimmingBody());
        player.setHair(outfitFactory.createHair());
        player.setTop(outfitFactory.createTop());
        player.setBottom(outfitFactory.createBottom());
        player.setBag(outfitFactory.createBag());
        player.initSpritePositions(0f, 0f);
        initPlayerPosition();
    }

    private void initPlayerPosition() {
        //Defaults is x = 18, y = 25 for the lab

        player.setYTile(playerProfile.getyPosition());
        player.setXTile(playerProfile.getxPosition());
    }

    private void initMap() {
        mapLoader = new TmxMapLoader();
        loadMap();

    }

    private void loadMap() {
        if (pokemonMap != null) {
            pokemonMap.dispose();
        }
        String testMap = "maps/test.tmx";//"maps/untitled2.tmx";
        String route1 = "maps/3.0.tmx";
        String currentMap = "maps/" + playerProfile.getMapName() + ".tmx";
        pokemonMap = new PokemonMap(mapLoader.load(currentMap), this);
        if (pokemonMap.getBGM() != "") {
            mapBgm = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/" + pokemonMap.getBGM()));
            bgm = mapBgm;
            bgm.setLooping(true);
        }
        triggerMapHeader();
    }

    public void loadMap(String mapName) {
        String oldBGM = pokemonMap.getBGM();
        pokemonMap.setMap(mapLoader.load("maps/" + mapName));
        if (pokemonMap.getBGM() != "" && !pokemonMap.getBGM().equals(oldBGM)) {
            bgm.stop();
            mapBgm = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/" + pokemonMap.getBGM()));
            bgm = mapBgm;
            bgm.setLooping(true);
            bgm.play();
        }
        renderer.resetMap();
    }

    public void removeMapHUDAnimations() {
        //Remove the map header
        displayMapName = false;
        mapHeader.resetMapBox();

        //Remove the quest popup
        questPopupX = 1080;
        questPopupState = 0;
    }

    public void triggerMapHeader() {

        if (pokemonMap.getMapName() != "") {
            displayMapName = true;
            mapHeader.setMapHeaderName(pokemonMap.getMapName());
        }
    }

    public void playSelectSound() {
        selectSound.play();
    }

    public void playJumpSound() {
        jumpSound.play();
    }

    public void playGrassStepSound() { grassStepSound.play(); }

    public void playBumpSound() {
        if (!bumpSound.isPlaying()) {
            bumpSound.play();
        }
    }

    public void playCutSound() {
        cutSound.play();
    }

    public void stopBgm() {
        bgm.pause();
    }

    public void playBgm() {
        bgm.play();
    }


    private void initTextures() {
        textBox = new Texture("hud/textborder.png");
        mapNameBoxTexture = new Texture("hud/maptitleborder.png");
        textBoxTriangle = new Texture("hud/triangle.png");
        regularFont = new BitmapFont(Gdx.files.internal("hud/regularFont.fnt"));
        regularFont.setColor(Color.BLACK);

    }

    public void startSwimmingEvent() {
        playSelectSound();
        setEvent(tbf.getSwimmingTextBox());
    }

    public void startWildPokemonEvent() {
        setBgm(wildPokemonBgm);
        Pokemon wildPokemon = pokemonMap.getWildPokemon(getBasePokemonFactory());
        setEvent(new WildPokemonEvent(this, wildPokemon));
    }

    public void startEncounterEvent(EncounterEvent encounterEvent) {
        setBgm(encounterEvent.getMusic());
        setEvent(encounterEvent);
    }

    public void setEvent(Event event) {
        this.event = event;
        closeMenu();
        if (event != null) {
            player.startEvent();
        } else {
            player.endEvent();
        }
    }

    public void setBgm(Music newBgm) {
        bgm.stop();
        bgm = newBgm;
        bgm.play();
    }

    public void playMapBgm() {
        bgm.stop();
        bgm = mapBgm;
        bgm.play();
    }

    public FadeInAnimation getFadeInAnimation() {
        return fadeInAnimation;
    }

    public FadeOutAnimation getFadeOutAnimation() {
        return fadeOutAnimation;
    }

    public void startFadeInAnimation() {
        this.fadeInAnimation.startAnimation();
    }

    public void stopFadeInAnimation() {
        this.fadeInAnimation.stopAnimation();
    }

    public void startFadeOutAnimation() {
        this.fadeOutAnimation.startAnimation();
    }

    public void stopFadeOutAnimation() {
        this.fadeOutAnimation.stopAnimation();
    }

    public void clearTransitionAnimations() {
        this.fadeInAnimation.stopAnimation();
        this.fadeOutAnimation.stopAnimation();
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public TextureAtlas getPokemonTypeAtlas() { return pokemonTypeAtlas; }

    public TextureAtlas getNPCAtlas() {
        return npcAtlas;
    }

    public TextureAtlas getBodyAtlas() {
        return bodyAtlas;
    }

    public TextureAtlas getSwimmingAtlas() {
        return swimmingAtlas;
    }

    public HashMap<String, TextureAtlas> getHairAtlases() {
        return hairAtlases;
    }

    public TextureAtlas getTopAtlas() {
        return topAtlas;
    }

    public TextureAtlas getBottomAtlas() {
        return bottomAtlas;
    }

    public TextureAtlas getBagAtlas() {
        return bagAtlas;
    }

    public TextureAtlas getHairAtlas(String hairType) {
        return hairAtlases.get(hairType);
    }

    public TextureAtlas getBerryAtlas() {
        return berryAtlas;
    }

    public PokemonMap getPokemonMap() {
        return pokemonMap;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void show() {

    }

    private boolean updateLoadingStatus() {
        if (dbLoader.hasLoadedEssentials() && loadStatus == 0) {
            loadStatus = 1;
            return false;
        } else if (dbLoader.hasLoadedEssentials()) {
            return true;
        }
        return false;
    }

    public BasePokemonFactory getBasePokemonFactory() {
        return dbLoader.getBasePokemonFactory();
    }

    public ItemFactory getItemFactory() { return dbLoader.getItemFactory(); }

    public void update(float dt) {
        //updateLoadingStatus();
        //Gdx.app.log("cam", "x: " + gameCam.position.x + ", y: " + gameCam.position.y);
        if (loadStatus == 3) {
            handleInput(dt);
            player.update(dt);
            customPlayer.update(dt);
            if (event != null) {
                event.update(dt);
            }
            pokemonMap.update(dt);

            emojiHandler.update(dt);
            updateQuestPopup(dt);
            if (displayMapName) {
                mapHeader.update(dt);
                if (mapHeader.isFinishedAnimation()) {
                    displayMapName = false;
                }
            }
            fadeOutAnimation.update(dt);
            fadeInAnimation.update(dt);


            gameCam.update();
            controlsCam.update();
            renderer.setView(gameCam);
        } else if (loadStatus == 1) {
            loadStatus = 2;
            loadPlayerFromDB();
            renderer = new OrthogonalTileSpriteRenderer(pokemonMap, AnimeArena.PPM);
            renderer.setPlayer(player);
            gameCam.position.set((AnimeArena.V_WIDTH / 2) / AnimeArena.PPM, (AnimeArena.V_HEIGHT / 2) / AnimeArena.PPM, 0);

            bgm.play();
        } else if (loadStatus == 2) {
            loadStatus = 3;
        }

    }

    public Event getEvent() {
        return event;
    }

    private void handleInput(float dt) {
        //Gdx.app.log("dt", "X: " + player.getX() + "Y: " + player.getYTile() + " mbdt: " + player.getMoveButtonDownTime());
        if (Gdx.input.isTouched()) {
            if (event == null) {
                mart = new PokeMart(this);
                setEvent(mart);
            }
            //gameCam.position.y += 100 * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {

                if (isMenuOpen) {
                    menuCloseSound.play();
                    closeMenu();
                }
                if (questPopupState != 0) {
                    questPopupState = 3;
                    questPopupElapsedTime = 0;
                }
                if (event != null) {
                    event.clickedX();
                }
            }
            player.run();
        } else {
            player.stopRun();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (!isMenuOpen && event == null && player.getSwitches().isActive(3)) {
                isMenuOpen = true;
                menuOpenSound.play();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            if (event != null) {
                event.interact();
            } else {
                if (isMenuOpen) {
                    if (menuPosition == 2) {
                        game.setScreen(new BagScreen(game, player, pokemonAtlas, pokemonIconAtlas, pokemonTypeAtlas,this, dbLoader));
                    } else if (menuPosition == 0) {
                        game.setScreen(new PokedexScreen(game, this, pokemonAtlas, pokemonTypeAtlas, player.getPokedex(), dbLoader));
                    } else if (menuPosition == 1) {
                        game.setScreen(new PokemonScreen(game, this, player, pokemonAtlas, pokemonIconAtlas, pokemonTypeAtlas, dbLoader));
                    }
                }
                if (!player.isJumping() && player.isStopped()) {
                    player.interact();
                }
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            emojiHandler.emoteDots();
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            emojiHandler.emoteHappy();
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            emojiHandler.emoteSad();
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
            emojiHandler.emoteNo();
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_5)) {
            emojiHandler.emoteExclamation();
        } else if (Gdx.input.isKeyPressed((Input.Keys.NUM_6))) {
            if (questPopupState == 0) {
                questPopupState = 1;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (!isMenuOpen && player.isMovable()) {
                player.stopY();
                if (player.isStopped() || player.isMoving(Direction.LEFT)) {
                    player.moveLeft(dt);
                }
            } else if (event != null) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                    event.clickedLeft();
                }
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (!isMenuOpen && player.isMovable()) {
                player.stopY();
                if (player.isStopped() || player.isMoving(Direction.RIGHT)) {
                    player.moveRight(dt);
                }
            } else if (event != null) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                    event.clickedRight();
                }
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && isMenuOpen) {
                menuSelectSound.play();
                if (menuPosition == 0) {
                    menuPosition = 6;
                } else {
                    menuPosition--;
                }
            } else if (!isMenuOpen && player.isMovable()) {
                player.stopX();
                if (player.isStopped() || player.isMoving(Direction.UP)) {
                    player.moveUp(dt);
                }
            } else if (event != null && Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                event.clickedUp();
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && isMenuOpen) {
                menuSelectSound.play();
                if (menuPosition == 6) {
                    menuPosition = 0;
                } else {
                    menuPosition++;
                }
            }
            if (!isMenuOpen && player.isMovable()) {
                player.stopX();
                if (player.isStopped() || player.isMoving(Direction.DOWN)) {
                    player.moveDown(dt);
                }
            }  else if (event != null && Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                event.clickedDown();
            }
        } else {
            player.resetMoveButtonDownTime();
                player.stopMoving();
        }
        if (isMenuOpen) {
            player.resetMoveButtonDownTime();
            player.stopMoving();
        }
    }

    public AnimeArena getGame() {
        return game;
    }

    private void closeMenu() {
        isMenuOpen = false;
        menuPosition = 0;
    }

    private void renderMenu(SpriteBatch batch) {
        batch.draw(menuTop, 660, 1889, 400, 24);
        batch.draw(menuBody, 660, 1039, 400, 850);
        batch.draw(menuBottom, 660, 1038, 400 , 24);

        //Pokedex
        if (menuPosition == 0) {
            batch.draw(pokedexIconS, 685, 1792, pokedexIconS.getWidth() * 2, pokedexIconS.getHeight() * 2);
            batch.draw(menuSelector, 670, 1782, 380, 104);
        } else {
            batch.draw(pokedexIcon, 685, 1792, pokedexIcon.getWidth() * 2, pokedexIcon.getHeight() * 2);
        }
        //Pokemon
        if (menuPosition == 1) {
            batch.draw(pokemonIconS, 685, 1682, pokemonIconS.getWidth() * 2, pokemonIconS.getHeight() * 2);
            batch.draw(menuSelector, 670, 1672, 380, 104);
        } else {
            batch.draw(pokemonIcon, 685, 1682, pokemonIcon.getWidth() * 2, pokemonIcon.getHeight() * 2);
        }
        //Bag
        if (menuPosition == 2) {
            batch.draw(bagIconS, 685, 1562, bagIconS.getWidth() * 2, bagIconS.getHeight() * 2);
            batch.draw(menuSelector, 670, 1554, 380, 104);
        } else {
            batch.draw(bagIcon, 685, 1562, bagIcon.getWidth() * 2, bagIcon.getHeight() * 2);
        }
        //Gear
        if (menuPosition == 3) {
            batch.draw(gearIconS, 695, 1452, gearIconS.getWidth() * 2, gearIconS.getHeight() * 2);
            batch.draw(menuSelector, 670, 1447, 380, 104);
        } else {
            batch.draw(gearIcon, 695, 1452, gearIcon.getWidth() * 2, gearIcon.getHeight() * 2);
        }
        //ID
        if (menuPosition == 4) {
            batch.draw(idIconS, 685, 1342, idIconS.getWidth() * 2, idIconS.getHeight() * 2);
            batch.draw(menuSelector, 670, 1327, 380, 104);
        } else {
            batch.draw(idIcon, 685, 1342, idIcon.getWidth() * 2, idIcon.getHeight() * 2);
        }
        //Save
        if (menuPosition == 5) {
            batch.draw(saveIconS, 685, 1222, saveIconS.getWidth() * 2, saveIconS.getHeight() * 2);
            batch.draw(menuSelector, 670, 1215, 380, 104);
        } else {
            batch.draw(saveIcon, 685, 1222, saveIcon.getWidth() * 2, saveIcon.getHeight() * 2);
        }
        if (menuPosition == 6) {
            batch.draw(optionsIconS, 685, 1102, optionsIconS.getWidth() * 2, optionsIconS.getHeight() * 2);
            batch.draw(menuSelector, 670, 1095, 380, 104);
        } else {
            batch.draw(optionsIcon, 685, 1102, optionsIcon.getWidth() * 2, optionsIcon.getHeight() * 2);
        }

        menuFont.draw(batch, "Pok\u00e9dex", 782, 1842);
        menuFont.draw(batch, "Pok\u00e9mon", 782, 1732);
        menuFont.draw(batch, "Bag", 782, 1614);
        menuFont.draw(batch, "Pok\u00e9Gear", 782, 1507);
        menuFont.draw(batch, "Friends", 782, 1392);
        menuFont.draw(batch, "Save", 782, 1280);
        menuFont.draw(batch, "Options", 782, 1155);



    }

    @Override
    public void render(float delta) {
        if (updateLoadingStatus()) {
            update(delta);
            if (loadStatus == 3) {
                Gdx.gl.glClearColor(1, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                //Game Screen
                Gdx.gl.glViewport(0, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);
                renderer.render();
                renderer.getBatch().begin();
                customPlayer.draw(renderer.getBatch());

                emojiHandler.draw(renderer.getBatch(), player.getX() + 5, player.getY() + 28);
                renderer.getBatch().end();
                //Controls Screen
                Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                game.getBatch().setProjectionMatrix(controlsCam.combined);
                game.getBatch().begin();
//                if (mart != null) {
//                    mart.render(game.getBatch());
//                }
                if (event != null) {
                    event.render(game.getBatch());
                } else if (isMenuOpen) {
                    renderMenu(game.getBatch());
                } else if (drawBlackScreen) {
                    game.getBatch().draw(blackTexture, 0, 960);
                }
                if (questPopupState != 0) {
                    //Render Map Box
                    //Render Map Name
                    game.getBatch().draw(questStart, questPopupX, 1750, 558, 155);

                }
                game.getBatch().draw(panelTexture, 0, 0, 1080, 1920 / 2);
                game.getBatch().draw(dpad, 50, 300, 380, 380);
                game.getBatch().draw(textureB, 600, 350, 230, 230);
                game.getBatch().draw(textureA, 820, 450, 230, 230);
                game.getBatch().draw(startButton, 230, -20, 230, 230);
                game.getBatch().draw(selectButton, 650, -20, 230, 230);
                //game.getBatch().draw(texture, 0, 0);
                if (displayMapName && !isMenuOpen) {
                    mapHeader.render(game.getBatch());
                }
                fadeOutAnimation.render(delta, game.getBatch());
                fadeInAnimation.render(delta, game.getBatch());
                game.getBatch().end();
            } else {
                drawLoadingScreen(game.getBatch());
            }
        } else {
            drawLoadingScreen(game.getBatch());
        }

    }

    private void drawLoadingScreen(SpriteBatch batch) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(controlsCam.combined);
        batch.begin();
        batch.draw(blackTexture, 0, 960);
        loadingFont.draw(batch, "Loading...", 350, 1510);
        batch.end();
    }

    private void updateQuestPopup(float dt) {
        if (questPopupState != 0) {
            if (questPopupState == 1) {
                questPopupX = Math.max(523, (int) (questPopupX - (dt * 960)));
                if (questPopupX == 523) {
                    questPopupState = 2;
                }
            }
            if (questPopupState == 2) {
                questPopupElapsedTime += dt;
                if (questPopupElapsedTime >= 3) {
                    questPopupState = 3;
                    questPopupElapsedTime = 0;
                }
            }
            if (questPopupState == 3) {
                questPopupX += dt * 960;
                if (questPopupX >= 1080) {
                    questPopupX = 1080;
                    questPopupState = 0;
                }
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
        renderer.dispose();
        selectSound.dispose();
        jumpSound.dispose();
        bumpSound.dispose();
        cutSound.dispose();
        mapBgm.dispose();
        wildPokemonBgm.dispose();
        atlas.dispose();
        bodyAtlas.dispose();
        swimmingAtlas.dispose();
        topAtlas.dispose();
        bottomAtlas.dispose();
        mapHeader.dispose();
        for (TextureAtlas hair : hairAtlases.values()) {
            hair.dispose();
        }
        menuTop.dispose();
        menuBody.dispose();
        menuBottom.dispose();
        pokemonTypeAtlas.dispose();
    }
}
