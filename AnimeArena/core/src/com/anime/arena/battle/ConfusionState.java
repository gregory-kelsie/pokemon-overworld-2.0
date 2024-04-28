package com.anime.arena.battle;

import com.anime.arena.battle.ui.BattleTextBoxComponent;
import com.anime.arena.battle.ui.ConfusedAnimation;
import com.anime.arena.battle.ui.HealthUpdater;
import com.anime.arena.skill.HurtByConfusion;
import com.badlogic.gdx.Gdx;

public class ConfusionState extends BattleState {
    private boolean isFirstMove;
    private static final float CONFUSION_RATE = 0.33f;
    private BattleStatePokemon confusedPokemon;
    private BattleStatePokemon otherPokemon;

    public ConfusionState(BattleStateManager battleStateManager, boolean isFirstMove) {
        this.isFirstMove = isFirstMove;
        this.battleStateManager = battleStateManager;
        this.confusedPokemon = isFirstMove ? battleStateManager.getFirstAttacker() : battleStateManager.getSecondAttacker();
        this.otherPokemon = isFirstMove ? battleStateManager.getSecondAttacker() : battleStateManager.getFirstAttacker();
        this.currentStep = BattleStep.CONFUSION_CHECK;
        battleLog("CONFUSION CHECK FOR: " + confusedPokemon.getPokemon().getName());
    }

    private void battleLog(String str) {
        Gdx.app.log("ConfusionState", str);
    }

    public void update(float dt) {
        if (uiComponent != null) {
            uiComponent.update(dt);
        }
        if (finishedUpdatingUI()) {
            if (currentStep == BattleStep.CONFUSION_CHECK) {
                boolean isConfused = confusedPokemon.getPokemon().isConfused();
                int confusionTime = confusedPokemon.getPokemon().getConfusionTime();
                battleLog("IS CONFUSED?: " + isConfused);
                battleLog("CONFUSION TIME: " + confusionTime);
                if (isConfused && confusionTime != 0) {
                    //IS CONFUSED
                    currentStep = BattleStep.IS_CONFUSED;
                    uiComponent = new BattleTextBoxComponent(this, createMoveMessages(confusedPokemon.getPokemon().getName() + " is confused."));
                    reduceConfusionTime();
                } else if (isConfused && confusionTime == 0) {
                    currentStep = BattleStep.SNAPPED_OUT_OF_IT;
                    uiComponent = new BattleTextBoxComponent(this, createMoveMessages(confusedPokemon.getPokemon().getName() + " snapped out of it!"));
                    confusedPokemon.getPokemon().removeConfusion();
                    battleLog(confusedPokemon.getPokemon().getName() + " SNAPPED OUT OF IT");
                } else {
                    passedConfusionCheck();
                }
            } else if (currentStep == BattleStep.IS_CONFUSED) {
                currentStep = BattleStep.CONFUSION_ANIMATION;
                uiComponent = new ConfusedAnimation(this);
            } else if (currentStep == BattleStep.CONFUSION_ANIMATION) {
                boolean isHurtingSelfInConfusion = getConfusionResult();
                battleLog("IS HURTING SELF?: " + isHurtingSelfInConfusion);
                if (isHurtingSelfInConfusion) {
                    this.currentStep = BattleStep.FAIL_CONFUSION_ROLL;
                    HurtByConfusion hurtByConfusion = new HurtByConfusion();
                    hurtByConfusion.use(confusedPokemon.getPokemon(), confusedPokemon.getPokemon(), battleStateManager.getField());
                    battleLog("NEW HEALTH AFTER HURTING SELF: " + confusedPokemon.getPokemon().getCurrentHealth());
                    uiComponent = new HealthUpdater(this, confusedPokemon.getPokemon());

                } else {
                    passedConfusionCheck();
                }
            } else if (currentStep == BattleStep.FAIL_CONFUSION_ROLL) {
                currentStep = BattleStep.HURT_SELF_IN_CONFUSION;
                uiComponent = new BattleTextBoxComponent(this, createMoveMessages(confusedPokemon.getPokemon().getName() + " hurt itself in confusion!"));
            } else if (currentStep == BattleStep.SNAPPED_OUT_OF_IT) {
                passedConfusionCheck();
            } else if (currentStep == BattleStep.HURT_SELF_IN_CONFUSION) {
                failConfusionCheck();
            }
        }
    }

    private void failConfusionCheck() {
        if (confusedPokemon.getPokemon().getPokemon().isFainted()) {
            if (!confusedPokemon.isUser()) {

            }
        } else {
            if (isFirstMove) {
                battleLog("FAILED CONFUSION CHECK - GOING TO SECOND ATTACKER'S SLEEP CHECK");
                battleStateManager.setState(new SleepState(battleStateManager, false));
            } else {
                battleLog("FAILED CONFUSION CHECK - GOING TO END TURN STATE");
                battleStateManager.setState(new EndTurnState2(battleStateManager));
            }
        }
    }

    private void passedConfusionCheck() {
        battleLog("PASSED CONFUSION CHECK - GOING TO PARALYSIS CHECK");
        battleStateManager.setState(new ParalysisState(battleStateManager, isFirstMove));
    }

    private void reduceConfusionTime() {
        confusedPokemon.getPokemon().reduceConfusionTime();
        battleLog("REDUCED CONFUSION TIME TO: " + confusedPokemon.getPokemon().getConfusionTime());
    }

    /**
     * Check if the confused pokemon will hit himself of not.
     */
    private boolean getConfusionResult() {
        double rand = Math.random();
        if (rand <= CONFUSION_RATE) {
            return true;
        } else {
            return false;
        }
    }

}
