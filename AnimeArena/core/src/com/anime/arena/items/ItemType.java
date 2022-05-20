package com.anime.arena.items;


/**
 * Item Types from the Database - Hold Item, Medicine etc
 */
public enum ItemType {
    MEDICINE(0), KEY_ITEM(2), EVOLUTION_STONE(3), BERRY(4), TM(5), CLOTHES(6),
    POKEBALL(7), HOLD_ITEM(8), BATTLE_ITEM(9), VITAMIN(10);
    private final int value;
    private ItemType(int value) {
        this.value = value;
    }
    public static ItemType fromInt(int i) {
        for (ItemType n : ItemType.values()) {
            if (n.getValue() == i) { return n; }
        }
        return null;
    }
    public int getValue() {
        return value;
    }
}