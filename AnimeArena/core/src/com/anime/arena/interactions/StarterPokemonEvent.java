package com.anime.arena.interactions;

import com.anime.arena.pokemon.*;
import com.anime.arena.screens.PlayScreen;
import com.anime.arena.tools.TextFormater;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class StarterPokemonEvent extends Event {
    private BasePokemon leftPokemon;
    private BasePokemon middlePokemon;
    private BasePokemon rightPokemon;

    private Sprite left;

    private Texture groundBG;
    private Texture electricBG;
    private Texture waterBG;
    private Texture textbox;
    private Texture selectArrow;

    private Sprite leftBall;
    private Sprite middleBall;
    private Sprite rightBall;

    private int selectedPokemon; // 0 left, 1 mid, 2 right
    private boolean selectedYes;
    private BitmapFont descriptionFont;
    private String description;

    private Sound leftSound;
    private Sound midSound;
    private Sound rightSound;

    private int leftStarter = 511;
    private int midStarter = 513;
    private int rightStarter = 515;

    public StarterPokemonEvent(PlayScreen screen) {
        super(screen);
        init();
    }

    private void init() {
        initBackgrounds();
        initHudTextures();
        initStarterPokemon();
        initBallSelection();
        initFont();
        selectedYes = false;
        leftSound.play();
    }

    private void initHudTextures() {
        textbox = new Texture("hud/starters/textbox.png");
        selectArrow = new Texture("hud/starters/selarrowwhite.png");
    }

    private void initStarterPokemon() {
        leftPokemon = screen.getBasePokemonFactory().createBasePokemon(leftStarter);
        middlePokemon = screen.getBasePokemonFactory().createBasePokemon(midStarter);
        rightPokemon = screen.getBasePokemonFactory().createBasePokemon(rightStarter);

        left = new Sprite(screen.getPokemonAtlas().findRegion(leftPokemon.getFormattedImage()));
        left.setSize(576, 576);
        left.setPosition(256,1200);
        initCries();
    }

    private void initBallSelection() {
        openLeftBall();
        closeMiddleBall();
        closeRightBall();
        selectedPokemon = 0;
    }

    private void initBackgrounds() {
        groundBG = new Texture("hud/starters/grass.png");
        electricBG = new Texture("hud/starters/ground.png");
        waterBG = new Texture("hud/starters/water.png");
    }

    private void initCries() {
        try {
            leftSound = Gdx.audio.newSound(Gdx.files.internal("audio/SE/cry/" + PokemonUtils.getCry(leftPokemon)));
        } catch (Exception e) {
            leftSound = Gdx.audio.newSound(Gdx.files.internal("audio/SE/cry/001.wav"));
        }
        try {
            midSound = Gdx.audio.newSound(Gdx.files.internal("audio/SE/cry/" + PokemonUtils.getCry(middlePokemon)));
        } catch (Exception e) {
            midSound = Gdx.audio.newSound(Gdx.files.internal("audio/SE/cry/001.wav"));
        }
        try {
            rightSound = Gdx.audio.newSound(Gdx.files.internal("audio/SE/cry/" + PokemonUtils.getCry(rightPokemon)));
        } catch (Exception e) {
            rightSound = Gdx.audio.newSound(Gdx.files.internal("audio/SE/cry/001.wav"));
        }
    }

    private void initFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pkmnems.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter2.size = 60;
        parameter2.color = Color.WHITE;
        parameter2.spaceY = 20;
        parameter2.spaceX = -2;

        parameter2.shadowColor = Color.BLACK;
        parameter2.shadowOffsetX = 5;
        parameter2.shadowOffsetY = 5;

        descriptionFont = generator.generateFont(parameter2);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void interact() {
        if (selectedYes) {
            if (selectedPokemon == 0) {
                //Add Pansage
                screen.getPlayer().getPokedex().updateObtained(511);
                screen.getPlayer().getPokemonParty().add(PokemonUtils.createPokemon(3, 54, screen.getBasePokemonFactory()));
                screen.getPlayer().getPokemonParty().get(0).setCurrentHealth(50);
                screen.getPlayer().getPokemonParty().get(0).getUniqueVariables().setStatus(StatusCondition.PARALYSIS);
                screen.getPlayer().getPokemonParty().add(PokemonUtils.createPokemon(56, 100, screen.getBasePokemonFactory()));
                screen.getPlayer().getPokemonParty().get(1).getUniqueVariables().setStatus(StatusCondition.POISON);
                screen.getPlayer().getPokemonParty().add(PokemonUtils.createPokemon(50, 5, screen.getBasePokemonFactory()));
                screen.getPlayer().getPokemonParty().get(2).getUniqueVariables().setStatus(StatusCondition.BURN);
                screen.getPlayer().getPokemonParty().add(PokemonUtils.createPokemon(300, 17, screen.getBasePokemonFactory()));
                screen.getPlayer().getPokemonParty().get(3).getUniqueVariables().setStatus(StatusCondition.FROZEN);
                screen.getPlayer().getPokemonParty().add(PokemonUtils.createPokemon(500, 78, screen.getBasePokemonFactory()));
                screen.getPlayer().getPokemonParty().get(4).getUniqueVariables().setStatus(StatusCondition.SLEEP);
                screen.getPlayer().getPokemonParty().add(PokemonUtils.createPokemon(511, 11, screen.getBasePokemonFactory()));
                screen.getPlayer().getPokemonParty().get(5).setCurrentHealth(0);
            } else if (selectedPokemon == 1) {
                //Add Pansear
                screen.getPlayer().getPokedex().updateObtained(513);
                screen.getPlayer().getPokemonParty().add(PokemonUtils.createPokemon(513, 5, screen.getBasePokemonFactory()));

            } else {
                //Add Panpour
                screen.getPlayer().getPokedex().updateObtained(515);
                screen.getPlayer().getPokemonParty().add(PokemonUtils.createPokemon(515, 5, screen.getBasePokemonFactory()));
            }
            screen.setEvent(getNextEvent());
        } else {

        }
    }

    @Override
    public boolean isFinishedEvent() {
        return false;
    }

    @Override
    public void dispose() {
        textbox.dispose();
        groundBG.dispose();
        electricBG.dispose();
        waterBG.dispose();
        selectArrow.dispose();
    }

    @Override
    public void clickedUp() {
        if (!selectedYes) {
            screen.playSelectSound();
            selectedYes = true;
        }
    }

    @Override
    public void clickedDown() {
        if (selectedYes) {
            screen.playSelectSound();
            selectedYes = false;
        }
    }

    @Override
    public void clickedRight() {
        if (selectedPokemon != 2) {
            selectedPokemon++;
            if (selectedPokemon == 1) {
                selectedYes = false;
                left = new Sprite(screen.getPokemonAtlas().findRegion(middlePokemon.getFormattedImage()));
                closeLeftBall();
                openMiddleBall();
                midSound.play();
            } else if (selectedPokemon == 2) {
                selectedYes = false;
                left = new Sprite(screen.getPokemonAtlas().findRegion(rightPokemon.getFormattedImage()));
                closeMiddleBall();
                openRightBall();
                rightSound.play();
            }
            left.setSize(576, 576);
            left.setPosition(256,1200);
        }
    }

    private void openRightBall() {
        rightBall = new Sprite(new Texture("hud/starters/ball00_open.png"));
        rightBall.setSize(128, 256);
        rightBall.setPosition(326, 1630);
    }

    private void closeRightBall() {
        rightBall = new Sprite(new Texture("hud/starters/ball00.png"));
        rightBall.setSize(128, 256);
        rightBall.setPosition(326, 1630);
    }

    private void openMiddleBall() {
        middleBall = new Sprite(new Texture("hud/starters/ball00_open.png"));
        middleBall.setSize(128, 256);
        middleBall.setPosition(188, 1630);
    }

    private void closeMiddleBall() {
        middleBall = new Sprite(new Texture("hud/starters/ball00.png"));
        middleBall.setSize(128, 256);
        middleBall.setPosition(188, 1630);
    }

    private void openLeftBall() {
        leftBall = new Sprite(new Texture("hud/starters/ball00_open.png"));
        leftBall.setSize(128, 256);
        leftBall.setPosition(50, 1630);
    }

    private void closeLeftBall() {
        leftBall = new Sprite(new Texture("hud/starters/ball00.png"));
        leftBall.setSize(128, 256);
        leftBall.setPosition(50, 1630);
    }

    @Override
    public void clickedLeft() {

        if (selectedPokemon != 0) {
            selectedPokemon--;
            if (selectedPokemon == 0) {
                selectedYes = false;
                left = new Sprite(screen.getPokemonAtlas().findRegion(leftPokemon.getFormattedImage()));
                closeMiddleBall();
                openLeftBall();
                leftSound.play();
            } else if (selectedPokemon == 1) {
                selectedYes = false;
                left = new Sprite(screen.getPokemonAtlas().findRegion(middlePokemon.getFormattedImage()));
                closeRightBall();
                openMiddleBall();
                midSound.play();
            }
            left.setSize(576, 576);
            left.setPosition(256,1200);
        }
    }

    private String getDescription() {
        String type;
        if (selectedPokemon == 0) {
            type = PokemonType.toString(leftPokemon.getFirstType());
        } else if (selectedPokemon == 1) {
            type = PokemonType.toString(middlePokemon.getFirstType());
        } else {
            type = PokemonType.toString(rightPokemon.getFirstType());
        }
        Gdx.app.log("length", TextFormater.getWordLengthValue("The " + type + "-type Pokemon") + "");
        return "The " + type + "-type Pokemon";
    }

    private String getPokemonName() {
        if (selectedPokemon == 0) {
            return leftPokemon.getName();
        } else if (selectedPokemon == 1) {
            return middlePokemon.getName();
        } else {
            return rightPokemon.getName();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (selectedPokemon == 0) {
            batch.draw(groundBG, 0, 960, 1080, 959);
        } else if (selectedPokemon == 1) {
            batch.draw(electricBG, 0, 960, 1080, 959);
        } else if (selectedPokemon == 2) {
            batch.draw(waterBG, 0, 960, 1080, 959);
        }
        batch.draw(textbox, 0, 1000, 1080, 250);
        batch.draw(textbox, 830, 1260, 205, 220);
        if (left != null) {
            left.draw(batch);
        }
        leftBall.draw(batch);
        middleBall.draw(batch);
        rightBall.draw(batch);

        descriptionFont.draw(batch, getDescription(), 300, 1180);
        descriptionFont.draw(batch, getPokemonName(), 410, 1120);

        descriptionFont.draw(batch, "No", 935, 1360);
        descriptionFont.draw(batch, "Yes", 915, 1430);
        if (selectedYes) {
            batch.draw(selectArrow, 875, 1385, 24, 56);
        } else {
            batch.draw(selectArrow, 875, 1315, 24, 56);
        }
    }
}
