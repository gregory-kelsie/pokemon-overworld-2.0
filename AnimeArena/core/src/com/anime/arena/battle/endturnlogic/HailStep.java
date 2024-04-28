package com.anime.arena.battle.endturnlogic;

import com.anime.arena.battle.BattleStatePokemon;
import com.anime.arena.battle.BattleStep;
import com.anime.arena.battle.ui.HealthUpdater;
import com.anime.arena.field.Field;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;
import com.badlogic.gdx.Gdx;

import java.util.Arrays;

public class HailStep extends AbstractEndTurnStep {
    public HailStep(BattleStatePokemon firstAttacker, BattleStatePokemon secondAttacker, Field field) {
        super(firstAttacker, secondAttacker, field);
    }

    @Override
    protected void executeStep(BattleStatePokemon battleStatePokemon) {
        AbilityId ability = battleStatePokemon.getPokemon().getAbility();
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        AbilityId[] hailImmuneAbilities = {AbilityId.ICE_BODY, AbilityId.SNOW_CLOAK, AbilityId.MAGIC_GUARD, AbilityId.OVERCOAT};
        AbilityId[] hailRecoveringAbilities = {AbilityId.ICE_BODY};
        boolean hasHailRecoveringAbility = Arrays.stream(hailRecoveringAbilities).anyMatch(n -> n == ability);
        boolean hasHailImmuneAbility = Arrays.stream(hailImmuneAbilities).anyMatch(n -> n == ability);
        if (hasHailImmuneAbility ||
                pokemon.getFirstType() == PokemonType.ICE ||
                pokemon.getSecondType() == PokemonType.ICE) { //TODO: Check for holding safety goggles
            if (hasHailRecoveringAbility && !pokemon.hasFullHealth()) {
                recoverPokemonFromHail(pokemon);
                currentState = StepState.UPDATING_HEALTH;
                resultsText = pokemon.getName() + " recovered health from Ice Body.";
            } else {
                battleLog("HAIL DOES NOTHING TO " + pokemon.getName() + " DUE TO THEIR ABILITY OR TYPE");
                finishExecution();
            }
            } else {
                damagePokemonFromHail(pokemon);
                currentState = StepState.UPDATING_HEALTH;
            resultsText = pokemon.getName() + " is buffeted by the hail.";
        }
    }

    private void recoverPokemonFromHail(BattlePokemon pokemon) {
        int recoverAmount = (int)Math.round(pokemon.getPokemon().getHealthStat() / 16.0);
        pokemon.addHealth(recoverAmount);
        battleLog(pokemon.getName() + " RECOVERED HEALTH FROM HAIL WITH THE ABILITY: ICE BODY");
    }

    private void damagePokemonFromHail(BattlePokemon pokemon) {
        int damage = (int)Math.round(pokemon.getPokemon().getHealthStat() / 16.0);
        pokemon.subtractHealth(damage);
        battleLog(pokemon.getName() + " IS BUFFETED BY HAIL");
    }


    private void battleLog(String str) {
        Gdx.app.log("EndTurnState::HailStep", str);
    }
}
