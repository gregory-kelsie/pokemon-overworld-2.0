package com.anime.arena.test;

import com.anime.arena.field.Field;
import com.anime.arena.pokemon.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.List;

public class BattleTest {

    private BattlePokemon battleUser;
    private BattlePokemon battleEnemy;
    private int battlePokemonPosition;
    private Field field;
    private List<Pokemon> party;
    public BattleTest(List<Pokemon> party, BasePokemonFactory factory, TextureAtlas pokemonAtlas, TextureAtlas pokemonBackAtlas) {

//        Pokemon user = PokemonUtils.createTestPokemon("bulbasaur", factory);
//        Pokemon pidgey = PokemonUtils.createTestPokemon("raticate", factory, 20);
        Pokemon user = PokemonUtils.createPokemon(4, 3, factory);
        Pokemon pidgey = PokemonUtils.createPokemon(24, 5, factory);
        user.getUniqueVariables().getMoves().set(0, PokemonUtils.createPoisonpowder());
        user.getUniqueVariables().getMoves().set(1, PokemonUtils.createHail());
        //user.getUniqueVariables().setAbility(AbilityId.DRY_SKIN.getValue());
        //Clamp
        user.getUniqueVariables().getMoves().set(0, factory.getMove(337));
        party.add(user);
        party.add(pidgey);
        //user.getUniqueVariables().getMoves().add(PokemonUtils.createTackleMove());
//        pidgey.getUniqueVariables().getMoves().add(PokemonUtils.createTackleMove());
//        pidgey.getUniqueVariables().getMoves().add(PokemonUtils.createHail());
        Pokemon opponent = PokemonUtils.createPokemon(19, 20, factory); //24 arbok 242 blissey
        opponent.getUniqueVariables().getMoves().set(0, PokemonUtils.createTackleMove());
        opponent.getUniqueVariables().setAbility(AbilityId.DRY_SKIN.getValue());
        //opponent.setCurrentHealth(1);
        //opponent.getUniqueVariables().getMoves().add(PokemonUtils.createScratchMove());
        battlePokemonPosition = 0;
        this.battleUser = new BattlePokemon(party.get(battlePokemonPosition), pokemonBackAtlas, true);
        this.battleEnemy = new BattlePokemon(opponent, pokemonAtlas, false);
        this.field = new Field();
        this.party = party;
    }

    public int getBattlePokemonPosition() {
        return battlePokemonPosition;
    }

    public void setBattlePokemonPosition(int battlePokemonPosition) {
        this.battlePokemonPosition = battlePokemonPosition;
    }

    public void setUserPokemon(Pokemon newUser, int battlePokemonPosition, TextureAtlas pokemonBackAtlas) {
        battleUser = new BattlePokemon(newUser, pokemonBackAtlas, true);
        this.battlePokemonPosition = battlePokemonPosition;
    }

    public Field getField() {
        return field;
    }

    public BattlePokemon getUserPokemon() {
        return battleUser;
    }

    public BattlePokemon getEnemyPokemon() {
        return battleEnemy;
    }

    public List<Pokemon> getParty() {
        return party;
    }

    public void startBattle() {


    }
}
