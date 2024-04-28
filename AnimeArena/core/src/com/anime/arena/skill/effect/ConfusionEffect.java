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

public class ConfusionEffect extends Effect {
    public ConfusionEffect(SkillTarget target) {
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
        //Check if the user is able to receive confusion.
        if (effectReceiver.isConfusable() && !targetSubField.hasSafeguard()) {
            effectReceiver.induceConfusion();
            effectMessages.add(effectReceiver.getName() + " was confused!");
        }
        return effectMessages;
    }

    public boolean willFailAsSingleEffect(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        BattlePokemon effectReceiver;
        SubField targetSubField;
        if (target == SkillTarget.SELF) {
            effectReceiver = skillUser;
            targetSubField = userField;
        } else {
            effectReceiver = enemyPokemon;
            targetSubField = enemyField;
        }
        if (effectReceiver.isConfused() || effectReceiver.getAbility() == AbilityId.OWN_TEMPO) {
            return true;
        }
        return false;
    }
}
