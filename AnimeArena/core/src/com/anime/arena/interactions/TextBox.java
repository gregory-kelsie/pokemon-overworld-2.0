package com.anime.arena.interactions;


import com.anime.arena.objects.Player;
import com.anime.arena.screens.PlayScreen;
import com.anime.arena.tools.TextFormater;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.*;

/**
 * Created by gregorykelsie on 2018-11-25.
 */

public class TextBox extends Event {

    private static final int NO_OPTION_SELECTED = -1;

    protected String text;
    protected TextBox nextTextBox;

    protected LinkedHashMap<String, Event> options;
    protected double textCounter;
    protected int textPosition;
    protected double triangleCounter;
    protected boolean displayTriangle; //When the triangle is displayed. It's flashing so it switches frequently.
    protected int optionSelector;

    protected boolean hasNextTextBox; //Another textbox after this one. No options or anything else.


    private Texture textBox;
    private Texture optionBox;
    private Texture textBoxTriangle;
    private Texture mapNameBoxTexture;
    private BitmapFont regularFont;
    private BitmapFont selectedFont;

    private Music jingle;
    private boolean isPlaying;


    public TextBox(PlayScreen screen, String text) {
        super(screen);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pkmndp.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;
        parameter.spaceY = 20;
//        parameter.shadowColor = Color.GRAY;
//        parameter.shadowOffsetX = 1;
//        parameter.shadowOffsetY = 1;
        regularFont = generator.generateFont(parameter); // font size 12 pixels
        this.text = TextFormater.formatText(text);
        initCounters();
        hasNextTextBox = false;
        options = new LinkedHashMap<String, Event>();
        optionSelector = NO_OPTION_SELECTED;

        textBox = new Texture("hud/textborder.png");
        optionBox = new Texture("hud/optionbox.png");
        mapNameBoxTexture = new Texture("hud/maptitleborder.png");
        textBoxTriangle = new Texture("hud/triangle.png");
        //regularFont = new BitmapFont(Gdx.files.internal("hud/regularFont.fnt"));
        selectedFont = new BitmapFont(Gdx.files.internal("hud/regularFont.fnt"));
        regularFont.setColor(Color.BLACK);
        selectedFont.setColor(Color.RED);

        isPlaying = false;
    }

    public TextBox(PlayScreen screen, String text, String jingle) {
        this(screen, text);
        this.jingle = Gdx.audio.newMusic(Gdx.files.internal(jingle));
    }



    /**
     * Initialize the counters and variables that maintain the text in the textbox and when it appears.
     */
    private void initCounters() {
        textCounter = 0;
        textPosition = 0;
        triangleCounter = 0;
        this.displayTriangle = false;
    }

    public void addOption(String optionName, Event optionEvent) {
        options.put(optionName, optionEvent);
    }

    public Set<String> getOptionNames() {
        return options.keySet();
    }

    public String getOption(int index) {
        if (index >= 0 && index < options.keySet().size()) {
            return new ArrayList<String>(options.keySet()).get(index);
        }
        return "";
    }

    public void refreshTextBox() {
        textCounter = 0;
        textPosition = 0;
        triangleCounter = 0;
        displayTriangle = false;
    }

    public boolean hasOptions() {
        return options.size() > 0;
    }


    public void setNextEvent(Event nextEvent) {
        this.nextEvent = nextEvent;
        if (nextEvent instanceof TextBox) {
            hasNextTextBox = true;
        }
    }

    @Override
    public void interact() {
        if (isFinishedRendering()) {
            if (jingle != null && !jingle.isPlaying()) {
                jingle.stop();
                screen.playSelectSound();
                screen.playBgm();
                textPosition = 0;
                screen.setEvent(getNextEvent());
            } else if (jingle == null) {
                screen.playSelectSound();
                screen.playBgm();
                textPosition = 0;
                screen.setEvent(getNextEvent());
            }

        } else {
            quickUpdate();
        }
    }

    @Override
    public void dispose() {
        textBox.dispose();
        textBoxTriangle.dispose();
        regularFont.dispose();
        optionBox.dispose();
        selectedFont.dispose();
        if (jingle != null) {
            jingle.dispose();
        }
    }

    @Override
    public void clickedUp() {
        getPreviousOption();
    }

    @Override
    public void clickedDown() {
        getNextOption();
    }

    @Override
    public void clickedLeft() {

    }

    @Override
    public void clickedRight() {

    }

    @Override
    public boolean isFinishedEvent() {
        return false;
    }

    public Event getNextEvent() {
        if (hasOptions() && optionSelector != NO_OPTION_SELECTED) {
            List<Event> events = new ArrayList<Event>(options.values());
            return events.get(optionSelector);
        } else {
            return nextEvent;
        }
    }

    public boolean hasNextEvent() {
        if (hasOptions() || nextEvent != null) {
            return true;
        }
        return false;
    }

    public void getNextOption() {
        if (optionSelector != NO_OPTION_SELECTED && optionSelector < options.size() - 1) {
            optionSelector++;
        } else {
            optionSelector = 0;
        }
    }

    public void getPreviousOption() {
        if (optionSelector != NO_OPTION_SELECTED && optionSelector != 0) {
            optionSelector--;
        } else if (optionSelector == 0) {
            optionSelector = options.size() - 1;
        }
    }


    public void update(float dt) {
        if (!isPlaying && jingle != null) {
            screen.stopBgm();
            jingle.play();
            isPlaying = true;
        }
        if (!isFinishedRendering()) {
            textCounter += dt;
            if (textCounter >= 0.03) {
                if (textPosition < text.length()) {
                    textPosition += 1;
                    textCounter = 0;
                }
            }
        } else {
            if (!hasOptions() && hasNextTextBox) {
                if (jingle == null || jingle != null && !jingle.isPlaying()) {
                    if (triangleCounter >= 0.35) {
                        triangleCounter = 0;
                        displayTriangle = !displayTriangle;
                    } else {
                        triangleCounter += dt;
                    }
                }
            }
            if (hasOptions() && optionSelector == NO_OPTION_SELECTED) {
                optionSelector = 0;
            }
        }

    }

    public void quickUpdate() {
        textPosition = text.length();
        if (options.size() > 0) {
            optionSelector = 0; //First option is the top option
        }
    }

    public boolean isFinishedRendering() {
        if (textPosition == text.length()) {

            return true;
        }
        return false;
    }

    public void render(SpriteBatch batch) {
        batch.draw(textBox, 40, 968, 1000, 218); //Was 178
        regularFont.draw(batch, text.substring(0, textPosition), 100, 1130);
        if (displayTriangle && isFinishedRendering()) {
            batch.draw(textBoxTriangle, 910, 1008, 50, 30);
        }
        if (isFinishedRendering() && hasOptions()) {
            batch.draw(optionBox, 785, 1200, 244, 140);
            if (optionSelector == 0) {
                selectedFont.draw(batch, getOption(0), 845, 1315);
                regularFont.draw(batch, getOption(1), 845, 1261);
            } else if (optionSelector == 1) {
                regularFont.draw(batch, getOption(0), 845, 1315);
                selectedFont.draw(batch, getOption(1), 845, 1261);
            }
        }
    }


}