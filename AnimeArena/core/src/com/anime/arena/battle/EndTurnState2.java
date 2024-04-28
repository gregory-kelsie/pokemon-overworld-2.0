package com.anime.arena.battle;

import com.anime.arena.battle.endturnlogic.*;
import com.anime.arena.battle.ui.BattleTextBox;
import com.anime.arena.battle.ui.BattleTextBoxComponent;
import com.anime.arena.battle.ui.FaintAnimation;
import com.anime.arena.battle.ui.HealthUpdater;
import com.anime.arena.field.Field;
import com.anime.arena.field.WeatherType;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EndTurnState2 extends BattleState {

    private Field field;
    private BattleStatePokemon firstAttacker;
    private BattleStatePokemon secondAttacker;
    private BattleStatePokemon faintingPokemon;
    private BattleStep stepAfterEnemyFaint;
    private AbstractEndTurnStep endTurnStep;
    private List<BattleStep> stepList;

    private String afterHealthUpdateText; //When you lose health, usually there is text that comes associated with it - Recovered due to dry skin, Recovered due to rain dish etc.

    public EndTurnState2(BattleStateManager battleStateManager) {
        this.battleStateManager = battleStateManager;
        this.field = battleStateManager.getField();
        this.firstAttacker = battleStateManager.getFirstAttacker();
        this.secondAttacker = battleStateManager.getSecondAttacker();
        this.afterHealthUpdateText = "";
        stepList = new ArrayList<BattleStep>();
        stepList.add(BattleStep.UPDATE_WEATHER);
        stepList.add(BattleStep.FIRST_RAIN);
        stepList.add(BattleStep.FIRST_SUN);
        stepList.add(BattleStep.FIRST_SAND);
        stepList.add(BattleStep.FIRST_HAIL);
        stepList.add(BattleStep.FIRST_BURN);
        stepList.add(BattleStep.FIRST_POISON);
        stepList.add(BattleStep.CLAMP);
        setFirstBattleStep();
        //this.currentStep = BattleStep.UPDATE_WEATHER;
    }


    private void setFirstBattleStep() {
        endTurnStep = getNextStep(null);
    }

    private AbstractEndTurnStep getNextStep(BattleStep finishedStep) {
        //UPDATE_WEATHER -> WEATHER_EFFECTS -> (FIRST_POISON -> FIRST_HURT_POISON) -> SECOND_POISON -> FIRST_BURN -> SECOND_BURN -> DONE_END_TURN
        int nextStepNumber = finishedStep != null ? stepList.indexOf(finishedStep) + 1 : 0;
        for (int i = nextStepNumber; i < stepList.size(); i++) {
            if (isBattleStepConditionSatisfied(stepList.get(i))) {
                return getCurrentStep(stepList.get(i));
            }
        }
        return null;
    }

    private AbstractEndTurnStep getCurrentStep(BattleStep step) {
        if (step == BattleStep.UPDATE_WEATHER) {
            currentStep = BattleStep.UPDATE_WEATHER;
            return new WeatherStep(firstAttacker, secondAttacker, field);
        } else if (step == BattleStep.FIRST_RAIN) {
            currentStep = BattleStep.FIRST_RAIN;
            return new RainStep(firstAttacker, secondAttacker, field);
        } else if (step == BattleStep.FIRST_SUN) {
            currentStep = BattleStep.FIRST_SUN;
            return new SunStep(firstAttacker, secondAttacker, field);
        } else if (step == BattleStep.FIRST_SAND) {
            currentStep = BattleStep.FIRST_SAND;
            return new SandstormStep(firstAttacker, secondAttacker, field);
        } else if (step == BattleStep.FIRST_HAIL) {
            currentStep = BattleStep.FIRST_HAIL;
            return new HailStep(firstAttacker, secondAttacker, field);
        } else if (step == BattleStep.FIRST_BURN) {
            currentStep = BattleStep.FIRST_BURN;
            return new BurnStep(firstAttacker, secondAttacker, field);
        } else if (step == BattleStep.FIRST_POISON) {
            currentStep = BattleStep.FIRST_POISON;
            return new PoisonStep(firstAttacker, secondAttacker, field);
        } else if (step == BattleStep.CLAMP) {
            currentStep = BattleStep.CLAMP;
            return new ClampStep(firstAttacker, secondAttacker, field);
        }
        return null;
    }

    private boolean isBattleStepConditionSatisfied(BattleStep step) {
        if (step == BattleStep.UPDATE_WEATHER) {
            return field.getWeatherType() != WeatherType.NORMAL;
        } else if (step == BattleStep.FIRST_RAIN) {
            return field.getWeatherType() == WeatherType.RAIN;
        } else if (step == BattleStep.FIRST_SUN) {
            return field.getWeatherType() == WeatherType.SUN;
        } else if (step == BattleStep.FIRST_SAND) {
            return field.getWeatherType() == WeatherType.SAND;
        } else if (step == BattleStep.FIRST_HAIL) {
            return field.getWeatherType() == WeatherType.HAIL;
        } else if (step == BattleStep.FIRST_BURN) {
            return firstAttacker.getPokemon().isBurned() || secondAttacker.getPokemon().isBurned();
        } else if (step == BattleStep.FIRST_POISON) {
            return firstAttacker.getPokemon().isPoisoned() || secondAttacker.getPokemon().isPoisoned();
        } else if (step == BattleStep.CLAMP) {
            return firstAttacker.getPokemon().isClamped() || secondAttacker.getPokemon().isClamped();
        }  else if (step == BattleStep.DONE_END_TURN) {
            return true;
        }
        return false;
    }

    public void update(float dt) {
        if (uiComponent != null) {
            uiComponent.update(dt);
        }
        if (finishedUpdatingUI()) {
            if (endTurnStep != null) {
                endTurnStep.updateStep(dt);
                if (endTurnStep.isCompleted()) {
                    setNextEndTurnStep(); //TODO: If the next step is not null it should be executed in this loop instead of waiting.
                } else if (!endTurnStep.isExecuting()) {
                    createUIComponent();
                }
            } else {
                finishEndTurnState();
            }
        } else if (endTurnStep == null) {
            finishEndTurnState();
        }
    }

    private void setNextEndTurnStep() {
        endTurnStep = getNextStep(currentStep);
        if (endTurnStep == null) {
            finishEndTurnState();
        }
    }

    private void createUIComponent() {
        if (endTurnStep.isWaitingToUpdateHealth()) {
            uiComponent = new HealthUpdater(this, endTurnStep.getCurrentBattlePokemon());
        } else if (endTurnStep.isWaitingToDisplayResults()) {
            uiComponent = new BattleTextBoxComponent(this, createMoveMessages(endTurnStep.getResultsText()));
        }
    }

    private void finishEndTurnState() {
        battleLog("DONE END TURN STATE");
        if (battleStateManager.isUserPokemonFainted()
                && battleStateManager.isWildBattle() && !battleStateManager.isPlayerPartyWiped()) {
            currentStep = BattleStep.GET_NEXT_POKEMON;
        } else {
            battleStateManager.setState(null);
            battleStateManager.setReturningToFightOptions(true);
        }
    }



    private void battleLog(String str) {
        Gdx.app.log("EndTurnState", str);
    }



}
