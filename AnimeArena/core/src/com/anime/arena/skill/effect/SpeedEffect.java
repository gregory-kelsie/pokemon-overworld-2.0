package com.anime.arena.skill.effect;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.skill.SkillTarget;
import com.anime.arena.skill.StatDirection;

import java.util.ArrayList;
import java.util.List;

public class SpeedEffect extends StatEffect {
    public SpeedEffect(SkillTarget target, StatDirection direction, int amount) {
        super(target, amount, direction);
    }

    @Override
    public List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstspeed) {
        List<String> effectMessages = new ArrayList<String>();
        BattlePokemon effectReceiver = getReceiver(target, skillUser, enemyPokemon);
        SubField receiverField = getReceiverField(target, userField, enemyField);
        finalAmount = amount;
        if (statDirection == StatDirection.DECREASE) {
            //Attempt to lower the speed stage.
            if (receiverField.hasMist()) {
                addEffectMessage(effectMessages,"Mist prevents " + effectReceiver.getName() + " from losing speed!");
            } else if (effectReceiver.getSpeedStage() == -6) {
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s speed can't be lowered.");
            } else {

                effectReceiver.decreaseSpeedStage(finalAmount);
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s speed" + getFallText());
            }
        } else {
            //Attempt to increase the speed stage.
            if (effectReceiver.getSpeedStage() == 6) {
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s speed can't get higher.");
            } else {
                effectReceiver.increaseSpeedStage(finalAmount);
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s speed" + getRoseText());
            }
        }
        return effectMessages;
    }
}