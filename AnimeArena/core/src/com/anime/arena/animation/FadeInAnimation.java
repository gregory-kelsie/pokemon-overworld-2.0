package com.anime.arena.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class FadeInAnimation {
    private Texture black;
    private ShaderProgram shaderProgram;
    private float elapsedTime;
    private float totalTime;
    private boolean start;
    public FadeInAnimation(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        black = new Texture("animation/black.png");
        totalTime = 0.5f;
        elapsedTime = totalTime;
        this.start = false;
    }

    public void startAnimation() {
        resetAnimation();
        this.start = true;
    }

    public void stopAnimation() {
        this.start = false;
        resetAnimation();
    }

    private void resetAnimation() {
        elapsedTime = totalTime;
    }

    public void update(float dt) {
        if (start) {
            Gdx.app.log("elapsedTime", "" + dt);
            if (elapsedTime > 0) {
                elapsedTime -= dt;
            } else {
                elapsedTime = 0;
            }
        }
    }

    public boolean isFinished() {
        return elapsedTime == 0;
    }

    /**
     * Return the actual elapsed time for the transition.
     * elapsedTime is just a time counter subtracted from the totalTime
     * @return
     */
    private float getActualElapsedTime() {
        return totalTime - elapsedTime;
    }

    private float getElapsedTimePercentage() {
        return getActualElapsedTime() / totalTime;
    }

    public void render(float dt, SpriteBatch batch) {
        if (start) {
            batch.setShader(shaderProgram);
            shaderProgram.setUniformf("u_time", 1 - getElapsedTimePercentage());
            batch.draw(black, 0, 960);
            batch.setShader(null);
        }
    }
}
