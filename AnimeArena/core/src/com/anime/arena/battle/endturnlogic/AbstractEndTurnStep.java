package com.anime.arena.battle.endturnlogic;

import com.anime.arena.battle.BattleStatePokemon;
import com.anime.arena.battle.ui.UIComponent;
import com.anime.arena.field.Field;
import com.anime.arena.pokemon.BattlePokemon;

public abstract class AbstractEndTurnStep {

    protected StepState currentState;
    protected boolean isFirstPokemonExecution;
    protected boolean hasImpactOnPokemon;
    protected BattleStatePokemon firstAttacker;
    protected BattleStatePokemon secondAttacker;
    protected Field field;
    protected String resultsText;

    public AbstractEndTurnStep(BattleStatePokemon firstAttacker, BattleStatePokemon secondAttacker, Field field) {
        this.currentState = StepState.EXECUTING;
        this.isFirstPokemonExecution = true;
        this.resultsText = "";
        this.field = field;
        this.hasImpactOnPokemon = true;
        this.firstAttacker = firstAttacker;
        this.secondAttacker = secondAttacker;
    }

    public void updateStep(float dt) {
        if (currentState == StepState.EXECUTING) {
            execute();
        } else if (currentState == StepState.UPDATING_HEALTH) {
            if (!resultsText.equals("")) {
                currentState = StepState.DISPLAYING_RESULTS;
            } else {
                finishExecution();
            }
        } else if (currentState == StepState.DISPLAYING_RESULTS) {
            finishExecution();
        } else if (currentState == StepState.COMPLETED) {

        }
    }

    private void execute() {
        executeStep(isFirstPokemonExecution ? firstAttacker : secondAttacker);
        if (isExecuting() && isFirstPokemonExecution && hasImpactOnPokemon) {
            isFirstPokemonExecution = false;
            executeStep(secondAttacker);
        }
    }

    public boolean isExecuting() {
        return currentState == StepState.EXECUTING;
    }

    public boolean  isCompleted() {
        return currentState == StepState.COMPLETED;
    }

    public boolean isWaitingToUpdateHealth() {
        return currentState == StepState.UPDATING_HEALTH;
    }

    public boolean isWaitingToDisplayResults() {
        return currentState == StepState.DISPLAYING_RESULTS;
    }

    protected void finishExecution() {
        if (hasImpactOnPokemon && isFirstPokemonExecution) {
            currentState = StepState.EXECUTING;
            isFirstPokemonExecution = false;
        } else {
            currentState = StepState.COMPLETED;
        }
    }

    public BattlePokemon getCurrentBattlePokemon() {
        return isFirstPokemonExecution ? firstAttacker.getPokemon() : secondAttacker.getPokemon();
    }

    public String getResultsText() {
        return resultsText;
    }
    protected abstract void executeStep(BattleStatePokemon battleStatePokemon);
}
