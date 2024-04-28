package com.anime.arena.skill.effect;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.Pokemon;
import com.anime.arena.skill.MoveEffectSubtype;
import com.anime.arena.skill.SecondaryEffect;
import com.anime.arena.skill.SkillTarget;
import com.anime.arena.skill.StatDirection;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public abstract class StatEffect extends Effect {
    protected int amount;
    protected int finalAmount; //Amount after ability or skill adjustments. Ex: Simple Ability
    protected StatDirection statDirection;



    public StatEffect(SkillTarget target, int amount, StatDirection statDirection) {
        super(MoveEffectSubtype.STAT_CHANGE, target);
        this.amount = amount;
        this.statDirection = statDirection;
    }

    public List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        return new ArrayList<String>();
    }

    protected SubField getReceiverField(SkillTarget target, SubField userField, SubField enemyField) {
        return target == SkillTarget.ENEMY ? enemyField : userField;
    }

    protected BattlePokemon getReceiver(SkillTarget target, BattlePokemon skillUser, BattlePokemon enemyPokemon) {
        return target == SkillTarget.ENEMY ? enemyPokemon : skillUser;
    }

    private void log(String str) {
        Gdx.app.log("AttackEffect", str);
    }

    protected void addEffectMessage(List<String> effectMessages, String msg) {
        effectMessages.add(msg);
        log(msg);
    }

    protected String getFallText() {
        if (finalAmount == 1) {
            return " fell!";
        } else if (finalAmount == 2) {
            return " harshly fell!";
        } else {
            return " severely fell!";
        }
    }

    protected String getRoseText() {
        if (finalAmount == 1) {
            return " rose!";
        } else if (finalAmount == 2) {
            return " sharply rose!";
        } else {
            return " drastically rose!";
        }
    }

}
