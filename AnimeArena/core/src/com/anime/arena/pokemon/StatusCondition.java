package com.anime.arena.pokemon;

/**
 * Pokemon status conditions. (Poison, burn etc)
 * STATUS_FREE is when the Pokemon has no condition.
 */
public enum StatusCondition {
    STATUS_FREE(0), POISON(1), BURN(2), PARALYSIS(3), FROZEN(4), SLEEP(5), RECOVER(6);
    private final int value;
    private StatusCondition(int value) {
        this.value = value;
    }
    public static StatusCondition fromInt(int i) {
        for (StatusCondition n : StatusCondition.values()) {
            if (n.getValue() == i) { return n; }
        }
        return null;
    }
    public int getValue() {
        return value;
    }
}