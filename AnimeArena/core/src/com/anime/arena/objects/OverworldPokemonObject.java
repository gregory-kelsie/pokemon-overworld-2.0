package com.anime.arena.objects;

import com.anime.arena.interactions.Event;
import com.anime.arena.pokemon.Pokemon;
import com.anime.arena.screens.PlayScreen;
import com.anime.arena.tools.MovementScript;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;
import java.util.List;

public class OverworldPokemonObject extends NPCObject {
    private int pokemonID;
    private boolean isShiny;
    private int level;
    private int attackEV;
    private int defenseEV;
    private int spAttackEV;
    private int spDefenseEV;
    private int speedEV;
    private int healthEV;
    private int attackIV;
    private int defenseIV;
    private int spAttackIV;
    private int spDefenseIV;
    private int speedIV;
    private int healthIV;

    public OverworldPokemonObject(int x, int y, PlayScreen screen, Sprite npcSprite, MovementScript movementScript,
                                  boolean catchable, String battleBGM, Event beforeBattleEvent, Event afterDefeatEvent, Pokemon pokemon) {
        super(x, y, screen, npcSprite, beforeBattleEvent, movementScript, new ArrayList<List<Integer>>());
    }
}
