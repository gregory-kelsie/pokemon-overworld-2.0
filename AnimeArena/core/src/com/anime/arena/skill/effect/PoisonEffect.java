package com.anime.arena.skill.effect;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;
import com.anime.arena.skill.MoveEffectSubtype;
import com.anime.arena.skill.SkillTarget;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class PoisonEffect extends  Effect {
    public PoisonEffect(SkillTarget target) {
        super(MoveEffectSubtype.STATUS, target);
    }

    @Override
    public List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        List<String> effectMessages = new ArrayList<String>();
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
            addEffectMessage(effectMessages, "PoisonEffect", "The poison failed because the pokemon has a status condition.");
        } else if (targetSubField.hasSafeguard()) {
            addEffectMessage(effectMessages, "PoisonEffect", "The poison failed because of the safeguard");
        } else if (effectReceiver.getAbility() == AbilityId.SHIELD_DUST) {
            addEffectMessage(effectMessages, "PoisonEffect", "The poison failed because of the SHIELD DUST ability");
        } else {
            if (effectReceiver.getAbility() != AbilityId.CORROSION) {
                if (effectReceiver.getFirstType() == PokemonType.POISON || effectReceiver.getSecondType() == PokemonType.POISON) {
                    addEffectMessage(effectMessages, "PoisonEffect", "The poison failed because the enemy pokemon is a POISON type");
                } else if (effectReceiver.getFirstType() == PokemonType.STEEL || effectReceiver.getSecondType() == PokemonType.STEEL) {
                    addEffectMessage(effectMessages, "PoisonEffect", "The poison effect failed because the enemy pokemon is a STEEL type");
                } else {
                    inflictPoison(effectReceiver, effectMessages);
                }
            } else {
                inflictPoison(effectReceiver, effectMessages);
            }
        }
        return effectMessages;
    }

    private void inflictPoison(BattlePokemon pokemon, List<String> effectMessages) {
        addEffectMessage(effectMessages, "PoisonEffect", pokemon.getPokemon().getConstantVariables().getName() + " was poisoned!");
        pokemon.inflictPoison();
    }

}