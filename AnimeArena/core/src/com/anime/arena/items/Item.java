package com.anime.arena.items;

import com.anime.arena.objects.Player;
import com.anime.arena.pokemon.BasePokemonFactory;
import com.anime.arena.pokemon.Pokemon;
import com.anime.arena.tools.TextFormater;

public class Item {
    protected int itemID;
    protected String name;
    protected String description;
    protected String itemIcon;
    protected int itemType;

    public Item() {

    }

    public Item(int itemID, String name, String description, String itemIcon, int itemType) {
        this.itemID = itemID;
        this.name = name;
        this.description = TextFormater.formatText(description);
        this.itemIcon = itemIcon;
        this.itemType = itemType;
    }

    public boolean isTossable() {
        return true;
    }

    public int getItemID() {
        return itemID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getItemIcon() {
        return itemIcon;
    }

    public int getItemType() { return itemType; }

    public boolean isUsable() { return false; }

    public boolean use(Player player, Pokemon p, BasePokemonFactory factory) {
        return false;
    }

    public boolean use(Player player, ItemFactory itemFactory) {
        return false;
    }
}
