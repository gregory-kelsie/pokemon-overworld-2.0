package com.anime.arena.skill.effect;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.skill.MoveEffectSubtype;
import com.anime.arena.skill.SkillTarget;

import java.util.List;

public class RecoverEffect extends Effect {
    private double recoveryAmount;
    private boolean recoverPartyStatus;
    public RecoverEffect(MoveEffectSubtype subtype, SkillTarget target, double recoveryAmount) {
        super(subtype, target);
        this.recoveryAmount = recoveryAmount;
    }

    @Override
    public List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        skillUser.addHealth((int)Math.round(skillUser.getPokemon().getHealthStat() * recoveryAmount));
        return null;
    }

    @Override
    public void use(List<String> results, BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField,
                    List<BattlePokemon> skillUserParty, List<BattlePokemon> enemyPokemonParty, boolean isFirstAttack) {
        skillUser.addHealth((int)Math.round(skillUser.getPokemon().getHealthStat() * recoveryAmount));
        if (recoverPartyStatus) {
            for (int i = 0; i < skillUserParty.size(); i++) {
                if (skillUserParty.get(i).isStatused() &&
                        skillUserParty.get(i).getCurrentHealth() > 0) {
                    skillUserParty.get(i).wakeUp();//Wake up removes status ailments and sleep timer.
                }
            }
        }
    }
}
