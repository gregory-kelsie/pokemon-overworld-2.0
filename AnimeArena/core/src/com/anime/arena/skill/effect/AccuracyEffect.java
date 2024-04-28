package com.anime.arena.skill.effect;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.skill.SkillTarget;
import com.anime.arena.skill.StatDirection;

import java.util.ArrayList;
import java.util.List;

public class AccuracyEffect extends StatEffect {
    public AccuracyEffect(SkillTarget target, StatDirection direction, int amount) {
        super(target, amount, direction);
    }

    @Override
    public List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        List<String> effectMessages = new ArrayList<String>();
        BattlePokemon effectReceiver = getReceiver(target, skillUser, enemyPokemon);
        SubField receiverField = getReceiverField(target, userField, enemyField);
        finalAmount = amount;
        if (statDirection == StatDirection.DECREASE) {
            //Attempt to lower the accuracy stage.
            if (effectReceiver.isProtectedByDefenseLoweringEffects()) {
                addEffectMessage(effectMessages, effectReceiver.getName() + "'s accuracy cannot be\nlowered due to " +
                        effectReceiver.getAbility());
            } else if (receiverField.hasMist()) {
                addEffectMessage(effectMessages,"Mist prevents " + effectReceiver.getName() + " from losing accuracy!");
            } else if (effectReceiver.getAccuracyStage() == -6) {
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s accuracy can't be lowered.");
            } else {

                effectReceiver.decreaseAccuracyStage(finalAmount);
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s accuracy" + getFallText());
            }
        } else {
            //Attempt to increase the accuracy stage.
            if (effectReceiver.getAccuracyStage() == 6) {
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s accuracy can't get higher.");
            } else {
                effectReceiver.increaseAccuracyStage(finalAmount);
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s accuracy" + getRoseText());
            }
        }
        return effectMessages;
    }
}