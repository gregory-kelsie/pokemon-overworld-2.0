package com.anime.arena.skill.effect;

/**
 * EffectType is the enum for the database table effect-type
 */
public enum EffectType {
    STATUS(0), STAGE(1), WEATHER(2), TERRAIN(3), HAZZARD(4), BIND_TYPE(5), SPLASH(7);
    private final int value;
    private EffectType(int value) {
        this.value = value;
    }
    public static EffectType fromInt(int i) {
        for (EffectType n : EffectType.values()) {
            if (n.getValue() == i) { return n; }
        }
        return null;
    }
    public int getValue() {
        return value;
    }
}
