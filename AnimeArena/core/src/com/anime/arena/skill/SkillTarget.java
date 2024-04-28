package com.anime.arena.skill;

import com.anime.arena.pokemon.StatusCondition;

public enum SkillTarget {
    ENEMY(0), SELF(1), SELF_OR_ALLY(2), ALL_ENEMIES(3), FIELD(4), ENEMY_SIDE(5), PLAYER_SIDE(6);
    private final int value;
    private SkillTarget(int value) {
        this.value = value;
    }
    public static SkillTarget fromInt(int i) {
        for (SkillTarget n : SkillTarget.values()) {
            if (n.getValue() == i) { return n; }
        }
        return null;
    }
    public int getValue() {
        return value;
    }
}
