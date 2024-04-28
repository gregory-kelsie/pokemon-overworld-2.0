package com.anime.arena.skill.effect;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.skill.SkillTarget;
import com.anime.arena.skill.StatDirection;

import java.util.ArrayList;
import java.util.List;

public class EvasionEffect extends StatEffect{
    public EvasionEffect(SkillTarget target, StatDirection direction, int amount) {
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
                addEffectMessage(effectMessages, effectReceiver.getName() + "'s evasion cannot be\nlowered due to " +
                        effectReceiver.getAbility());
            } else if (receiverField.hasMist()) {
                addEffectMessage(effectMessages,"Mist prevents " + effectReceiver.getName() + " from losing evasion!");
            } else if (effectReceiver.getEvasionStage() == -6) {
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s evasion can't be lowered.");
            } else {

                effectReceiver.decreaseEvasionStage(finalAmount);
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s evasion" + getFallText());
            }
        } else {
            //Attempt to increase the evasion stage.
            if (effectReceiver.getEvasionStage() == 6) {
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s evasion can't get higher.");
            } else {
                effectReceiver.increaseEvasionStage(finalAmount);
                addEffectMessage(effectMessages,effectReceiver.getName() + "'s evasion" + getRoseText());
            }
        }
        return effectMessages;
    }
}
