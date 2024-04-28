package com.anime.arena.skill;


public enum BindType {
    BIND_MOVE_TYPE(0), CLAMP(1), WHIRLPOOL(2), FIRE_SPIN(3), MAGMA_STORM(4), INFESTATION(5), WRAP(6), SAND_TOMB(7);
    private final int value;
    private BindType(int value) {
        this.value = value;
    }
    public static BindType fromInt(int i) {
        for (BindType n : BindType.values()) {
            if (n.getValue() == i) { return n; }
        }
        return null;
    }
    public int getValue() {
        return value;
    }
}
