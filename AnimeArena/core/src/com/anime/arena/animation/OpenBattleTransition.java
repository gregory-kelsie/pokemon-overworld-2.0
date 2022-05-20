package com.anime.arena.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;


public class OpenBattleTransition {
    private String vertexShader;
    private String transitionShader;
    private ShaderProgram transitionProgram;
    private Texture transitionTexture;
    private float currentTransitionTime;
    private float totalTransitionTime;
    public OpenBattleTransition() {
        this.vertexShader = Gdx.files.internal("shaders/default.vs").readString();
        this.transitionShader = Gdx.files.internal("shaders/opentrans.fs").readString();
        this.transitionProgram = new ShaderProgram(vertexShader, transitionShader);
        if (!transitionProgram.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + transitionProgram.getLog());
        this.transitionTexture = new Texture("animation/open.png");
        this.currentTransitionTime = 0;
        this.totalTransitionTime = 1.5f;
    }

    public boolean isFinished() {
        return currentTransitionTime == totalTransitionTime;
    }

    public void update(float dt) {
        currentTransitionTime = Math.min(totalTransitionTime, currentTransitionTime + dt);
    }

    public void render(SpriteBatch batch) {
        batch.setShader(transitionProgram);
        float displayedPixel =  1.0f - ((float)(currentTransitionTime / totalTransitionTime));
        transitionProgram.setUniformf("u_time", displayedPixel);
        batch.draw(transitionTexture, 0, 960);
        batch.setShader(null);
    }

    public void dispose() {
        transitionProgram.dispose();
        transitionTexture.dispose();;
    }
}
