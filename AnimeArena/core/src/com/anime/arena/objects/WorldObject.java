package com.anime.arena.objects;

import com.anime.arena.interactions.TextBoxFactory;
import com.anime.arena.screens.PlayScreen;

public class WorldObject {
    private final int MANUAL_INTERACTION = 0;
    private final int COLLISION_INTERACTION = 1;

    protected int interactionType;
    protected boolean visible;
    protected boolean collision;

    protected int x;
    protected int y;

    protected PlayScreen screen;
    public WorldObject(int x, int y, PlayScreen screen) {
        this.visible = true;
        this.collision = true;
        this.interactionType = MANUAL_INTERACTION;
        this.x = x;
        this.y = y;
        this.screen = screen;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean occupiesCell(int cellX, int cellY) {
        if (cellX == x && cellY == y) {
            return true;
        }
        return false;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean hasCollision() {
        return collision;
    }

    public void manualInteract(Player player) {

    }

    public void collisionInteract() {

    }

    public void dispose() {

    }

}
