package com.anime.arena.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BattleAnimationBackground {
    private TextureRegion redbg;
    private TextureRegion redbg2;

    private int imageWidth;
    private int imageHeight;
    private float imageScreenXPos;
    private int imageScreenYPos;
    private float imageScaleFactor;
    private int animationSpeed;

    private Texture backgroundTexture;
    private int offset;

    public static final int FINAL_LEFT_X_POSITION = 0;
    private static final int FINAL_Y_POSITION = 1300;

    private boolean animateBackground;

    public BattleAnimationBackground(String background) {
        this.backgroundTexture = new Texture(background);
        redbg = new TextureRegion(backgroundTexture);
        redbg2 = new TextureRegion(backgroundTexture);
        imageWidth = 128;
        imageHeight = 64;

        imageScreenYPos = FINAL_Y_POSITION;
        animationSpeed = 600;
        imageScaleFactor = 4.21875f;
        imageScreenXPos = Math.round(-imageWidth * imageScaleFactor);
        offset = 0;

        animateBackground = false;
    }

    public BattleAnimationBackground(String background, int posX, int posY) {
        this.backgroundTexture = new Texture(background);
        redbg = new TextureRegion(backgroundTexture);
        redbg2 = new TextureRegion(backgroundTexture);
        imageWidth = 128;
        imageHeight = 64;
        imageScreenXPos = posX;
        imageScreenYPos = posY;
        animationSpeed = 600;
        imageScaleFactor = 4.21875f;
        offset = 0;

        animateBackground = false;
    }

    public float getX() {
        return imageScreenXPos;
    }

    public void setX(float x) {
        this.imageScreenXPos = x;
    }

    public float getImageWidth() {
        return imageWidth * imageScaleFactor;
    }

    public float getImageHeight() {
        return imageHeight * imageScaleFactor;
    }


    public void update(float dt) {
        if (animateBackground) {
            offset = Math.round(offset + dt * animationSpeed);
            if (offset >= imageWidth) {
                offset = 0;
            }
            redbg.setRegion(offset, 0, imageWidth, imageHeight);
            redbg2.setRegion(0, 0, offset, imageHeight);
        }
    }

    public void setAnimateBackground(boolean animateBackground) {
        this.animateBackground = animateBackground;
    }

    public void render(float delta, SpriteBatch batch) {
        batch.draw(redbg, imageScreenXPos, imageScreenYPos, imageWidth * imageScaleFactor, imageHeight * imageScaleFactor);
        batch.draw(redbg2, (imageScreenXPos + (imageWidth * imageScaleFactor)) - (offset * imageScaleFactor),
                imageScreenYPos, offset * imageScaleFactor, imageHeight * imageScaleFactor);
    }

    public void dispose() {
        backgroundTexture.dispose();
    }
}
