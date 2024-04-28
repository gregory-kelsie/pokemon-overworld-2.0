package com.anime.arena.skill.effect;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.field.TerrainType;
import com.anime.arena.field.WeatherType;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;
import com.anime.arena.skill.MoveEffectSubtype;
import com.anime.arena.skill.SkillTarget;

import java.util.ArrayList;
import java.util.List;

public class SleepEffect extends Effect {
    public SleepEffect(SkillTarget target) {
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
        if (effectReceiver.isSleeping()) {
            addEffectMessage(effectMessages, "SleepEffect", "The sleep failed because\nthe enemy pokemon is already asleep");
        } else if (effectReceiver.getAbility() == AbilityId.VITAL_SPIRIT) {
            addEffectMessage(effectMessages, "SleepEffect", "The sleep failed because\nthe enemy pokemon has the VITAL SPIRIT ability");
        } else if (effectReceiver.getAbility() == AbilityId.COMATOSE) {
            addEffectMessage(effectMessages, "SleepEffect", "The sleep failed because\nthe enemy pokemon has the COMATOSE ability");
        } else if (effectReceiver.getAbility() == AbilityId.SWEET_VEIL) {
            addEffectMessage(effectMessages, "SleepEffect", "The sleep failed because\nthe enemy pokemon has the SWEET VEIL ability");
        } else if (field.getTerrainType() == TerrainType.ELECTRIC && !effectReceiver.isUngrounded()) {
            addEffectMessage(effectMessages, "SleepEffect", "The sleep failed because\nof the electric terrain");
        } else if (field.getTerrainType() == TerrainType.MISTY && !effectReceiver.isUngrounded()) {
            addEffectMessage(effectMessages, "SleepEffect", "The sleep failed because\nof the misty terrain");
        } else if (field.getWeatherType() == WeatherType.SUN && effectReceiver.getAbility() == AbilityId.LEAF_GUARD) {
            addEffectMessage(effectMessages, "SleepEffect", "The sleep failed because\nthe enemy pokemon has the LEAF GUARD ability");
        } else {
            induceSleep(effectReceiver, effectMessages);
        }
        return effectMessages;
    }

    private void induceSleep(BattlePokemon pokemon, List<String> effectMessages) {
        addEffectMessage(effectMessages, "SleepEffect", pokemon.getPokemon().getConstantVariables().getName() + " fell asleep!");
        pokemon.induceSleep();
    }
}
