package com.anime.arena.tools;

public enum Movement { MOVE_UP(0), MOVE_RIGHT(1), MOVE_DOWN(2), MOVE_LEFT(3), LOOK_UP(4), LOOK_DOWN(5), LOOK_LEFT(6), LOOK_RIGHT(7);

    private final int value;
    private Movement(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
    public Movement getReversedMovement() {
        if (this == MOVE_UP) {
            return MOVE_DOWN;
        } else if (this == MOVE_DOWN) {
            return MOVE_UP;
        } else if (this == MOVE_RIGHT) {
            return MOVE_LEFT;
        } else if (this == MOVE_LEFT) {
            return MOVE_RIGHT;
        } else if (this == LOOK_UP) {
            return LOOK_DOWN;
        } else if (this == LOOK_DOWN) {
            return LOOK_UP;
        } else if (this == LOOK_RIGHT) {
            return LOOK_LEFT;
        } else if (this == LOOK_LEFT) {
            return LOOK_RIGHT;
        }
        return null;
    }
};