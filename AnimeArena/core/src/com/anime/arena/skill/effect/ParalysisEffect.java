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

public class ParalysisEffect extends  Effect {
    public ParalysisEffect(SkillTarget target) {
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
        if (effectReceiver.isStatused()) {
            addEffectMessage(effectMessages, "ParalysisEffect", "The paralysis failed because\nthe pokemon has a status condition.");
        } else if (targetSubField.hasSafeguard()) {
            addEffectMessage(effectMessages, "ParalysisEffect", "The paralysis failed because\nof the safeguard");
        } else if (effectReceiver.getAbility() == AbilityId.SHIELD_DUST) {
            addEffectMessage(effectMessages, "ParalysisEffect", "The paralysis failed because\nof the SHIELD DUST ability");
        } else if (effectReceiver.getFirstType() == PokemonType.ELECTRIC || effectReceiver.getSecondType() == PokemonType.ELECTRIC) {
            addEffectMessage(effectMessages, "ParalysisEffect", "The paralyze failed because\nthe enemy pokemon is an ELECTRIC type");
        } else if (effectReceiver.getFirstType() == PokemonType.GROUND || effectReceiver.getSecondType() == PokemonType.GROUND && moveType == PokemonType.ELECTRIC) {
            addEffectMessage(effectMessages, "ParalysisEffect", "The paralyze failed because\nthe enemy pokemon is a GROUND type and the move is an ELECTRIC type");
        } else if (effectReceiver.getAbility() == AbilityId.LIMBER) {
            addEffectMessage(effectMessages, "ParalysisEffect", "The paralyze failed because\nthe enemy pokemon has the LIMBER ability");
        } else {
            inflictParalysis(effectReceiver, effectMessages);
        }
        return effectMessages;
    }

    private void inflictParalysis(BattlePokemon pokemon, List<String> effectMessages) {
        addEffectMessage(effectMessages, "ParalysisEffect", pokemon.getPokemon().getConstantVariables().getName() + " was paralyzed!");
        pokemon.inflictParalysis();
    }
}
