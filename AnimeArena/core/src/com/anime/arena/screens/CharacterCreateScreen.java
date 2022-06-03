package com.anime.arena.screens;

import com.anime.arena.AnimeArena;
import com.anime.arena.dto.PlayerProfile;
import com.anime.arena.objects.CustomPlayer;
import com.anime.arena.objects.OutfitFactory;
import com.anime.arena.objects.PlayerBody;
import com.anime.arena.objects.PlayerOutfit;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CharacterCreateScreen implements Screen {

    private AnimeArena game;
    private Texture black;

    private Texture background;
    private Texture blackPanel;
    private Texture leftArrow;
    private Texture rightArrow;
    private Texture blankPanel;
    private Texture titleTexture;
    private Texture labelsTexture;
    private Texture characterPanel;

    private OrthographicCamera gameCam;
    private OrthographicCamera controlsCam;
    private Viewport gamePort;

    private Screen previousScreen;


    private BitmapFont menuFont;
    private BitmapFont listFont;
    private BitmapFont nameFont;

    private int screenPosition; //0 menu, 1 list, 2 entry, 3 sprite, 4 nest

    private int selectPosition;

    private OutfitFactory outfitFactory;
    private char gender;
    private String[] maleGenders;
    private String[] femaleGenders;
    private String[] maleHairStyles;
    private String[] femaleHairStyles;
    private String[] hairColours;
    private String[] tops;
    private String[] bottoms;

    private int bodyTypeIndex;
    private int hairTypeIndex;
    private int hairColourIndex;
    private int topsIndex;
    private int bottomsIndex;

    private HashMap<String, Integer> topMap;
    private HashMap<String, Integer> bottomMap;

    private CustomPlayer displayCharacter;
    private PlayerProfile playerProfile;

    public static final boolean DEBUG_CHARACTER_CREATE_SCREEN = true;

    public CharacterCreateScreen(AnimeArena game, PlayerProfile playerProfile) {
        this.game = game;
        this.playerProfile = playerProfile;
        this.black = new Texture("animation/black.png");
        this.background = new Texture("charcreate/background.png");
        this.blackPanel = new Texture("charcreate/window.png");
        this.leftArrow = new Texture("charcreate/leftarrow.png");
        this.rightArrow = new Texture("charcreate/rightarrow.png");
        this.blankPanel = new Texture("charcreate/blankpanel.png");
        this.characterPanel = new Texture("charcreate/charpanel.png");
        this.titleTexture = new Texture("charcreate/title.png");
        this.labelsTexture = new Texture("charcreate/labels.png");

        this.selectPosition = 0; //Gender, the top of the list
        this.femaleGenders  = new String[]{"female-pale", "female-light", "female-medium", "female-dark"};
        this.maleGenders = new String[]{"male-pale", "male-light", "male-medium", "male-dark"};
        this.maleHairStyles = new String[]{null, "MaleHair1", "MaleHair2","MaleHair3","MaleHair4"};
        this.femaleHairStyles = new String[]{null, "FemaleHair1", "FemaleHair2","FemaleHair3","FemaleHair4"};
        this.hairColours = new String[]{"black", "brown", "blue", "cyan", "pink", "purple", "white"};
        this.tops = new String[] {null, "t-shirt-black", "t-shirt-blue","t-shirt-green","t-shirt-red","t-shirt-white"};
        this.bottoms = new String[] {null, "shorts-black", "shorts-blue","shorts-green","shorts-red","shorts-white"};

        //Map the clothing code from the arrays to the item id in the database.
        this.topMap = new HashMap<String, Integer>();
        topMap.put("t-shirt-black", 29);
        topMap.put("t-shirt-blue", 30);
        topMap.put("t-shirt-green", 31);
        topMap.put("t-shirt-red", 32);
        topMap.put("t-shirt-white", 33);
        this.bottomMap = new HashMap<String, Integer>();
        bottomMap.put("shorts-black", 34);
        bottomMap.put("shorts-blue", 35);
        bottomMap.put("shorts-green", 36);
        bottomMap.put("shorts-red", 37);
        bottomMap.put("shorts-white", 38);

        this.gender = 'M';
        this.bodyTypeIndex = 0;
        this.hairTypeIndex = 0;
        this.hairColourIndex = 0;
        this.topsIndex = 0;
        this.bottomsIndex = 0;

        initDisplayCharacter();
        initFont();

        initCamera();

        initVariables();

        gameCam.position.set((AnimeArena.V_WIDTH / 2) / AnimeArena.PPM, (AnimeArena.V_HEIGHT / 2) / AnimeArena.PPM, 0);

    }

    private void initDisplayCharacter() {
        String body = "male-pale";
        String hairType = null;
        String hairColour = "black";
        String top = null;
        String bottom = null;
        PlayerOutfit outfit = new PlayerOutfit();
        outfit.setBodyType(body);
        outfit.setHairType(hairType);
        outfit.setHairColour(hairColour);
        outfit.setTop(top);
        outfit.setBottom(bottom);
        displayCharacter = new CustomPlayer(outfit);

        outfitFactory = new OutfitFactory(outfit);
        displayCharacter.setBody(outfitFactory.createBody());
        displayCharacter.setSwimmingBody(outfitFactory.createSwimmingBody());
        displayCharacter.setHair(outfitFactory.createHair());
        displayCharacter.setTop(outfitFactory.createTop());
        displayCharacter.setBottom(outfitFactory.createBottom());
        displayCharacter.setBag(outfitFactory.createBag());
        displayCharacter.initSpritePosition2(245, 1138);
        displayCharacter.setShadow(false);
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
        parameter3.size = 48;
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
        handleInput(dt);
        displayCharacter.update(dt);

    }



    private void handleInput(float dt) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (selectPosition > 0) {
                selectPosition--;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (selectPosition < 5) {
                selectPosition++;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (this.selectPosition == 0) {
                if (gender == 'M') {
                    gender = 'F';
                    setFemaleBody();
                    setFemaleHairStyle();
                } else {
                    gender = 'M';
                    setMaleBody();
                    setMaleHairStyle();
                }
            } else if (selectPosition == 1) {
                if (gender == 'M' && bodyTypeIndex < maleGenders.length - 1) {
                    bodyTypeIndex++;
                    setMaleBody();
                } else if (gender == 'F' && bodyTypeIndex < femaleGenders.length - 1) {
                    bodyTypeIndex++;
                    setFemaleBody();
                }
            } else if (selectPosition == 2) {
                if (gender == 'M'  && hairTypeIndex < maleHairStyles.length - 1) {
                    hairTypeIndex++;
                    setMaleHairStyle();
                } else if (gender == 'F' && hairTypeIndex < femaleHairStyles.length - 1) {
                    hairTypeIndex++;
                    setFemaleHairStyle();
                }
            } else if (selectPosition == 3) {
                if (hairColourIndex < hairColours.length - 1) {
                    hairColourIndex++;
                    setHairColour();
                }
            } else if (selectPosition == 4) {
                if (topsIndex < tops.length - 1) {
                    topsIndex++;
                    setTopClothing();
                }
            } else if (selectPosition == 5) {
                if (bottomsIndex < bottoms.length - 1) {
                    bottomsIndex++;
                    setBottomClothing();
                }
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (this.selectPosition == 0) {
                if (gender == 'M') {
                    gender = 'F';
                    setFemaleBody();
                    setFemaleHairStyle();
                } else {
                    gender = 'M';
                    setMaleBody();
                    setMaleHairStyle();
                }
            } else if (selectPosition == 1) {
                if (gender == 'M' && bodyTypeIndex > 0) {
                    bodyTypeIndex--;
                    setMaleBody();
                } else if (gender == 'F' && bodyTypeIndex > 0) {
                    bodyTypeIndex--;
                    setFemaleBody();
                }
            } else if (selectPosition == 2) {
                if (gender == 'M' && hairTypeIndex > 0) {
                    hairTypeIndex--;
                    setMaleHairStyle();
                } else if (gender == 'F' && hairTypeIndex > 0) {
                    hairTypeIndex--;
                    setFemaleHairStyle();
                }
            } else if (selectPosition == 3) {
                if (hairColourIndex > 0) {
                    hairColourIndex--;
                    setHairColour();
                }
            } else if (selectPosition == 4) {
                if (topsIndex > 0) {
                    topsIndex--;
                    setTopClothing();
                }
            } else if (selectPosition == 5) {
                if (bottomsIndex > 0) {
                    bottomsIndex--;
                    setBottomClothing();
                }
            }
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.X))) {

        } else if (Gdx.input.isKeyJustPressed((Input.Keys.Z))) {
            saveCharacter();
        }

    }


    private void setMaleBody() {
        displayCharacter.getOutfit().setBodyType(maleGenders[bodyTypeIndex]);
        displayCharacter.setBody(outfitFactory.createBody());
        displayCharacter.initSpritePosition2(245, 1138);
    }

    private void setFemaleBody() {

        displayCharacter.getOutfit().setBodyType(femaleGenders[bodyTypeIndex]);
        displayCharacter.setBody(outfitFactory.createBody());
        displayCharacter.initSpritePosition2(245, 1138);
    }

    private void setMaleHairStyle() {
        displayCharacter.getOutfit().setHairType(maleHairStyles[hairTypeIndex]);
        displayCharacter.setHair(outfitFactory.createHair());
        displayCharacter.initSpritePosition2(245, 1138);
    }

    private void setFemaleHairStyle() {
        displayCharacter.getOutfit().setHairType(femaleHairStyles[hairTypeIndex]);
        displayCharacter.setHair(outfitFactory.createHair());
        displayCharacter.initSpritePosition2(245, 1138);
    }

    private void setHairColour() {
        displayCharacter.getOutfit().setHairColour(hairColours[hairColourIndex]);
        displayCharacter.setHair(outfitFactory.createHair());
        displayCharacter.initSpritePosition2(245, 1138);
    }

    private void setTopClothing() {
        displayCharacter.getOutfit().setTop(tops[topsIndex]);
        displayCharacter.setTop(outfitFactory.createTop());
        displayCharacter.initSpritePosition2(245, 1138);
    }

    private void setBottomClothing() {
        displayCharacter.getOutfit().setBottom(bottoms[bottomsIndex]);
        displayCharacter.setBottom(outfitFactory.createBottom());
        displayCharacter.initSpritePosition2(245, 1138);
    }

    private void saveCharacter() {
        playerProfile.setHairColour(hairColours[hairColourIndex]);
        playerProfile.setGender(Character.toString(gender).toUpperCase());
        if (gender == 'M') {
            playerProfile.setHairStyle(maleHairStyles[hairTypeIndex]);
            playerProfile.setSkinTone(maleGenders[bodyTypeIndex]);
        } else {
            playerProfile.setHairStyle(femaleHairStyles[hairTypeIndex]);
            playerProfile.setSkinTone(femaleGenders[bodyTypeIndex]);
        }
        playerProfile.setTopID(topMap.get(tops[topsIndex]));
        playerProfile.setBottomID(bottomMap.get(bottoms[bottomsIndex]));
        playerProfile.updateProfile();
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
        int blackPanelWidth = 700;
        game.getBatch().draw(blackPanel, (1080 - blackPanelWidth) / 2, 1110, blackPanelWidth, 660);
        game.getBatch().draw(titleTexture, 400, 1700, 322, 50);
        game.getBatch().draw(labelsTexture, 230, 1300, 214, 338);
        game.getBatch().draw(characterPanel, 240, 1130, 134, 128);

        game.getBatch().draw(blankPanel, 560, 1600, 260, 42);
        game.getBatch().draw(blankPanel, 560, 1543, 260, 42);
        game.getBatch().draw(blankPanel, 560, 1486, 260, 42);
        game.getBatch().draw(blankPanel, 560, 1429, 260, 42);
        game.getBatch().draw(blankPanel, 560, 1372, 260, 42);
        game.getBatch().draw(blankPanel, 560, 1315, 260, 42);

        int arrowYPos = 1608 - (selectPosition * 57);

        game.getBatch().draw(leftArrow, 527, arrowYPos, 22,24);
        game.getBatch().draw(rightArrow, 832, arrowYPos, 22,24);
        displayCharacter.draw(game.getBatch());

        String genderLabel;
        if (gender == 'M') {
            genderLabel = "Male";
        } else {
            genderLabel = "Female";
        }
        menuFont.draw(game.getBatch(), genderLabel, 630, 1635);
        menuFont.draw(game.getBatch(), Integer.toString(bodyTypeIndex), 650, 1575);
        menuFont.draw(game.getBatch(), Integer.toString(hairTypeIndex), 650, 1515);
        menuFont.draw(game.getBatch(), Integer.toString(hairColourIndex), 650, 1460);
        menuFont.draw(game.getBatch(), Integer.toString(hairColourIndex), 650, 1460);
        menuFont.draw(game.getBatch(), Integer.toString(hairColourIndex), 650, 1460);
        menuFont.draw(game.getBatch(), Integer.toString(topsIndex), 650, 1400);
        menuFont.draw(game.getBatch(), Integer.toString(bottomsIndex), 650, 1345);
        game.getBatch().end();
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
