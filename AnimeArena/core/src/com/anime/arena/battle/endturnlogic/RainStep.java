package com.anime.arena.battle.endturnlogic;

import com.anime.arena.battle.BattleStatePokemon;
import com.anime.arena.field.Field;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.badlogic.gdx.Gdx;

import java.util.Arrays;

public class RainStep extends AbstractEndTurnStep {
    public RainStep(BattleStatePokemon firstAttacker, BattleStatePokemon secondAttacker, Field field) {
        super(firstAttacker, secondAttacker, field);
    }

    @Override
    protected void executeStep(BattleStatePokemon battleStatePokemon) {
        AbilityId ability = battleStatePokemon.getPokemon().getAbility();
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        AbilityId[] smallRainRecoveringAbilities = {AbilityId.RAIN_DISH};
        AbilityId[] largeRainRecoveringAbilities = {AbilityId.DRY_SKIN};
        boolean hasSmallRainRecoveringAbility = Arrays.stream(smallRainRecoveringAbilities).anyMatch(n -> n == ability);
        boolean hasLargeRainRecoveringAbility = Arrays.stream(largeRainRecoveringAbilities).anyMatch(n -> n == ability);
        if (hasSmallRainRecoveringAbility
                && !pokemon.hasFullHealth()) {
            recoverPokemonFromRain(pokemon, 16.0);
            currentState = StepState.UPDATING_HEALTH;
            resultsText = pokemon.getName() + " recovered health from Rain Dish.";
        } else if (hasLargeRainRecoveringAbility
                && !pokemon.hasFullHealth()) {
            recoverPokemonFromRain(pokemon, 8.0);
            currentState = StepState.UPDATING_HEALTH;
            resultsText = pokemon.getName() + " recovered health from Dry Skin.";
        } else {
            battleLog("RAIN DOES NOTHING TO " + pokemon.getName());
            if (isFirstPokemonExecution) {
                currentState = StepState.EXECUTING;
                isFirstPokemonExecution = false;
            } else {
                currentState = StepState.COMPLETED;
            }
        }
    }

    private void recoverPokemonFromRain(BattlePokemon pokemon, double denominator) {
        int recoverAmount = (int)Math.round(pokemon.getPokemon().getHealthStat() / denominator);
        pokemon.addHealth(recoverAmount);
        battleLog(pokemon.getName() + " RECOVERED HEALTH IN THE RAIN WITH THE ABILITY: " + pokemon.getAbility().toString());
    }

    private void battleLog(String str) {
        Gdx.app.log("EndTurnState::RainStep", str);
    }
}
