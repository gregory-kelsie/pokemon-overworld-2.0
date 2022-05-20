package com.anime.arena.items;

import com.anime.arena.objects.Player;
import com.anime.arena.pokemon.BasePokemon;
import com.anime.arena.pokemon.BasePokemonFactory;
import com.anime.arena.pokemon.Pokemon;
import com.anime.arena.pokemon.PokemonUtils;

public class EvolutionStone extends Item {
    private int evolutionMethod;
    public EvolutionStone(int itemID, String name, String description, String itemImage, int itemType) {
        super(itemID, name, description, itemImage, itemType);
    }
    public int getEvolutionMethod() {
        return evolutionMethod;
    }

    public void setEvolutionMethod(int evolutionMethod) {
        this.evolutionMethod = evolutionMethod;
    }

    @Override
    public boolean use(Player player, Pokemon pokemon, BasePokemonFactory factory) {
        BasePokemon evolvedPokemon = PokemonUtils.getEvolvedPokemon(pokemon, evolutionMethod, factory);
        if (evolvedPokemon != null) {
            PokemonUtils.evolvePokemon(pokemon, evolvedPokemon);
            player.getPokedex().updateObtained(evolvedPokemon.getPID());
            return true;
        }
        return false;
    }

    public boolean isUsable() {
        return true;
    }
}
