package com.anime.arena.tools;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteUtils {
    public static int getSpriteX(Sprite sprite, int spriteXOffset) {
        return sprite.getRegionX() + spriteXOffset;
    }
    public static int getSpriteY(Sprite sprite, int spriteYOffset) {
        return sprite.getRegionY() + spriteYOffset;
    }
}
