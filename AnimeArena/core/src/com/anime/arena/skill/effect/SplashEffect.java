package com.anime.arena.skill.effect;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.skill.MoveEffectSubtype;
import com.anime.arena.skill.SkillTarget;

import java.util.ArrayList;
import java.util.List;

public class SplashEffect extends Effect {
    public SplashEffect() {
        super(MoveEffectSubtype.OTHER);
    }

    @Override
    public List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        List<String> effectMessages = new ArrayList<String>();
        effectMessages.add("But nothing happened!");
        return effectMessages;
    }

}
