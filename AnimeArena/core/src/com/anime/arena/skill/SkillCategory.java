package com.anime.arena.skill;
public enum SkillCategory {
    PHYSICAL(0), SPECIAL(1), MISC(2);
    private final int value;
    private SkillCategory(int value) {
        this.value = value;
    }
    public static SkillCategory fromInt(int i) {
        for (SkillCategory n : SkillCategory.values()) {
            if (n.getValue() == i) { return n; }
        }
        return null;
    }
    public int getValue() {
        return value;
    }
}