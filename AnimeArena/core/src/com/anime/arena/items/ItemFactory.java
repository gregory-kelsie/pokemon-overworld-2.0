package com.anime.arena.items;

import com.anime.arena.pokemon.BasePokemon;
import com.anime.arena.pokemon.StatusCondition;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemFactory {
    private String itemResponse;
    private Map<Integer, JsonObject> database;
    private Map<Integer, Item> itemDB;
    public ItemFactory(String itemResponse, Map<Integer, JsonObject> database) {
        this.itemResponse = itemResponse;
        this.database = database;
        itemDB = new HashMap<Integer, Item>();
        for (Integer key : database.keySet()) {
            itemDB.put(key, createItem(database.get(key)));
        }
    }

    public Item createItem(int itemID) {
        return createItem(database.get(itemID));
    }

    public Item createItem(JsonObject jsonItem) {
        if (jsonItem != null) {
            int itemID = jsonItem.get("item_id").getAsInt();
            int itemType = jsonItem.get("type").getAsInt();
            String itemName = jsonItem.get("name").getAsString();
            String description = jsonItem.get("description").getAsString();
            String image = jsonItem.get("icon").getAsString();
            if (itemType == 0) {
                Medicine m = new Medicine(itemID, itemName, description, image, itemType);
                if (!jsonItem.get("heal_amount").isJsonNull()) {
                    m.setHealAmount(jsonItem.get("heal_amount").getAsInt());
                }
                if (!jsonItem.get("status_rec").isJsonNull()) {
                    m.setHealStatus(StatusCondition.fromInt(jsonItem.get("status_rec").getAsInt()));
                }
                if (!jsonItem.get("revive").isJsonNull()) {
                    m.setRevive(jsonItem.get("revive").getAsInt());
                }
                if (!jsonItem.get("full_health").isJsonNull()) {
                    m.setFullyHeal(jsonItem.get("full_health").getAsInt());
                }
                if (!jsonItem.get("clear_status").isJsonNull()) {
                    m.setHealAnyStatus(jsonItem.get("clear_status").getAsInt());
                }
                return m;
            } else if (itemType == 7) {
                Pokeball pokeball = new Pokeball(itemID, itemName, description, image, itemType);
                if (!jsonItem.get("catch_rate").isJsonNull()) {
                    pokeball.setCatchRate(jsonItem.get("catch_rate").getAsDouble());
                }
                return pokeball;
            } else if (itemType == 3) {
                EvolutionStone stone = new EvolutionStone(itemID, itemName, description, image, itemType);
                if (!jsonItem.get("evolution_method").isJsonNull()) {
                    stone.setEvolutionMethod(jsonItem.get("evolution_method").getAsInt());
                }
                return stone;
            } else if (itemType == 10) {
                Vitamin vitamin = new Vitamin(itemID, itemName, description, image, itemType);
                if (!jsonItem.get("level_up").isJsonNull()) {
                    vitamin.setLevelUp(jsonItem.get("level_up").getAsBoolean());
                }
                if (!jsonItem.get("hp_ev").isJsonNull()) {
                    vitamin.setHpEV(jsonItem.get("hp_ev").getAsInt());
                }
                if (!jsonItem.get("atk_ev").isJsonNull()) {
                    vitamin.setAtkEV(jsonItem.get("atk_ev").getAsInt());
                }
                if (!jsonItem.get("def_ev").isJsonNull()) {
                    vitamin.setDefEV(jsonItem.get("def_ev").getAsInt());
                }
                if (!jsonItem.get("spatk_ev").isJsonNull()) {
                    vitamin.setSpatkEV(jsonItem.get("spatk_ev").getAsInt());
                }
                if (!jsonItem.get("spdef_ev").isJsonNull()) {
                    vitamin.setSpdefEV(jsonItem.get("spdef_ev").getAsInt());
                }
                if (!jsonItem.get("spd_ev").isJsonNull()) {
                    vitamin.setSpdEV(jsonItem.get("spd_ev").getAsInt());
                }
                return vitamin;
            }
        }
        return null;
    }
}
