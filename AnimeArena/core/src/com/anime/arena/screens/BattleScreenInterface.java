package com.anime.arena.screens;

import com.anime.arena.battle.ExpState;
import com.anime.arena.pokemon.BasePokemonFactory;
import com.anime.arena.pokemon.Pokemon;
import com.anime.arena.skill.Skill;

public interface BattleScreenInterface {
    public void switchFaintedPokemon(int battlePokemonPosition);
    public void switchPokemon(int battlePokemonPosition);
    public void switchOutFaintedPokemon();
    public BasePokemonFactory getBasePokemonFactory();
    public void setLearningMoveScreen(Pokemon learningPokemon, Skill learningMove, ExpState expState);
}
