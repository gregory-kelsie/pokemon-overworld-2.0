package com.anime.arena.battle.endturnlogic;

import com.anime.arena.battle.BattleStatePokemon;
import com.anime.arena.field.Field;
import com.anime.arena.pokemon.BattlePokemon;
import com.badlogic.gdx.Gdx;

public class PoisonStep extends AbstractEndTurnStep {
    public PoisonStep(BattleStatePokemon firstAttacker, BattleStatePokemon secondAttacker, Field field) {
        super(firstAttacker, secondAttacker, field);
    }

    @Override
    protected void executeStep(BattleStatePokemon battleStatePokemon) {
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        if (pokemon.isPoisoned()) {
            usePoison(battleStatePokemon);
            currentState = StepState.UPDATING_HEALTH;
            resultsText = pokemon.getName() + " was hurt by poison.";
        } else {
            battleLog(pokemon.getName() + " IS NOT POISONED");
            finishExecution();
        }
    }

    private void usePoison(BattleStatePokemon battleStatePokemon) {
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        int damage = (int)Math.round(pokemon.getPokemon().getHealthStat() / 8.0);
        pokemon.subtractHealth(damage);
        battleLog(pokemon.getName() + " WAS HURT BY POISON");
    }

    private void battleLog(String str) {
        Gdx.app.log("EndTurnState::PoisonStep", str);
    }
}
