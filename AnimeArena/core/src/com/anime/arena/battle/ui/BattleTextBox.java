package com.anime.arena.battle.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.ArrayList;
import java.util.List;

public class BattleTextBox {

    public enum BattleTextBoxFinish {CLICK, TRIGGER, DELAY};
    protected List<String> textboxes;
    protected int currentTextbox;
    protected CustomBattleText currentCustomTextBox;
    protected int textPosition;
    protected double textCounter;
    protected double afterTextBoxDelay;

    protected BitmapFont regularFont;

    protected boolean isAutomaticFinish;
    protected boolean hasClickedThroughLastTextBox;

    protected BattleTextBoxFinish finishType;
    protected boolean hasTriggered;

    public BattleTextBox(String text) {
        init();
        textboxes = new ArrayList<String>();
        textboxes.add(text);
    }

    public BattleTextBox(List<String> text) {
        init();
        this.textboxes = text;
    }

    public BattleTextBox(CustomBattleText text) {
        init();
        this.currentCustomTextBox = text;
    }

    private void init() {
        this.currentTextbox = 0;
        this.textPosition = 0;
        this.textCounter = 0;
        this.afterTextBoxDelay = 0;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pkmndp.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;
        parameter.spaceY = 20;

        regularFont = generator.generateFont(parameter); // font size 12 pixels
        regularFont.setColor(Color.BLACK);
        hasClickedThroughLastTextBox = false;
        finishType = BattleTextBoxFinish.CLICK;
        isAutomaticFinish = false;
        hasTriggered = false;
    }

    public void reset() {
        this.currentTextbox = 0;
        this.textPosition = 0;
        this.textCounter = 0;
        this.afterTextBoxDelay = 0;
        hasClickedThroughLastTextBox = false;
        hasTriggered = false;
    }

    public void setFinishType(BattleTextBoxFinish finishType) {
        this.finishType = finishType;
    }

    public void triggerClosingTextBox() {
        if (finishType == BattleTextBoxFinish.TRIGGER) {
            hasTriggered = true;
        }
    }

    public void setAutomaticFinish(boolean automaticFinish) {
        if (automaticFinish) {
            finishType = BattleTextBoxFinish.DELAY;
        }
        this.isAutomaticFinish = automaticFinish;
    }

    private boolean isDisplayingAllText() {
       return textPosition == textboxes.get(currentTextbox).length();
    }


    public boolean isFinished() {
        return isDisplayingAllText() && isDisplayingLastTextBox() && finishedTextBoxCondition();//(isFinishedAfterTextBoxDelay() || isAutomaticFinish) && (hasClickedThroughLastTextBox || isAutomaticFinish);
    }

    public boolean isFinishedAndNotTriggered() {
        return isDisplayingAllText() && isDisplayingLastTextBox();
    }

    public boolean finishedTextBoxCondition() {
        if (finishType == BattleTextBoxFinish.TRIGGER) {
            return hasTriggered;
        } else if (finishType == BattleTextBoxFinish.CLICK) {
            return isFinishedAfterTextBoxDelay() && hasClickedThroughLastTextBox;
        } else if (finishType == BattleTextBoxFinish.DELAY) {
            return isFinishedAfterTextBoxDelay();
        }
        return false;
    }

    protected boolean isDisplayingLastTextBox() {
        return currentTextbox == textboxes.size() - 1;
    }


    public void update(float dt) {
        if (!isFinished()) {
            componentUpdate(dt);
        }
    }

    public void componentUpdate(float dt) {
        if (!isDisplayingAllText()) {
            updateTextCounter(dt);
        } else if (!isFinishedAfterTextBoxDelay()) {
            updateAfterTextBoxDelay(dt);
        } else {
            if (!isDisplayingLastTextBox() && hasClickedThroughLastTextBox) {
                hasClickedThroughLastTextBox = false;
                setNextTextBox();
            }
        }
    }

    public void clicked() {
        if (isDisplayingAllText() && isFinishedAfterTextBoxDelay()) {
            hasClickedThroughLastTextBox = true;
        }
    }

    public void render(Batch batch) {
        regularFont.draw(batch, textboxes.get(currentTextbox).substring(0, textPosition), 100, 1130);
    }

    protected void setNextTextBox() {
        textCounter = 0;
        textPosition = 0;
        afterTextBoxDelay = 0;
        currentTextbox++;
    }

    protected boolean isFinishedAfterTextBoxDelay() {
        return afterTextBoxDelay >= 1.5;
    }

    protected void updateAfterTextBoxDelay(float dt) {
        afterTextBoxDelay += dt;
    }

    protected void updateTextCounter(float dt) {
        textCounter += dt;
        if (textCounter >= 0.03) {
            if (textPosition < textboxes.get(currentTextbox).length()) {
                textPosition += 1;
                textCounter = 0;
            }
        }
    }
}
