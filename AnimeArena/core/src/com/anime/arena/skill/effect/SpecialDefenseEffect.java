package com.anime.arena.skill.effect;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.skill.SkillTarget;
import com.anime.arena.skill.StatDirection;

import java.util.ArrayList;
import java.util.List;

public class SpecialDefenseEffect extends StatEffect {
    public SpecialDefenseEffect(SkillTarget target, StatDirection direction, int amount) {
        super(target, amount, direction);
    }

    @Override
    public List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        List<String> effectMessages = new ArrayList<String>();
        BattlePokemon effectReceiver = getReceiver(target, skillUser, enemyPokemon);
        SubField receiverField = getReceiverField(target, userField, enemyField);
        finalAmount = amount;
        if (statDirection == StatDirection.DECREASE) {
            //Attempt to lower the attack stage.
            if (receiverField.hasMist()) {
                addEffectMessage(effectMessages,"Mist prevents " + effectReceiver.getName() + " from losing Special Defense!");
            } else if (effectReceiver.getSpecialDefenseStage() == -6) {
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s special defense can't be lowered.");
            } else {
                effectReceiver.decreaseSpDefenseStage(finalAmount);
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s special defense" + getFallText());
            }
        } else {
            //Attempt to increase the special defense stage.
            if (effectReceiver.getSpecialDefenseStage() == 6) {
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s special defense can't get higher.");
            } else {
                effectReceiver.increaseSpDefenseStage(finalAmount);
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s special defense" + getRoseText());
            }
        }
        return effectMessages;
    }
}