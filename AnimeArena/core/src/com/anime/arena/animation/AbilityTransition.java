package com.anime.arena.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class AbilityTransition {
    private String abilityUserName;
    private String abilityName;
    private BitmapFont abilityMessageFont;
    private Texture playerAbilityMessageTexture;

    private final float POPUP_SPEED = 1000.0f;

    private final float STARTING_ABILITY_X = -414.0f;
    private final float STARTING_USER_NAME_X = -364.0f;
    private final float STARTING_ABILITY_NAME_X = -364.0f;

    private final float STARTING_ABILITY_X_OPP = 1080 + 414.0f;
    private final float STARTING_OPPONENT_NAME_X = 1080 + 464.0f;
    private final float STARTING_ABILITY_NAME_X_OPP = 1080 + 464.0f;

    private final float FINAL_ABILITY_X = 0.0f;
    private final float FINAL_USER_NAME_X = 50.0f;
    private final float FINAL_ABILITY_NAME_X = 50.0f;

    private final float FINAL_ABILITY_X_OPP = 666.0f;
    private final float FINAL_OPPONENT_NAME_X = 716.0f;
    private final float FINAL_ABILITY_NAME_X_OPP = 716.0f;

    private float currentAbilityX;
    private float currentUserNameX;
    private float currentAbilityNameX;

    private float preTransitionDelay;
    private float afterPopupDelay;

    private enum TransitionType {POP_UP, REMOVE}

    private TransitionType transitionState;
    private boolean displayAbilityTransition;

    private boolean isPlayerAbility;

    public AbilityTransition(boolean isPlayerAbility) {
        this.isPlayerAbility = isPlayerAbility;
        this.abilityUserName = "";
        this.abilityUserName = "";
        this.afterPopupDelay = 0.0f;
        if (isPlayerAbility) {
            this.playerAbilityMessageTexture = new Texture("battle/ui/abilityMessagePlayer.png");
        } else {
            this.playerAbilityMessageTexture = new Texture("battle/ui/abilityMessageOpponent.png");
        }
        resetTransition();
        initFont();
    }

    public void resetTransition() {
        currentAbilityX = getStartingAbilityX();
        currentUserNameX = getStartingUserNameX();
        currentAbilityNameX = getStartingAbilityNameX();
        transitionState = TransitionType.POP_UP;
        displayAbilityTransition = false;
    }

    private float getStartingAbilityX() {
        return isPlayerAbility ? STARTING_ABILITY_X : STARTING_ABILITY_X_OPP;
    }

    private float getStartingUserNameX() {
        return isPlayerAbility ? STARTING_USER_NAME_X : STARTING_OPPONENT_NAME_X;
    }

    private float getStartingAbilityNameX() {
        return isPlayerAbility ? STARTING_ABILITY_NAME_X : STARTING_ABILITY_NAME_X_OPP;
    }

    private float getFinalAbilityX() {
        return isPlayerAbility ? FINAL_ABILITY_X : FINAL_ABILITY_X_OPP;
    }

    private float getFinalUserNameX() {
        return isPlayerAbility ? FINAL_USER_NAME_X : FINAL_OPPONENT_NAME_X;
    }

    private float getFinalAbilityNameX() {
        return isPlayerAbility ? FINAL_ABILITY_NAME_X : FINAL_ABILITY_NAME_X_OPP;
    }


    private void initFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pkmnems.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter abilityMessageParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        abilityMessageParam.size = 40;
        abilityMessageParam.borderWidth = 4;
        abilityMessageParam.borderColor = Color.BLACK;
        abilityMessageParam.color = Color.WHITE;
        abilityMessageParam.spaceY = 20;
        abilityMessageParam.spaceX = -4;
        abilityMessageFont = generator.generateFont(abilityMessageParam);
    }

    public boolean isDisplaying() {
        return displayAbilityTransition;
    }

    public void setAbilityName(String newAbilityName) {
        this.abilityName = newAbilityName;
    }

    public void setUserName(String newUserName) {
        this.abilityUserName = newUserName;
    }

    public void update(float dt) {
        if (transitionState == TransitionType.POP_UP) {
            if (preTransitionDelay <= 0.0f) {
                updatePopupAbilityMessageTexture(dt);
                updatePopupAbilityName(dt);
                updatePopupUserName(dt);
            } else {
                preTransitionDelay -= dt;
                preTransitionDelay = Math.max(0.0f, preTransitionDelay);
            }
            if (currentAbilityX == getFinalAbilityX() && currentAbilityNameX == getFinalAbilityNameX() && currentUserNameX == getFinalUserNameX()) {
                afterPopupDelay += dt;
                afterPopupDelay = Math.min(1.5f, afterPopupDelay);
            }
        } else if (transitionState == TransitionType.REMOVE) {
            updateRemoveAbilityMessageTexture(dt);
            updateRemoveAbilityName(dt);
            updateRemoveUserName(dt);
        }
    }

    public boolean isDonePoppingUp() {
        if (transitionState == TransitionType.POP_UP) {
            return currentAbilityX == getFinalAbilityX() && currentAbilityNameX == getFinalAbilityNameX() && currentUserNameX == getFinalUserNameX() && afterPopupDelay == 1.5f;
        }
        return false;
    }

    public boolean isPoppingUp() {
        return transitionState == TransitionType.POP_UP;
    }

    public boolean isDoneRemovingPopup() {
        if (transitionState == TransitionType.REMOVE) {
            return currentAbilityX == getStartingAbilityX() && currentAbilityNameX == getStartingAbilityNameX() && currentUserNameX == getStartingUserNameX();
        }
        return false;
    }

    public void render(SpriteBatch batch) {
        if (isPlayerAbility) {
            batch.draw(playerAbilityMessageTexture, currentAbilityX, 1470, 414, 84);
            abilityMessageFont.draw(batch, abilityUserName + "'s", currentUserNameX, 1540);
            abilityMessageFont.draw(batch, abilityName, currentAbilityNameX, 1505);
        } else {
            batch.draw(playerAbilityMessageTexture, currentAbilityX, 1770, 414, 84);
            abilityMessageFont.draw(batch, abilityUserName + "'s", currentUserNameX, 1840);
            abilityMessageFont.draw(batch, abilityName, currentAbilityNameX, 1805);
        }
    }

    public void removeAbilityDisplay() {
        transitionState = TransitionType.REMOVE;
        afterPopupDelay = 0.0f;
    }

    public void popupAbilityDisplay() {
        transitionState = TransitionType.POP_UP;
        displayAbilityTransition = true;
        preTransitionDelay = 0.0f;
    }

    public void popupAbilityDisplay(float delay) {
        popupAbilityDisplay();
        preTransitionDelay = delay;
    }

    private void updatePopupAbilityMessageTexture(float dt) {
        if (isPlayerAbility && currentAbilityX < FINAL_ABILITY_X) {
            currentAbilityX += (dt * POPUP_SPEED);
            currentAbilityX = Math.min(FINAL_ABILITY_X, currentAbilityX);
        } else if (!isPlayerAbility && currentAbilityX > FINAL_ABILITY_X_OPP) {
            currentAbilityX -= (dt * POPUP_SPEED);
            currentAbilityX = Math.max(FINAL_ABILITY_X_OPP, currentAbilityX);
        }
    }

    private void updatePopupAbilityName(float dt) {
        if (currentAbilityNameX < FINAL_ABILITY_NAME_X) {
            currentAbilityNameX += (dt * POPUP_SPEED);
            currentAbilityNameX = Math.min(FINAL_ABILITY_NAME_X, currentAbilityNameX);
        } else if (!isPlayerAbility && currentAbilityNameX > FINAL_ABILITY_NAME_X_OPP) {
            currentAbilityNameX -= (dt * POPUP_SPEED);
            currentAbilityNameX = Math.max(FINAL_ABILITY_NAME_X_OPP, currentAbilityNameX);
        }
    }

    private void updatePopupUserName(float dt) {
        if (currentUserNameX < FINAL_USER_NAME_X) {
            currentUserNameX += (dt * POPUP_SPEED);
            currentUserNameX = Math.min(FINAL_USER_NAME_X, currentUserNameX);
        } else if (!isPlayerAbility && currentUserNameX > FINAL_OPPONENT_NAME_X) {
            currentUserNameX -= (dt * POPUP_SPEED);
            currentUserNameX = Math.max(FINAL_OPPONENT_NAME_X, currentUserNameX);
        }
    }

    private void updateRemoveAbilityMessageTexture(float dt) {
        if (isPlayerAbility && currentAbilityX > STARTING_ABILITY_X) {
            currentAbilityX -= (dt * POPUP_SPEED);
            currentAbilityX = Math.max(STARTING_ABILITY_X, currentAbilityX);
        } else if (!isPlayerAbility && currentAbilityX < STARTING_ABILITY_X_OPP) {
            currentAbilityX += (dt * POPUP_SPEED);
            currentAbilityX = Math.min(STARTING_ABILITY_X_OPP, currentAbilityX);
        }
    }

    private void updateRemoveAbilityName(float dt) {
        if (isPlayerAbility && currentAbilityNameX > STARTING_ABILITY_NAME_X) {
            currentAbilityNameX -= (dt * POPUP_SPEED);
            currentAbilityNameX = Math.max(STARTING_ABILITY_NAME_X, currentAbilityNameX);
        } else if (!isPlayerAbility && currentAbilityNameX < STARTING_ABILITY_NAME_X_OPP) {
            currentAbilityNameX += (dt * POPUP_SPEED);
            currentAbilityNameX = Math.min(STARTING_ABILITY_NAME_X_OPP, currentAbilityNameX);
        }
    }

    private void updateRemoveUserName(float dt) {
        if (isPlayerAbility && currentUserNameX > STARTING_USER_NAME_X) {
            currentUserNameX -= (dt * POPUP_SPEED);
            currentUserNameX = Math.max(STARTING_USER_NAME_X, currentUserNameX);
        } else if (!isPlayerAbility && currentUserNameX < STARTING_OPPONENT_NAME_X) {
            currentUserNameX += (dt * POPUP_SPEED);
            currentUserNameX = Math.min(STARTING_OPPONENT_NAME_X, currentUserNameX);
        }
    }

}
