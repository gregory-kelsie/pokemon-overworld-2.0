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

public class SunStep extends AbstractEndTurnStep {
    public SunStep(BattleStatePokemon firstAttacker, BattleStatePokemon secondAttacker, Field field) {
        super(firstAttacker, secondAttacker, field);
    }

    @Override
    protected void executeStep(BattleStatePokemon battleStatePokemon) {
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        AbilityId ability = pokemon.getAbility();
        AbilityId[] sunDamagingAbilities = {AbilityId.DRY_SKIN, AbilityId.SOLAR_POWER};
        AbilityId[] sunRecoveringAbilities = {};
        boolean hasSunlightDamagingAbility = Arrays.stream(sunDamagingAbilities).anyMatch(n -> n == ability);
        boolean hasSunlightRecoveringAbility = Arrays.stream(sunRecoveringAbilities).anyMatch(n -> n == ability);
        if (hasSunlightDamagingAbility) {
            damagePokemonFromSunlight(pokemon);
            currentState = StepState.UPDATING_HEALTH;
            if (ability == AbilityId.DRY_SKIN) {
                resultsText = pokemon.getName() + " took damage due to\nDry Skin in Sunlight";
            } else {
                resultsText = pokemon.getName() + " took damage due to\nSolar Power in Sunlight";
            }
        } else {
            battleLog("SUNLIGHT DOES NOTHING TO " + pokemon.getName());
            finishExecution();
        }
    }

    private void damagePokemonFromSunlight(BattlePokemon pokemon) {
        int damage = (int)Math.round(pokemon.getPokemon().getHealthStat() / 8.0);
        pokemon.subtractHealth(damage);
        if (pokemon.getAbility() == AbilityId.SOLAR_POWER) {
            battleLog(pokemon.getName() + " IS HURT FROM DRAWING IN SOLAR POWER");
        } else { //Having Dry skin is the only other way to get here.
            battleLog(pokemon.getName() + " IS HURT BY HAVING DRY SKIN IN THE SUNLIGHT");
        }
    }

    private void battleLog(String str) {
        Gdx.app.log("EndTurnState::SunStep", str);
    }
}
