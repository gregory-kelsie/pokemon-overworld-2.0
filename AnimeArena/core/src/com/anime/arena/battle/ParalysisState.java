package com.anime.arena.battle;

import com.anime.arena.battle.ui.BattleTextBoxComponent;
import com.badlogic.gdx.Gdx;

public class ParalysisState extends BattleState {
    private boolean isFirstMove;
    private static final float PARALYSIS_RATE = 0.3f;
    private BattleStatePokemon paralyzedPokemon;

    public ParalysisState(BattleStateManager battleStateManager, boolean isFirstMove) {
        this.isFirstMove = isFirstMove;
        this.battleStateManager = battleStateManager;
        this.paralyzedPokemon = isFirstMove ? battleStateManager.getFirstAttacker() : battleStateManager.getSecondAttacker();
        battleLog("PARALYSIS CHECK FOR: " + paralyzedPokemon.getPokemon().getName());
        currentStep = BattleStep.PARALYSIS_CHECK;
    }

    private void battleLog(String str) {
        Gdx.app.log("ParalysisState", str);
    }

    public void update(float dt) {
        if (uiComponent != null) {
            uiComponent.update(dt);
        }
        if (finishedUpdatingUI()) {
            if (currentStep == BattleStep.PARALYSIS_CHECK) {
                boolean isParalyzed = paralyzedPokemon.getPokemon().isParalyzed();
                battleLog("IS PARALYZED?: " + isParalyzed);
                if (isParalyzed) {
                    boolean isFullyParalyzed = getParalysisResult();
                    if (isFullyParalyzed) {
                        currentStep = BattleStep.FAILED_PARALYSIS_CHECK;
                        uiComponent = new BattleTextBoxComponent(this, createMoveMessages(paralyzedPokemon.getPokemon().getName() + " is fully paralyzed."));
                    } else {
                        passedParalysisCheck();
                    }
                } else {
                    passedParalysisCheck();
                }
            } else if (currentStep == BattleStep.FAILED_PARALYSIS_CHECK) {
                if (isFirstMove) {
                    battleLog("FAILED PARALYSIS CHECK - GOING TO SLEEP CHECK FOR SECOND ATTACKER");
                    battleStateManager.setState(new SleepState(battleStateManager, false));
                } else {
                    battleLog("FAILED PARALYSIS CHECK - GOING TO END TURN STATE");
                    battleStateManager.setState(new EndTurnState2(battleStateManager));
                }
            }
        }
    }

    private void passedParalysisCheck() {
        battleLog("PASSED PARALYSIS CHECK - GOING TO MOVE FAIL CHECK");
        battleStateManager.setState(new MoveFailState(battleStateManager, isFirstMove));
    }

    /**
     * Check if the paralyzed pokemon is fully paralyzed.
     */
    private boolean getParalysisResult() {
        double rand = Math.random();
        battleLog("PARALYSIS ROLL: " + rand + ", PARALYSIS RATE: " + PARALYSIS_RATE);
        if (rand <= PARALYSIS_RATE) {
            return true;
        } else {
            return false;
        }
    }
}
