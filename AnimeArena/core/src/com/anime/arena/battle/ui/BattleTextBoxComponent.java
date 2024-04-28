package com.anime.arena.battle.ui;

import com.anime.arena.battle.BattleState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.ArrayList;
import java.util.List;

public class BattleTextBoxComponent extends BattleTextBox implements UIComponent {


    private BattleState currentState;

    public BattleTextBoxComponent(BattleState currentState, String text) {
        super(text);
        this.currentState = currentState;
    }
    public BattleTextBoxComponent(BattleState currentState, List<String> text) {
        super(text);
        this.currentState = currentState;
    }

    private boolean isDisplayingAllText() {
       return textPosition == textboxes.get(currentTextbox).length();
    }

    @Override
    public boolean isFinished() {
        if (this.finishType == BattleTextBoxFinish.TRIGGER) {
            return isFinishedAfterTextBoxDelay() && hasClickedThroughLastTextBox;
        }
        return isDisplayingAllText() && isDisplayingLastTextBox() & isFinishedAfterTextBoxDelay();
    }

    public void update(float dt) {
        if (!isFinished()) {
            componentUpdate(dt);
        } else {
            returnToBattleState();
        }
    }

    public void componentUpdate(float dt) {
        if (!isDisplayingAllText()) {
            updateTextCounter(dt);
        } else if (!isFinishedAfterTextBoxDelay()) {
            updateAfterTextBoxDelay(dt);
        } else {
            if (!isDisplayingLastTextBox()) {
                setNextTextBox();
            }
        }
    }

    public void returnToBattleState() {
        currentState.removeUIComponent();
    }

    @Override
    public void click() {
        this.clicked();
    }

    @Override
    public void clickUp() {

    }

    @Override
    public void clickDown() {

    }

}
