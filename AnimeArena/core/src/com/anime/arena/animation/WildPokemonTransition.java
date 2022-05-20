package com.anime.arena.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class WildPokemonTransition {
    private String vertexShader;
    private String fragmentShader;
    private ShaderProgram flashShader;
    private String transitionShader;
    private ShaderProgram transitionProgram;
    private Texture whiteBackground;
    private Texture transitionTexture;
    private Music wildBgm;

    private float flashDuration;
    private float currentFlashTime;
    private int state;
    private final int START_FLASH = 0;
    private final int START_ANIMATION = 1;
    private final int DONE_ANIMATION = 2;
    private final int TOTAL_FLASHES = 3;

    private double currentTransitionTime;
    private final double totalTransitionTime = 1.5f;
    private int flashCount;
    public WildPokemonTransition() {
        vertexShader = Gdx.files.internal("shaders/default.vs").readString();
        fragmentShader = Gdx.files.internal("shaders/white.fs").readString();
        transitionShader = Gdx.files.internal("shaders/workingtrans.fs").readString();
        flashShader = new ShaderProgram(vertexShader, fragmentShader);
        transitionProgram = new ShaderProgram(vertexShader, transitionShader);
        if (!transitionProgram.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + transitionProgram.getLog());
        whiteBackground = new Texture("animation/white.png");
        transitionTexture = new Texture("animation/checkers.png");
        flashDuration = 0.33f;
        currentFlashTime = 0.0f;
        state = START_FLASH;
        flashCount = 0;
        currentTransitionTime = 0.0f;
        wildBgm = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/battle/wildpokemon.ogg"));
        //wildBgm.play();
    }

    public boolean isFinished() {
        if (state == DONE_ANIMATION) {
            return true;
        }
        return false;
    }

    private float getElapsedTimePercentage() {
        return currentFlashTime / flashDuration;
    }

    public void update(float dt) {
        if (flashCount == TOTAL_FLASHES) {
            state = START_ANIMATION;
        }
        if (currentFlashTime == flashDuration) {
            currentFlashTime = 0.0f;
        }
        if (state == START_FLASH) {
            currentFlashTime = Math.min(flashDuration, currentFlashTime + dt);
            if (currentFlashTime == flashDuration) {
                flashCount++;
            }
        } else if (state == START_ANIMATION) {
            if (totalTransitionTime == currentTransitionTime) {
                state = DONE_ANIMATION;
            } else {
                currentTransitionTime = Math.min(totalTransitionTime, currentTransitionTime + dt);
            }

        }
    }

    public void render(SpriteBatch batch) {
        if (state == START_FLASH) {
            batch.setShader(flashShader);
            flashShader.setUniformf("u_time", getElapsedTimePercentage());
            batch.draw(whiteBackground, 0, 960);
            batch.setShader(null);
        } else if (state == START_ANIMATION || state == DONE_ANIMATION) {
            batch.setShader(transitionProgram);
            //The gradient displays black first and then white last. (From the RGB, 0,0,0 to 255,255,255
            float displayedColour =  (float)(currentTransitionTime / totalTransitionTime);
            transitionProgram.setUniformf("u_time", displayedColour);
            batch.draw(transitionTexture, 0, 960);
            batch.setShader(null);
        }
    }


}
