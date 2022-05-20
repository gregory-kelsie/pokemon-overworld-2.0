package com.anime.arena.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class FadeOutAnimation {
    private Texture black;
    private ShaderProgram shaderProgram;
    private float elapsedTime;
    private float totalTime;
    private boolean start;
    public FadeOutAnimation(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        black = new Texture("animation/black.png");
        elapsedTime = 0;
        totalTime = 0.5f;
        this.start = false;
    }

    public void startAnimation() {
        this.start = true;
    }

    public void stopAnimation() {
        this.start = false;
        resetAnimation();
    }

    public void resetAnimation() {
        elapsedTime = 0;
    }

    public boolean isFinished() {
        return elapsedTime == totalTime;
    }

    public void update(float dt) {
        if (start) {
            if (elapsedTime < totalTime) {
                elapsedTime += dt;
            } else {
                elapsedTime = totalTime;
            }
        }
    }

    public void render(float dt, SpriteBatch batch) {
        if (start) {
            batch.setShader(shaderProgram);
            shaderProgram.setUniformf("u_time", elapsedTime / totalTime);
            batch.draw(black, 0, 960);
            batch.setShader(null);
        }
    }
}
