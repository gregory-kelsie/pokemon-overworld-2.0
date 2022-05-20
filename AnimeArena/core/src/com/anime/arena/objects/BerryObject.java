package com.anime.arena.objects;


import com.anime.arena.AnimeArena;
import com.anime.arena.interactions.TextBox;
import com.anime.arena.interactions.TextBoxFactory;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.Date;

public class BerryObject extends WorldObject {
    protected float stateTimer;
    protected String berryName;
    protected int amount; //The amount of berries on the tree.
    protected int exp; //Berry Gathering Exp per berry.
    protected int requiredLevel; //The required Berry Gathering level to gather berries.
    protected int respawnTime;
    protected long harvestTime;
    protected int berryTreeId;
    protected PlayScreen screen;
    protected Sprite sprite;
    protected Animation<TextureRegion> berryTreeAnimation;

    public BerryObject(int berryTreeId, int x, int y, PlayScreen screen, TextureAtlas.AtlasRegion region) {
        super(x, y, screen);
        this.berryName = "";
        this.berryTreeId = berryTreeId;
        this.screen = screen;
        sprite = new Sprite(region);
        sprite.setBounds(x * 16 - 2, y * 16, 22 / AnimeArena.PPM, 34 / AnimeArena.PPM);
        initAnimation();
        this.harvestTime = 0;
        respawnTime = 1;
    }

    public String getBerryName() {
        return berryName;
    }

    public int getExp(int amount) {
        return amount * exp;
    }

    public int getBerryAmount() {
        return 1;
    }

    public Sprite getSprite() {
        return sprite;
    }

    protected int getSpriteX(int spriteXOffset) {
        return sprite.getRegionX() + spriteXOffset;
    }
    protected int getSpriteY(int spriteYOffset) {
        return sprite.getRegionY() + spriteYOffset;
    }

    protected void initAnimation() {

    }

    @Override
    public boolean isVisible() {
        if (harvestTime == 0) {
            return true;
        }
        return false;
    }

    public void setHarvestTime(long harvestTime) {
        this.harvestTime = harvestTime;
    }


    @Override
    public void manualInteract(Player player) {
        if (isVisible()) {
            //harvest
            Date date = new Date();
            long newHarvestTime = date.getTime();
            this.harvestTime = newHarvestTime;
            TextBoxFactory tbf = new TextBoxFactory(screen);
            TextBox tb = tbf.getBerryTextBox(this);
            screen.setEvent(tb);

        }
    }
    public Texture getFrame(float dt) {
        return berryTreeAnimation.getKeyFrame(stateTimer, true).getTexture();
    }

    public void update(float dt) {
        if (harvestTime == 0) {
            stateTimer += dt;
            if (stateTimer >= berryTreeAnimation.getAnimationDuration()) {
                stateTimer = 0;
            }

            sprite.setRegion(berryTreeAnimation.getKeyFrame(stateTimer, true));
        } else {
            Date date = new Date();
            long elapsedHarvestTime = date.getTime() - harvestTime;
            if ((elapsedHarvestTime / 1000) / 60 >= respawnTime) {
                harvestTime = 0; //Respawn the berry tree.
            }
        }

    }



}
