package com.anime.arena.battle.ui;

import com.anime.arena.tools.SpriteUtils;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class SendOutPokeball extends Sprite {
    private float stateTimer;
    private TextureAtlas pokeballAtlas;
    private Animation<TextureRegion> pokeballAnimation;
//    private static final int AMPLITUDE = 3;
//    private static final int VERTICAL_SHIFT = 1200;
//    private static final double PERIOD = 0.0027;
//    private static final int BALL_SPEED = 400;
//    private static final int LANDING_DISTANCE = 700;
    private static final int AMPLITUDE = 300;
    private static final int VERTICAL_SHIFT = 1200;
    private static final double PERIOD = 0.008;
    private static final int BALL_SPEED = 300;
    private static final int LANDING_DISTANCE = 300;


    public SendOutPokeball(TextureAtlas pokeballAtlas) {
        super(pokeballAtlas.findRegion("01"));
        this.pokeballAtlas = pokeballAtlas;
        initAnimations();
    }

    private void initAnimations() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(pokeballAtlas.findRegion("01"));
        frames.add(pokeballAtlas.findRegion("02"));
        frames.add(pokeballAtlas.findRegion("03"));
        frames.add(pokeballAtlas.findRegion("04"));
        frames.add(pokeballAtlas.findRegion("05"));
        frames.add(pokeballAtlas.findRegion("06"));
        frames.add(pokeballAtlas.findRegion("07"));
        frames.add(pokeballAtlas.findRegion("08"));
        pokeballAnimation = new Animation<TextureRegion>(0.065f, frames);
        this.stateTimer = 0.0f;
    }

    public void update(float dt) {
        if (stateTimer >= pokeballAnimation.getAnimationDuration()) {
            stateTimer = 0f;
        }
        TextureRegion region;
        region = pokeballAnimation.getKeyFrame(stateTimer, true);
        setRegion(region);
        stateTimer += dt;
        updatePosition(dt);
    }

    public boolean isFinished() {
        return getX() >= LANDING_DISTANCE;
    }

    private void updatePosition(float dt) {
        if (getX() <= LANDING_DISTANCE) {
            setX(getX() + dt * BALL_SPEED);
            double currentX = Double.valueOf(Float.valueOf(getX()).toString());
            float newY = Float.valueOf(Double.toString(AMPLITUDE * Math.sin(PERIOD * currentX))) + VERTICAL_SHIFT;
            setY(newY);
        }
    }
}
