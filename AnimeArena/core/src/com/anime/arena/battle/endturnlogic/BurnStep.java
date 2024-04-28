package com.anime.arena.battle.endturnlogic;

import com.anime.arena.battle.BattleStatePokemon;
import com.anime.arena.field.Field;
import com.anime.arena.pokemon.BattlePokemon;
import com.badlogic.gdx.Gdx;

public class BurnStep extends AbstractEndTurnStep {
    public BurnStep(BattleStatePokemon firstAttacker, BattleStatePokemon secondAttacker, Field field) {
        super(firstAttacker, secondAttacker, field);
    }

    @Override
    protected void executeStep(BattleStatePokemon battleStatePokemon) {
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        if (pokemon.isBurned()) {
            useBurn(battleStatePokemon);
            currentState = StepState.UPDATING_HEALTH;
            resultsText = pokemon.getName() + " was hurt by burn.";
        } else {
            battleLog(pokemon.getName() + " IS NOT BURNED");
            finishExecution();
        }
    }

    private void useBurn(BattleStatePokemon battleStatePokemon) {
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        int damage = (int)Math.round(pokemon.getPokemon().getHealthStat() / 16.0);
        pokemon.subtractHealth(damage);
        battleLog(pokemon.getName() + " WAS HURT BY BURN - TOOK " + damage + " DAMAGE");
    }

    private void battleLog(String str) {
        Gdx.app.log("EndTurnState::BurnStep", str);
    }
}
