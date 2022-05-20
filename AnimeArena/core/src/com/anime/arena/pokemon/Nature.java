package com.anime.arena.pokemon;

/*
 * A Pokemon's Nature type.
 */
public enum Nature {
    /**
     * Hardy Nature (Neutral)
     */
    HARDY(0),

    /**
     * Lonely Nature (+ Atk, - Def)
     */
    LONELY(1),

    /**
     * Brave Nature (+ Atk, - Speed)
     */
    BRAVE(2),

    /**
     * Adamant Nature (+ Atk, - Sp. Atk)
     */
    ADAMANT(3),

    /**
     * Naughty Nature (+ Atk, - Sp. Def)
     */
    NAUGHTY(4),

    /**
     * Bold Nature (+ Def, - Atk)
     */
    BOLD(5),

    /**
     * Docile Nature (Neutral)
     */
    DOCILE(6),

    /**
     * Relaxed Nature (+ Def, - Speed)
     */
    RELAXED(7),

    /**
     * Impish Nature (+ Def, - Sp Atk)
     */
    IMPISH(8),

    /**
     * Lax Nature (+ Def, - Sp Def)
     */
    LAX(9),

    /**
     * Timid Nature (+ Speed, - Atk)
     */
    TIMID(10),

    /**
     * Hasty Nature (+ Speed, - Def)
     */
    HASTY(11),

    /**
     * Serious Nature (Neutral)
     */
    SERIOUS(12),

    /**
     * Jolly Nature (+ Speed, - Sp Atk)
     */
    JOLLY(13),

    /**
     * Naive Nature (+ Speed, - Sp Def)
     */
    NAIVE(14),

    /**
     * Modest Nature (+ Sp Atk, - Atk)
     */
    MODEST(15),

    /**
     * Mild Nature (+ Sp Atk, - Def)
     */
    MILD(16),

    /**
     * Quiet Nature (+ Sp Atk, - Speed)
     */
    QUIET(17),

    /**
     * Bashful Nature (Neutral)
     */
    BASHFUL(18),

    /**
     * Rash Nature (+ Sp Atk, - Sp Def)
     */
    RASH(19),

    /**
     * Calm Nature (+ Sp Def, - Atk)
     */
    CALM(20),

    /**
     * Gentle Nature (+ Sp Def, - Def)
     */
    GENTLE(21),

    /**
     * Sassy Nature (+ Sp Def, - Speed)
     */
    SASSY(22),

    /**
     * Careful Nature (+ Sp Def, - Sp Atk)
     */
    CAREFUL(23),

    /**
     * Quirky Nature (Neutral)
     */
    QUIRKY(24);
    private final int value;
    private Nature(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public static Nature fromInt(int i) {
        for (Nature n : Nature.values()) {
            if (n.getValue() == i) { return n; }
        }
        return null;
    }

    public static String toString(Nature t) {
        if (t == HARDY) {
            return "Hardy";
        } else if (t == LONELY) {
            return "Lonely";
        } else if (t == ADAMANT) {
            return "Adamant";
        } else if (t == MODEST) {
            return "Modest";
        } else if (t == JOLLY) {
            return "Jolly";
        } else if (t == QUIRKY) {
            return "Quirky";
        } else if (t == CAREFUL) {
            return "Careful";
        } else if (t == SASSY) {
            return "Sassy";
        } else if (t == GENTLE) {
            return "Gentle";
        } else if (t == CALM) {
            return "Calm";
        } else if (t == RASH) {
            return "Rash";
        } else if (t == BASHFUL) {
            return "Bashful";
        } else if (t == QUIET) {
            return "Quiet";
        } else if (t == MILD) {
            return "Mild";
        } else if (t == NAIVE) {
            return "Naive";
        } else if (t == SERIOUS) {
            return "Serious";
        } else if (t == HASTY) {
            return "Hasty";
        } else if (t == TIMID) {
            return "Timid";
        } else if (t == IMPISH) {
            return "Impish";
        } else if (t == DOCILE) {
            return "Docile";
        } else if (t == BOLD) {
            return "Bold";
        } else if (t == NAUGHTY) {
            return "Naughty";
        } else if (t == BRAVE) {
            return "Brave";
        } else if (t == LAX) {
            return "Lax";
        } else if (t == RELAXED) {
            return "Relaxed";
        }
        return "Error - " + t.toString();
    }
}