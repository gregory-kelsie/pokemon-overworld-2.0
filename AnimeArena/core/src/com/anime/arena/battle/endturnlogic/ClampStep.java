package com.anime.arena.battle.endturnlogic;

import com.anime.arena.battle.BattleStatePokemon;
import com.anime.arena.field.Field;
import com.anime.arena.pokemon.BattlePokemon;
import com.badlogic.gdx.Gdx;

public class ClampStep extends AbstractEndTurnStep {
    public ClampStep(BattleStatePokemon firstAttacker, BattleStatePokemon secondAttacker, Field field) {
        super(firstAttacker, secondAttacker, field);
    }

    @Override
    protected void executeStep(BattleStatePokemon battleStatePokemon) {
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        if (pokemon.isClamped()) {
            if (pokemon.getClampTurns() == 0) {
                pokemon.removeClamp();
                resultsText = pokemon.getName() + " was freed from Clamp.";
                currentState = StepState.DISPLAYING_RESULTS;
            } else {
                resultsText = pokemon.getName() + " was hurt by Clamp.";
                useClamp(battleStatePokemon);
                currentState = StepState.UPDATING_HEALTH;
            }
            battleLog(pokemon.getName() + " HAS " + pokemon.getClampTurns() + " LEFT.");
        } else {
            battleLog(pokemon.getName() + " IS NOT CLAMPED");
            finishExecution();
        }
    }

    private void useClamp(BattleStatePokemon battleStatePokemon) {
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        int damage = (int)Math.round(pokemon.getPokemon().getHealthStat() / 16.0);
        pokemon.subtractHealth(damage);
        pokemon.adjustClampTurns();
    }

    private void battleLog(String str) {
        Gdx.app.log("EndTurnState::ClampStep", str);
    }
}
