package com.anime.arena.skill.effect;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.field.WeatherType;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.skill.MoveEffectSubtype;
import com.anime.arena.skill.SkillTarget;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class BindEffect extends Effect {
    public BindEffect() {
        super(MoveEffectSubtype.OTHER, SkillTarget.ENEMY);
    }

    @Override
    public List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        List<String> results = new ArrayList<String>();
        if (!enemyPokemon.isBinded()) {
            enemyPokemon.bind();
            results.add(enemyPokemon.getName() + " was squeezed\nby " + skillUser.getName() +"!");
        }
        return results;
    }



    public boolean willFailAsSingleEffect(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField,
                                          SubField enemyField, boolean isFirstAttack) {
        return enemyPokemon.isBinded() ? true : false;
    }
}
