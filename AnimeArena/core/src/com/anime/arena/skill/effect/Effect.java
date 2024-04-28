package com.anime.arena.skill.effect;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;
import com.anime.arena.skill.MoveEffectSubtype;
import com.anime.arena.skill.SkillTarget;
import com.badlogic.gdx.Gdx;

import java.util.List;

public abstract class Effect {
    private MoveEffectSubtype subtype;
    protected SkillTarget target;
    protected PokemonType moveType;


    public Effect(MoveEffectSubtype subtype) {
        this.subtype = subtype;
    }
    public Effect(MoveEffectSubtype subtype, SkillTarget target) {
        this.subtype = subtype;
        this.target = target;
    }


    public MoveEffectSubtype getEffectType() {
        return subtype;
    }

    public void setMoveType(PokemonType moveType) {
        this.moveType = moveType;
    }

    public abstract List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field,
                             SubField userField, SubField enemyField, boolean isFirstAttack);

    public void use(List<String> results, BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field,
                             SubField userField, SubField enemyField, List<BattlePokemon> skillUserParty, List<BattlePokemon> enemyPokemonParty, boolean isFirstAttack)
    {

    }

    protected void addEffectMessage(List<String> effectMessages, String logTitle, String msg) {
        effectMessages.add(msg);
        Gdx.app.log(logTitle, msg);
    }

    public boolean willFailAsSingleEffect(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        return false;
    }

}
