package com.anime.arena.skill;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;
import com.badlogic.gdx.Gdx;

import java.util.List;

public class BurnEffect extends Effect {
    public BurnEffect(int id, MoveEffectSubtype subtype, SkillTarget target) {
        super(id, subtype, target);
    }

    @Override
    public void use(List<String> results, BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        if (enemyPokemon.isStatused()) {
            if (enemyPokemon.getFirstType() != PokemonType.FIRE && enemyPokemon.getSecondType() != PokemonType.FIRE) {
                enemyPokemon.inflictBurn();
                Gdx.app.log("StatusEffect", enemyPokemon.getPokemon().getConstantVariables().getName() + " was burned!");
            } else {
                Gdx.app.log("StatusEffect", "The burn effect failed because the enemy pokemon is a FIRE type");
            }
        } else {
            Gdx.app.log("StatusEffect", "The burn effect failed because the enemy pokemon is statused: "+ enemyPokemon.getPokemon().getUniqueVariables().getStatus());
        }
    }
}
