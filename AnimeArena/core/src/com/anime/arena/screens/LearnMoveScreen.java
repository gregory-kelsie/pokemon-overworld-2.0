package com.anime.arena.screens;

import com.anime.arena.AnimeArena;
import com.anime.arena.battle.BattleStep;
import com.anime.arena.battle.ExpState;
import com.anime.arena.pokemon.Pokemon;
import com.anime.arena.pokemon.PokemonType;
import com.anime.arena.skill.Skill;
import com.anime.arena.skill.SkillCategory;
import com.anime.arena.tools.TextFormater;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LearnMoveScreen implements Screen {
    private AnimeArena game;
    private Texture black;
    private Texture background;
    private Texture moveSelector;
    private OrthographicCamera gameCam;
    private OrthographicCamera controlsCam;
    private Viewport gamePort;

    private Screen previousScreen;


    private Pokemon learningPokemon;
    private Skill newMove;
    private String moveDescription;
    private int selectorPosition;

    private BitmapFont darkFont;
    private BitmapFont whiteFont;

    private Sprite currentCategorySprite;
    private Sprite pokemonIconSprite;

    //Pokemon type icons
    private Sprite typeOneSprite;
    private Sprite typeTwoSprite;

    //Move type sprites
    private Sprite firstMoveSprite;
    private Sprite secondMoveSprite;
    private Sprite thirdMoveSprite;
    private Sprite fourthMoveSprite;
    private Sprite newMoveSprite;

    private TextureAtlas pokemonTypeAtlas;
    private TextureAtlas pokemonIconAtlas;
    private TextureAtlas categoryAtlas;

    private ExpState expState;

    public LearnMoveScreen(AnimeArena game, Screen previousScreen, TextureAtlas pokemonIconAtlas, TextureAtlas pokemonTypeAtlas, TextureAtlas categoryAtlas,
                           Pokemon learningPokemon, Skill newMove, ExpState expState) {
        this.game = game;
        this.pokemonIconAtlas = pokemonIconAtlas;
        this.pokemonTypeAtlas = pokemonTypeAtlas;
        this.categoryAtlas = categoryAtlas;
        this.previousScreen = previousScreen;
        this.expState = expState;

        this.selectorPosition = 0;
        this.learningPokemon = learningPokemon;
        this.newMove = newMove;
        this.moveDescription = getMoveDescription();
        this.currentCategorySprite = createCategoryIcon();
        this.pokemonIconSprite = new Sprite(pokemonIconAtlas.findRegion(learningPokemon.getConstantVariables().getFormattedImage()));
        pokemonIconSprite.setPosition(20, 1635);
        pokemonIconSprite.setSize(pokemonIconSprite.getWidth() * 4, pokemonIconSprite.getHeight() * 4);

        this.black = new Texture("animation/black.png");
        this.background = new Texture("hud/party/learnmove.png");
        this.moveSelector = new Texture("hud/party/move_cursor.png");

        initFont();
        initTypeSprites();
        initCamera();

        gameCam.position.set((AnimeArena.V_WIDTH / 2) / AnimeArena.PPM, (AnimeArena.V_HEIGHT / 2) / AnimeArena.PPM, 0);
    }

    private void initTypeSprites() {
        typeOneSprite = createTypeIcon(learningPokemon.getConstantVariables().getFirstType());
        typeOneSprite.setPosition(180, 1680);
        if (learningPokemon.getConstantVariables().getSecondType() != PokemonType.NONE) {
            typeTwoSprite = createTypeIcon(learningPokemon.getConstantVariables().getSecondType());
            typeTwoSprite.setPosition(320, 1680);
        }
        firstMoveSprite = createTypeIcon(learningPokemon.getUniqueVariables().getMoves().get(0).getMoveType());
        firstMoveSprite.setPosition(560, 1800);
        secondMoveSprite = createTypeIcon(learningPokemon.getUniqueVariables().getMoves().get(1).getMoveType());
        secondMoveSprite.setPosition(560, 1640);
        thirdMoveSprite = createTypeIcon(learningPokemon.getUniqueVariables().getMoves().get(2).getMoveType());
        thirdMoveSprite.setPosition(560, 1480);
        fourthMoveSprite = createTypeIcon(learningPokemon.getUniqueVariables().getMoves().get(3).getMoveType());
        fourthMoveSprite.setPosition(560, 1320);
        newMoveSprite = createTypeIcon(newMove.getMoveType());
        newMoveSprite.setPosition(560, 1110);
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

        //Dark Font
        FreeTypeFontGenerator generator25 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pkmnem.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter abilityParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        abilityParameter.size = 48;
        abilityParameter.color = Color.BLACK;
        abilityParameter.spaceY = 48;
        abilityParameter.spaceX = 0;
        abilityParameter.borderColor = Color.BLACK;
        abilityParameter.shadowColor = Color.GRAY;
        abilityParameter.shadowOffsetX = 1;
        abilityParameter.shadowOffsetY = 1;

        darkFont = generator25.generateFont(abilityParameter);
        whiteFont = generator.generateFont(parameter2);
    }

    private void initCamera() {
        gameCam = new OrthographicCamera();
        controlsCam = new OrthographicCamera();
        controlsCam.setToOrtho(false, 1080, 1920);
        gamePort = new FitViewport(AnimeArena.V_WIDTH / AnimeArena.PPM, AnimeArena.V_HEIGHT / AnimeArena.PPM, gameCam);
        gamePort.apply();
    }

    private void drawText(Batch batch) {
        whiteFont.draw(batch, "MOVES", 60, 1850);
        whiteFont.draw(batch, "CATEGORY", 60, 1590);
        whiteFont.draw(batch, "POWER", 60, 1510);
        whiteFont.draw(batch, "ACCURACY", 60, 1440);

        darkFont.draw(batch, getBasePower(), 360, 1510);
        darkFont.draw(batch, getAccuracy(), 360, 1440);
        darkFont.draw(batch, moveDescription, 10, 1330);

        Skill firstMove = learningPokemon.getUniqueVariables().getMoves().get(0);
        Skill secondMove = learningPokemon.getUniqueVariables().getMoves().get(1);
        Skill thirdMove = learningPokemon.getUniqueVariables().getMoves().get(2);
        Skill fourthMove = learningPokemon.getUniqueVariables().getMoves().get(3);

        whiteFont.draw(batch, firstMove.getName(), 680, 1840);
        whiteFont.draw(batch, "PP", 720, 1770);
        whiteFont.draw(batch, firstMove.getCurrentPP() + "/" + firstMove.getMaxPP(), 855, 1770);

        whiteFont.draw(batch, secondMove.getName(), 680, 1680);
        whiteFont.draw(batch, "PP", 720, 1610);
        whiteFont.draw(batch, secondMove.getCurrentPP() + "/" + secondMove.getMaxPP(), 855, 1610);

        whiteFont.draw(batch, thirdMove.getName(), 680, 1520);
        whiteFont.draw(batch, "PP", 720, 1450);
        whiteFont.draw(batch, thirdMove.getCurrentPP() + "/" + thirdMove.getMaxPP(), 855, 1450);


        whiteFont.draw(batch, fourthMove.getName(), 680, 1360);
        whiteFont.draw(batch, "PP", 720, 1290);
        whiteFont.draw(batch, fourthMove.getCurrentPP() + "/" + fourthMove.getMaxPP(), 855, 1290);

        whiteFont.draw(batch, newMove.getName(), 680, 1150);
        whiteFont.draw(batch, "PP", 720, 1080);
        whiteFont.draw(batch, newMove.getCurrentPP() + "/" + newMove.getMaxPP(), 855, 1080);
    }

    private Skill getCurrentMove() {
        if (selectorPosition < 4) {
            return learningPokemon.getUniqueVariables().getMoves().get(selectorPosition);
        } else {
            return newMove;
        }
    }

    private String getMoveDescription() {
        return TextFormater.formatText(getCurrentMove().getDescription(), 25.0);
    }

    private String getBasePower() {
        int basePower = getCurrentMove().getBasePower();
        if (basePower > 0) {
            return Integer.toString(basePower);
        } else {
            return "--";
        }
    }

    private String getAccuracy() {
        int accuracy = getCurrentMove().getAccuracy();
        if (accuracy > 0) {
            return Integer.toString(accuracy) + "%";
        } else {
            return "--";
        }
    }

    public Sprite createCategoryIcon() {
        SkillCategory category = getCurrentMove().getCategory();
        Sprite s;
        if (category == SkillCategory.PHYSICAL) {
            s = new Sprite(categoryAtlas.findRegion("physical"));
        } else if (category == SkillCategory.SPECIAL) {
            s = new Sprite(categoryAtlas.findRegion("special"));
        } else {
            s = s = new Sprite(categoryAtlas.findRegion("misc"));
        }
        s.setSize(112, 48);
        s.setPosition(350, 1550);
        return s;
    }

    public Sprite createTypeIcon(PokemonType type) {
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
        return s;
    }



    @Override
    public void show() {

    }

    public void update(float dt) {
        handleInput(dt);

    }

    private void handleInput(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (selectorPosition > 0) {
                selectorPosition--;
                refreshCurrentMove();
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (selectorPosition < 4) {
                selectorPosition++;
                refreshCurrentMove();
            }
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.X))) {
            closeScreen(BattleStep.CHECK_NEW_MOVES);
        } else if (Gdx.input.isKeyJustPressed((Input.Keys.Z))) {
            if (selectorPosition >= 0 && selectorPosition < 4) {
                expState.setForgottenMoveIndex(selectorPosition);
                closeScreen(BattleStep.LEARN_NEW_MOVE);
            }
        }

    }

    private void refreshCurrentMove() {
        moveDescription = getMoveDescription();
        currentCategorySprite = createCategoryIcon();
    }

    public void closeScreen(BattleStep battleStep) {
        expState.setBattleStep(battleStep);
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

        game.getBatch().end();
    }


    private void drawScreen(SpriteBatch batch) {
        batch.draw(background,0, 960, 1080, 960);
        pokemonIconSprite.draw(batch);
        drawText(batch);
        drawPokemonType(batch);
        drawMoveTypes(batch);
        batch.draw(moveSelector,504, getCursorY(), 574, 170);

    }

    private int getCursorY() {
        int cursorY = 1708;
        if (selectorPosition == 0) {
            cursorY = 1708;
        }
        else if (selectorPosition == 1) {
            cursorY = 1545;
        } else if (selectorPosition == 2) {
            cursorY = 1388;
        } else if (selectorPosition == 3) {
            cursorY = 1225;
        } else {
            cursorY = 1015;
        }
        return cursorY;
    }

    private void drawPokemonType(SpriteBatch batch) {
        typeOneSprite.draw(batch);
        if (typeTwoSprite != null) {
            typeTwoSprite.draw(batch);
        }
    }

    private void drawMoveTypes(SpriteBatch batch) {
        firstMoveSprite.draw(batch);
        secondMoveSprite.draw(batch);
        thirdMoveSprite.draw(batch);
        fourthMoveSprite.draw(batch);
        newMoveSprite.draw(batch);
        currentCategorySprite.draw(batch);
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
