package com.anime.arena.pokemon;

/**
 * Pokemon types, Fire, Grass, Water etc.
 * None is also a type when the Pokemon doesn't have a second type.
 */
public enum PokemonType {
    NONE(0), BUG(1), DARK(2), DRAGON(3), ELECTRIC(4), FAIRY(5), FIGHTING(6),
    FIRE(7), FLYING(8), GHOST(9), GRASS(10), GROUND(11),
    ICE(12), NORMAL(13), POISON(14), PSYCHIC(15), ROCK(16), STEEL(17), WATER(18);
    private final int value;
    private PokemonType(int value) {
        this.value = value;
    }
    public static PokemonType fromInt(int i) {
        for (PokemonType n : PokemonType.values()) {
            if (n.getValue() == i) { return n; }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public static String toString(PokemonType t) {
        if (t == NONE) {
            return "None";
        } else if (t == BUG) {
            return "Bug";
        } else if (t == DARK) {
            return "Dark";
        } else if (t == DRAGON) {
            return "Dragon";
        } else if (t == ELECTRIC) {
            return "Electric";
        } else if (t == FAIRY) {
            return "Fairy";
        } else if (t == FIGHTING) {
            return "Fighting";
        } else if (t == FIRE) {
            return "Fire";
        } else if (t == FLYING) {
            return "Flying";
        } else if (t == GHOST) {
            return "Ghost";
        } else if (t == GRASS) {
            return "Grass";
        } else if (t == GROUND) {
            return "Ground";
        } else if (t == ICE) {
            return "Ice";
        } else if (t == NORMAL) {
            return "Normal";
        } else if (t == POISON) {
            return "Poison";
        } else if (t == PSYCHIC) {
            return "Psychic";
        } else if (t == ROCK) {
            return "Rock";
        } else if (t == STEEL) {
            return "Steel";
        } else if (t == WATER) {
            return "Water";
        }
        return "None";
    }

}
