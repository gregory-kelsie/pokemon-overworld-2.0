package com.anime.arena.battle;

import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.skill.Skill;

public class BattleStatePokemon {
    private boolean isUser;
    private BattlePokemon pokemon;
    private Skill skill;

    public BattleStatePokemon(BattlePokemon pokemon, Skill skill) {
        this.pokemon = pokemon;
        this.skill = skill;
        this.isUser = false;
    }

    public Skill getSkill() {
        return skill;
    }

    public BattlePokemon getPokemon() {
        return pokemon;
    }

    public void setUser(boolean isUser) {
        this.isUser = isUser;
    }

    public boolean isUser() {
        return isUser;
    }
}
