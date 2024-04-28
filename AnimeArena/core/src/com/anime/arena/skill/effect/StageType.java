package com.anime.arena.skill.effect;

public enum StageType {
    ATTACK(0), DEFENSE(1), SPECIAL_ATTACK(2), SPECIAL_DEFENSE(3), SPEED(4), ACCURACY(5), EVASION(6);
    private final int value;
    private StageType(int value) {
        this.value = value;
    }
    public static StageType fromInt(int i) {
        for (StageType n : StageType.values()) {
            if (n.getValue() == i) { return n; }
        }
        return null;
    }
    public int getValue() {
        return value;
    }
}
