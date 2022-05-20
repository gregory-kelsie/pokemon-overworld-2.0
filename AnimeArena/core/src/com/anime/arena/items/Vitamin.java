package com.anime.arena.items;

import com.anime.arena.pokemon.BasePokemonFactory;
import com.anime.arena.pokemon.Pokemon;

public class Vitamin extends Item {

    private boolean levelUp;
    private int hpEV;
    private int atkEV;
    private int defEV;
    private int spatkEV;
    private int spdefEV;
    private int spdEV;
    public Vitamin(int itemID, String name, String description, String itemImage, int itemType) {
        super(itemID, name, description, itemImage, itemType);
        this.levelUp = false;
        this.hpEV = 0;
        this.atkEV = 0;
        this.defEV = 0;
        this.spatkEV = 0;
        this.spdefEV = 0;
        this.spdEV = 0;
    }

    public void setLevelUp(boolean levelUp) {
        this.levelUp = levelUp;
    }

    public void setHpEV(int hpEV) {
        this.hpEV = hpEV;
    }

    public void setAtkEV(int atkEV) {
        this.atkEV = atkEV;
    }

    public void setDefEV(int defEV) {
        this.defEV = defEV;
    }

    public void setSpatkEV(int spatkEV) {
        this.spatkEV = spatkEV;
    }

    public void setSpdefEV(int spdefEV) {
        this.spdefEV = spdefEV;
    }

    public void setSpdEV(int spdEV) {
        this.spdEV = spdEV;
    }

    public boolean use(Pokemon pokemon, BasePokemonFactory factory) {
        boolean usedItem = false;
        return usedItem;
    }

    public boolean isUsable() {
        return true;
    }
}
