package com.anime.arena.skill;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;

import java.util.List;

public abstract class Effect {
    private int id;
    private MoveEffectSubtype subtype;
    protected SkillTarget target;
    protected PokemonType moveType;

    public Effect(int id, MoveEffectSubtype subtype, SkillTarget target) {
        this.id = id;
        this.subtype = subtype;
        this.target = target;
    }

    public void setMoveType(PokemonType moveType) {
        this.moveType = moveType;
    }

    public abstract void use(List<String> results, BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field,
                             SubField userField, SubField enemyField, boolean isFirstAttack);

}
