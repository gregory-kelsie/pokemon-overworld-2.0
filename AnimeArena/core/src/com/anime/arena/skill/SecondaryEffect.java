package com.anime.arena.skill;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.Pokemon;

import java.util.List;

public abstract class SecondaryEffect {

    protected SkillTarget target;

    public SecondaryEffect(SkillTarget target) {
        this.target = target;
    }

    public SkillTarget getTarget() {
        return target;
    }

    public abstract void use(List<String> results, Pokemon skillUser,
                             Pokemon enemyPokemon, Field field, SubField userField,
                             SubField enemyField, boolean isFirstAttack);
}
