package com.anime.arena.objects;

import com.anime.arena.screens.PlayScreen;

public class WarpObject extends WorldObject {
    private String mapName;

    //Which tile position the player will get warped to.
    private int positionX;
    private int positionY;
    public WarpObject(String mapName, int positionX, int positionY, int x, int y, PlayScreen screen) {
        super(x, y, screen);
        this.positionX = positionX;
        this.positionY = positionY;
        this.mapName = mapName;
        this.visible = false;

    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public String getMapName() {
        return mapName;
    }


}
