package com.anime.arena.field;

import com.anime.arena.pokemon.StatusCondition;

public enum WeatherType {
    NORMAL(0), RAIN(1), SUN(2), SAND(3), HAIL(4), HARSH_SUNSHINE(5), HEAVY_RAIN(6), STRONG_WINDS(7);
    private final int value;
    private WeatherType(int value) {
        this.value = value;
    }
    public static WeatherType fromInt(int i) {
        for (WeatherType n : WeatherType.values()) {
            if (n.getValue() == i) { return n; }
        }
        return null;
    }
    public int getValue() {
        return value;
    }
}
