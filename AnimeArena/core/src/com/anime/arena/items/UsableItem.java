package com.anime.arena.items;

import com.anime.arena.pokemon.BasePokemonFactory;
import com.anime.arena.pokemon.Pokemon;
import com.anime.arena.pokemon.UniquePokemon;

public interface UsableItem {
    public boolean use(Pokemon p, BasePokemonFactory factory);
    //Instead of returning a boolean possibly return a ItemUseResult.
    //Has a String with the display message and a boolean for whether or not the item was used.
    //Has a factory to create pokemon as a result of using an item (evolution stones)
}
