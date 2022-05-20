package com.anime.arena.skill;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.Pokemon;

import java.util.List;

public abstract class StatEffect extends SecondaryEffect {
    protected int amount;
    protected int finalAmount; //Amount after ability or skill adjustments. Ex: Simple Ability
    protected StatDirection statDirection;

    protected Pokemon effectReceiver;
    protected SubField receiverField;

    public StatEffect(SkillTarget target, int amount, StatDirection statDirection) {
        super(target);
        this.amount = amount;
        this.statDirection = statDirection;
    }

    public void use(List<String> results, Pokemon skillUser,
                             Pokemon enemyPokemon, Field field, SubField userField,
                             SubField enemyField, boolean isFirstAttack) {
        if (target == SkillTarget.SELF) {
            effectReceiver = skillUser;
            receiverField = userField;
        } else {
            effectReceiver = enemyPokemon;
            receiverField = enemyField;
        }
//        if (effectReceiver.getBattleAbility().getId() == Ability.AbilityId.SIMPLE) {
//            finalAmount = amount * 2;
//        } else {
//            finalAmount = amount;
//        }
    }


}
