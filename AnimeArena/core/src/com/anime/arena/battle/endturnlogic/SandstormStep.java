package com.anime.arena.battle.endturnlogic;

import com.anime.arena.battle.BattleStatePokemon;
import com.anime.arena.field.Field;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;
import com.badlogic.gdx.Gdx;

import java.util.Arrays;

public class SandstormStep extends AbstractEndTurnStep {
    public SandstormStep(BattleStatePokemon firstAttacker, BattleStatePokemon secondAttacker, Field field) {
        super(firstAttacker, secondAttacker, field);
    }

    @Override
    protected void executeStep(BattleStatePokemon battleStatePokemon) {
        AbilityId ability = battleStatePokemon.getPokemon().getAbility();
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        PokemonType[] sandstormImmuneTypes = {PokemonType.ROCK, PokemonType.GROUND, PokemonType.STEEL};
        AbilityId[] sandstormImmuneAbilities = {AbilityId.SAND_FORCE, AbilityId.SAND_RUSH, AbilityId.SAND_VEIL, AbilityId.MAGIC_GUARD, AbilityId.OVERCOAT};
        boolean hasSandstormImmuneType = Arrays.stream(sandstormImmuneTypes).anyMatch(n -> n == pokemon.getFirstType() || n == pokemon.getSecondType());
        boolean hasSandstormImmuneAbility = Arrays.stream(sandstormImmuneAbilities).anyMatch(n -> n == ability);
        if (hasSandstormImmuneAbility || hasSandstormImmuneType) { //TODO: Check for holding safety goggles
            battleLog("SANDSTORM DOES NOTHING TO " + pokemon.getName() + " DUE TO THEIR ABILITY OR TYPE");
            finishExecution();
        } else {
            damagePokemonFromSandstorm(pokemon);
            currentState = StepState.UPDATING_HEALTH;
            resultsText = pokemon.getName() + " is buffeted from the sandstorm!";
        }

    }

    private void damagePokemonFromSandstorm(BattlePokemon pokemon) {
        int damage = (int)Math.round(pokemon.getPokemon().getHealthStat() / 16.0);
        pokemon.subtractHealth(damage);
        battleLog(pokemon.getName() + " IS BUFFETED BY THE SANDSTORM!");
    }


    private void battleLog(String str) {
        Gdx.app.log("EndTurnState::SandstormStep", str);
    }
}
