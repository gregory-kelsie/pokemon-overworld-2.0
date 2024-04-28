package com.anime.arena.battle.ui;

import com.anime.arena.tools.SpriteUtils;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class PlayerBattleAnimation extends Sprite {
    private Animation<TextureRegion> trainerAnimation;
    private Animation<TextureRegion> hairAnimation;
    private Animation<TextureRegion> clothesAnimation;
    private float stateTimer;
    private boolean isStarted;
    private boolean isFinishedAnimation;
    private final float frameDuration = 0.15f;
    private final float afterAnimationDelay = 1.0f;

    //Player Positions
    private final int startingPosition = 1080;
    private final int stoppingPosition = 120;
    private final int finalPosition = -350;

    private int state; // 0 is Starting -> Stopping, 1 is Stopping -> Final Position

    public PlayerBattleAnimation(TextureAtlas trainerBodyAtlas, String gender, String skinTone) {
        super(trainerBodyAtlas.findRegion(getBodyType(gender, skinTone)));
        initAnimations();
        isStarted = false;
        state = 0;
    }

    private static String getBodyType(String gender, String skinTone) {
        return "base-light";
    }

    public void start() {
        isStarted = true;
    }

    public int getState() {
        return state;
    }

    public void setState(int newState) {
        this.state = newState;
    }


    private void initAnimations() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 0), SpriteUtils.getSpriteY(this, 0), 175, 196));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 175), SpriteUtils.getSpriteY(this, 0), 175, 196));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 350), SpriteUtils.getSpriteY(this, 0), 175, 196));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 525), SpriteUtils.getSpriteY(this, 0), 175, 196));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 700), SpriteUtils.getSpriteY(this, 0), 175, 196));
        trainerAnimation = new Animation<TextureRegion>(frameDuration, frames);
        this.stateTimer = 0.0f;
        this.isFinishedAnimation = false;
    }

    public void update(float dt) {
        TextureRegion region;
        if (state != 5) {
            region = trainerAnimation.getKeyFrame(stateTimer, true);
            if (isStarted) {
                if (state == 0) {
                    setX(getX() - (dt * 550));
                    if (getX() <= stoppingPosition) {
                        setX(stoppingPosition);
                        state = 1;
                    }
                } else if (state == 1) {
                    //this.stateTimer += dt;
                } else if (state == 2) {
                    setX(getX() - (dt * 550));
                    if (getX() <= -350) {
                        setX(-350);
                    }
                    this.stateTimer += dt;
                } else if (state == 3) {

                }
            }
            setRegion(region);
        } else {
            this.isFinishedAnimation = true;
        }

    }

    public boolean isAnimationFinished() {
        return stateTimer >= 0.5;//trainerAnimation.isAnimationFinished(stateTimer);
    }

    public boolean isFinishedAnimation() {
        return isFinishedAnimation;
    }

    private float getAnimationTime() {
        return frameDuration * 5;
    }

}
