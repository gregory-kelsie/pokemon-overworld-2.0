package com.anime.arena.skill;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;
import com.badlogic.gdx.Gdx;

import java.util.List;

public class PoisonEffect extends  Effect {
    public PoisonEffect(int id, MoveEffectSubtype subtype, SkillTarget target) {
        super(id, subtype, target);
    }

    @Override
    public void use(List<String> results, BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        BattlePokemon effectReceiver;
        SubField targetSubField;
        if (target == SkillTarget.SELF) {
            effectReceiver = skillUser;
            targetSubField = userField;
        } else {
            effectReceiver = enemyPokemon;
            targetSubField = enemyField;
        }
        //Check if the user is able to receive poison.
        if (effectReceiver.isStatused()) {
            Gdx.app.log("StatusEffect", "The poison failed because the pokemon has a status condition.");
        } else if (targetSubField.hasSafeguard()) {
            Gdx.app.log("StatusEffect", "The poison failed because of the safeguard");
        } else if (effectReceiver.getAbility() == AbilityId.SHIELD_DUST) {
            Gdx.app.log("StatusEffect", "The poison failed because of the SHIELD DUST ability");
        } else {
            if (effectReceiver.getAbility() != AbilityId.CORROSION) {
                if (effectReceiver.getFirstType() == PokemonType.POISON || effectReceiver.getSecondType() == PokemonType.POISON) {
                    Gdx.app.log("StatusEffect", "The poison failed because the enemy pokemon is a POISON type");
                } else if (effectReceiver.getFirstType() == PokemonType.STEEL || effectReceiver.getSecondType() == PokemonType.STEEL) {
                    Gdx.app.log("StatusEffect", "The poison effect failed because the enemy pokemon is a STEEL type");
                } else {
                    inflictPoison(effectReceiver);
                }
            } else {
                inflictPoison(effectReceiver);
            }
        }
    }

    private void inflictPoison(BattlePokemon pokemon) {
        Gdx.app.log("StatusEffect", pokemon.getPokemon().getConstantVariables().getName() + " was poisoned!");
        pokemon.inflictPoison();
    }

}