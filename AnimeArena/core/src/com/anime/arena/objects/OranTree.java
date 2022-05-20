package com.anime.arena.objects;

import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class OranTree extends BerryObject {
    public OranTree(int berryTreeId, int x, int y, PlayScreen screen) {
        super(berryTreeId, x, y, screen, screen.getBerryAtlas().findRegion("oran"));
        berryName = "Oran";
        exp = 5;
        respawnTime = 1;
    }


    @Override
    protected void initAnimation() {
        sprite.getTexture().getWidth();
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(sprite.getTexture(), getSpriteX(0), getSpriteY(0), 22, 34));
        frames.add(new TextureRegion(sprite.getTexture(), getSpriteX(23), getSpriteY(0), 22, 34));
        berryTreeAnimation = new Animation<TextureRegion>(0.15f, frames);
    }

    public int getBerryAmount() {
        int playerLevel = 20; //Change to get it from the player object
        double rand = Math.random();
        if (playerLevel >= 1 && playerLevel < 5) {
            return 1;
        } else if (playerLevel >= 5 && playerLevel < 15) {
            if (rand <= .75) {
                return 1;
            }
            return 2;
        } else if (playerLevel >= 15 && playerLevel < 20) {
            if (rand <= .5) {
                return 1;
            }
            return 2;
        } else if (playerLevel >= 20 && playerLevel < 25) {
            if (rand <= .75) {
                return 2;
            }
            return 1;
        } else if (playerLevel >= 25 && playerLevel < 40) {
            return 2;
        } else if (playerLevel >= 40 && playerLevel < 50) {
            if (rand <= .75) {
                return 2;
            }
            return 3;
        } else if (playerLevel >= 50 && playerLevel < 60) {
            if (rand <= .75) {
                return 3;
            }
            return 2;
        } else if (playerLevel >= 60 && playerLevel < 75) {
            return 3;
        } else if (playerLevel >= 75 && playerLevel < 85) {
            if (rand <= .75) {
                return 3;
            }
            return 4;
        } else if (playerLevel >= 85 && playerLevel >= 95) {
            return 4;
        } else {
            if (rand <= 0.1) {
                return 6;
            } else if (rand <= 0.6) {
                return 5;
            }
            return 4;
        }
    }
}