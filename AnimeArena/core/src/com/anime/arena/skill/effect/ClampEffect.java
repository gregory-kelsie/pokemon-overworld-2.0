package com.anime.arena.skill.effect;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.skill.MoveEffectSubtype;
import com.anime.arena.skill.SkillTarget;

import java.util.ArrayList;
import java.util.List;

public class ClampEffect extends Effect {
    public ClampEffect() {
        super(MoveEffectSubtype.OTHER, SkillTarget.ENEMY);
    }

    @Override
    public List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        List<String> results = new ArrayList<String>();
        if (!enemyPokemon.isClamped()) {
            enemyPokemon.clamp();
            results.add(skillUser.getName() + " clamped\nthe enemy " + enemyPokemon.getName() +"!");
        }
        return results;
    }



    public boolean willFailAsSingleEffect(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField,
                                          SubField enemyField, boolean isFirstAttack) {
        return enemyPokemon.isBinded() ? true : false;
    }
}
