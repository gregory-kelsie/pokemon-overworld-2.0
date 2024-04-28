package com.anime.arena.skill.effect;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;
import com.anime.arena.skill.MoveEffectSubtype;
import com.anime.arena.skill.SkillTarget;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class BurnEffect extends Effect {
    public BurnEffect(SkillTarget target) {
        super(MoveEffectSubtype.STATUS, target);
    }

    @Override
    public List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        List<String> effectMessages = new ArrayList<String>();
        if (!enemyPokemon.isStatused()) {
            if (enemyPokemon.getFirstType() != PokemonType.FIRE && enemyPokemon.getSecondType() != PokemonType.FIRE) {
                enemyPokemon.inflictBurn();
                addEffectMessage(effectMessages, "BurnEffect",enemyPokemon.getPokemon().getConstantVariables().getName() + " was burned!");
            } else {
                Gdx.app.log("StatusEffect", "The burn effect failed because the enemy pokemon is a FIRE type");
            }
        } else {
            Gdx.app.log("StatusEffect", "The burn effect failed because the enemy pokemon is statused: "+ enemyPokemon.getPokemon().getUniqueVariables().getStatus());
        }
        return effectMessages;
    }

    public boolean willFailAsSingleEffect(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        if (enemyPokemon.isStatused() || enemyPokemon.getFirstType() == PokemonType.FIRE || enemyPokemon.getSecondType() == PokemonType.FIRE) {
            return true;
        }
        return false;
    }


}
