package com.anime.arena.battle.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class TossPokeball extends Sprite {
    private float stateTimer;
    private TextureAtlas pokeballAtlas;
    private Animation<TextureRegion> pokeballAnimation;
    private Animation<TextureRegion> shakingPokeballAnimation;
    private static final double AMPLITUDE = 3.0;
    private static final double VERTICAL_SHIFT = 1200;
    private static final double PERIOD = 0.0027;
    private static final float BALL_SPEED = 750;
    private static final int FALL_SPEED = 400;
    private static final double LANDING_DISTANCE = 700;
    private static final int FALL_LOCATION_Y = 1620;
    private static double SECONDS_BALL_OPEN = 0.6;
    private int state;
    private float openBallTimer;

    private float shakingBallPauseDuration;
    private int totalNumberOfShakes;
    private int shakeCount;
    private boolean isShaking;

    public TossPokeball(TextureAtlas pokeballAtlas) {
        super(pokeballAtlas.findRegion("01"));
        this.pokeballAtlas = pokeballAtlas;
        this.state = 0;
        this.openBallTimer = 0.0f;
        this.totalNumberOfShakes = 2;
        if (totalNumberOfShakes == 1) {
            shakingBallPauseDuration = 0.5f;
        } else {
            shakingBallPauseDuration = 1.3f;
        }
        this.shakeCount = 0;
        this.isShaking = false;
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

        Array<TextureRegion> shakingPokeballFrames = new Array<TextureRegion>();
        shakingPokeballFrames.add(pokeballAtlas.findRegion("18"));
        shakingPokeballFrames.add(pokeballAtlas.findRegion("19"));
        shakingPokeballFrames.add(pokeballAtlas.findRegion("18"));
        shakingPokeballFrames.add(pokeballAtlas.findRegion("17"));
        shakingPokeballFrames.add(pokeballAtlas.findRegion("18"));
        shakingPokeballAnimation = new Animation<TextureRegion>(0.1f, shakingPokeballFrames);
    }


    public void update(float dt) {
        if (state == 0) {
            updateBallThrowAnimation(dt);
            if (getX() >= LANDING_DISTANCE) {
                state = 1;
                setRegion(pokeballAtlas.findRegion("10"));
            }
        } else if (state == 1) { // Pokeball opening
            openBallTimer += dt;
            if (openBallTimer >= SECONDS_BALL_OPEN) {
                state = 2;
                setRegion(pokeballAtlas.findRegion("11"));
            }
        } else if (state == 2) { //Pokeball falling into place
            updateFallingPokeball(dt);
            if (getY() <= FALL_LOCATION_Y) {
                state = 3;
                stateTimer = 0.0f;
                isShaking = true;
            }
        } else if (state == 3) {
            if (isShaking) {
                updateShakingBallAnimation(dt);
                if (stateTimer >= shakingPokeballAnimation.getAnimationDuration()) {
                    stateTimer = 0.0f;
                    isShaking = false;
                    shakeCount++;
                }
            } else {
                updateShakingBallPause(dt);
            }
        } else if (state == 4) {
            stateTimer += dt;
            if (stateTimer >= 1.5f) {
                state = 5;
            }
        }
    }

    public boolean isDisplayingEnemyPokemon() {
        return state < 2;
    }

    public boolean isFinished() {
        return state == 5;
    }

    private void updateBallThrowAnimation(float dt) {
        if (stateTimer >= pokeballAnimation.getAnimationDuration()) {
            stateTimer = 0f;
        }
        TextureRegion region;
        region = pokeballAnimation.getKeyFrame(stateTimer, true);
        setRegion(region);
        stateTimer += dt;
        updatePosition(dt);
    }

    private void updateShakingBallAnimation(float dt) {
        TextureRegion region;
        region = shakingPokeballAnimation.getKeyFrame(stateTimer, false);
        setRegion(region);
        stateTimer += dt;
    }

    private void updateShakingBallPause(float dt) {
        stateTimer += dt;
        if (stateTimer >= shakingBallPauseDuration) {
            stateTimer = 0.0f;
            if (shakeCount >= 3) {
                //Display Locked Pokeball
                setRegion(pokeballAtlas.findRegion("21"));
                state = 4;
                stateTimer = 0.0f;
            } else if (shakeCount < totalNumberOfShakes) {
                isShaking = true;
            } else {
                setRegion(pokeballAtlas.findRegion("10"));
                state = 4;
                stateTimer = 0.0f;
            }
        }
    }

    public boolean hasCaughtPokemon() {
        return shakeCount >= 3;
    }

    private void updateFallingPokeball(float dt) {
        float newY = getY() - dt * FALL_SPEED;
        setY(newY);
    }

    private void updatePosition(float dt) {
        if (getX() <= LANDING_DISTANCE) {
            setX(getX() + dt * BALL_SPEED);
            double currentX = Double.valueOf(Float.valueOf(getX()).toString());
            float newY = Float.valueOf(Double.toString(600 * Math.sin(0.0027 * currentX))) + 1200;
            Gdx.app.log("y", newY + "");
            setY(newY);
        }
    }
}
