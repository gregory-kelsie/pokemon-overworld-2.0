package com.anime.arena.battle.ui;

import com.anime.arena.battle.BattleState;
import com.anime.arena.battle.ExpState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.ArrayList;
import java.util.List;

public class CustomBattleTextController implements UIComponent {

    public enum CustomBattleTextResult {LEARNED_NEW_MOVE, OPEN_MOVE_LEARNER, STOPPED_LEARNING_MOVE};
    protected CustomBattleText currentTextbox;
    protected int textPosition;
    protected double textCounter;
    protected double afterTextBoxDelay;

    protected boolean isYes;

    protected BitmapFont regularFont;
    protected Texture optionBox;
    protected Texture selectArrowTexture;

    protected boolean hasClickedThroughTextBox;



    private BattleState currentBattleState;
    public CustomBattleTextController(BattleState currentBattleState, CustomBattleText text) {
        init();
        this.currentBattleState = currentBattleState;
        currentTextbox = text;
    }


    private void init() {
        this.textPosition = 0;
        this.textCounter = 0;
        this.afterTextBoxDelay = 0;
        this.isYes = true;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pkmndp.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;
        parameter.spaceY = 20;

        regularFont = generator.generateFont(parameter); // font size 12 pixels
        regularFont.setColor(Color.BLACK);
        optionBox = new Texture("hud/optionbox.png");
        this.selectArrowTexture = new Texture("hud/selarrow.png");
        hasClickedThroughTextBox = false;
    }

    public void reset() {
        this.textPosition = 0;
        this.textCounter = 0;
        this.afterTextBoxDelay = 0;
        hasClickedThroughTextBox = false;
    }


    private boolean isDisplayingAllText() {
        return textPosition == currentTextbox.getTextLength();
    }


    public boolean isFinished() {
        return isDisplayingAllText() && isDoneLastTextBox() && isFinishedAfterTextBoxDelay();
    }

    @Override
    public void returnToBattleState() {
        if (!currentTextbox.hasOptions()) {
            currentBattleState.setCustomTextResult(currentTextbox.getDefaultResult());
            //Send the result of the custom battle text box to the battle state. The battle state will know what BattleStep to go to
            // after this ui update and then use that battle text result to determine where to go next.
            currentBattleState.removeUIComponent();
        } else {
            if (currentTextbox.isSelectedYes()) {
                currentBattleState.setCustomTextResult(currentTextbox.getYesResult());
            } else {
                currentBattleState.setCustomTextResult(currentTextbox.getDefaultResult());
            }
            currentBattleState.removeUIComponent();
        }
    }

    @Override
    public void click() {
        if (isDisplayingAllText() && isFinishedAfterTextBoxDelay()) {
            if (currentTextbox.hasOptions() && !currentTextbox.hasSelectedOption()) {
                if (isYes) {
                    currentTextbox.setSelectedOption(true);
                } else {
                    currentTextbox.setSelectedOption(false);
                }
            }
            hasClickedThroughTextBox = true;
        }
    }

    @Override
    public void clickUp() {
        if (!isYes) {
            isYes = true;
        }
    }

    @Override
    public void clickDown() {
        if (isYes) {
            isYes = false;
        }
    }


    protected boolean isDoneLastTextBox() {
       if (!currentTextbox.hasOptions()) {
           return currentTextbox.getNextBattleText() == null;
       } else {
           if (currentTextbox.hasSelectedOption()) {
               if (currentTextbox.isSelectedYes() && currentTextbox.getNextBattleTextForYes() == null) {
                   return true;
               } else if (!currentTextbox.isSelectedYes() && currentTextbox.getNextBattleTextForNo() == null) {
                   return true;
               }
           }
           return false;
       }
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
            if (!isDoneLastTextBox() && hasClickedThroughTextBox) {
                hasClickedThroughTextBox = false;
                currentTextbox.removeSelectedOption();
                setNextTextBox();
            } else if (isDoneLastTextBox() && hasClickedThroughTextBox) {
                Gdx.app.log("CustomBattleTextController", "wowowow");
            }
        }
    }


    public void render(Batch batch) {
        if (currentTextbox != null) {
            regularFont.draw(batch, currentTextbox.getText().substring(0, textPosition), 100, 1130);
            if (currentTextbox.hasOptions() && isDisplayingAllText()) {
                drawOptionsBox(batch);
            }
        }
    }

    private void drawOptionsBox(Batch batch) {
        batch.draw(optionBox, 805, 1225, 244, 140);
        regularFont.draw(batch, "Yes", 880, 1340);
        regularFont.draw(batch, "No", 880, 1286);
        int arrowY = isYes ? 1302 : 1245;
        batch.draw(selectArrowTexture, 845, arrowY, 24, 56);
        //no y for arrow is 1245
    }

    protected void setNextTextBox() {
        textCounter = 0;
        textPosition = 0;
        afterTextBoxDelay = 0;
        if (!currentTextbox.hasOptions()) {
            currentTextbox = currentTextbox.getNextBattleText();
        } else {
            if (isYes) {
                currentTextbox.removeSelectedOption();
                currentTextbox = currentTextbox.getNextBattleTextForYes();
            } else {
                currentTextbox.removeSelectedOption();
                currentTextbox = currentTextbox.getNextBattleTextForNo();
            }
            isYes = true;
        }
    }

    protected boolean isFinishedAfterTextBoxDelay() {
        return afterTextBoxDelay >= 0.5;
    }

    protected void updateAfterTextBoxDelay(float dt) {
        afterTextBoxDelay += dt;
    }

    protected void updateTextCounter(float dt) {
        textCounter += dt;
        if (textCounter >= 0.03) {
            if (textPosition < currentTextbox.getTextLength()) {
                textPosition += 1;
                textCounter = 0;
            }
        }
    }
}
