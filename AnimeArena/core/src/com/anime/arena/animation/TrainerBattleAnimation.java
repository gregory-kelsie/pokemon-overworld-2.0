package com.anime.arena.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class TrainerBattleAnimation {

    private Texture white;
    private Texture transition;


    private BattleAnimationBackground battleAnimationBackground;
    private BattleAnimationBackground battleAnimationBackground2;

    private Texture player;
    private Texture opponent;

    private Texture vs;
    private int vsX;
    private int vsY;
    private float vsWidth;
    private float vsHeight;
    private float elapsedVs;


    private float playerX;
    private float opponentX;
    
    private String vertexShader;
    private String fragmentShader;
    private String whiteFragmentShader;
    private ShaderProgram shaderProgram;
    private ShaderProgram shaderProgram2;
    private ShaderProgram transitionProgram;
    private String transitionShader;

    private float uniformTime;
    private boolean showTrainer;

    private float hiddenTrainerElapsedTime;

    private int animationState;

    private final int START_STATE = 0; //When the backgrounds start to come colliding together
    private final int SCREEN_FLASH = 1; //After the backgrounds collides play a quick screen flash
    private final int TRAINER_STATE = 2; //Trainers come colliding together. Opponent has the black shader
    private final int SECOND_SCREEN_FLASH = 3; //Screen flash before removing the black shader and displaying the vs logo
    private final int FINAL_DISPLAY = 4; //Full display of the animation for a period of time
    private final int EXPAND_VS = 5; //Expand the vs texture before going to the battle screen

    public TrainerBattleAnimation() {
        white = new Texture("animation/white.png");
        transition = new Texture("animation/open2.png");
        player = new Texture("animation/faces/player.png");
        opponent = new Texture("animation/faces/jessejames.png");
        vs = new Texture("animation/faces/vs.png");
        battleAnimationBackground = new BattleAnimationBackground("animation/orangebg.png");
        battleAnimationBackground2 = new BattleAnimationBackground("animation/blackbg.png", 1080, 1300);

        vsX = 400;
        vsY = 1140;
        elapsedVs = 0;
        vsWidth = 70 * 4.21875f;
        vsHeight = 70 * 4.21875f;
        
        vertexShader = Gdx.files.internal("shaders/default.vs").readString();
        fragmentShader = Gdx.files.internal("shaders/black.fs").readString();
        whiteFragmentShader = Gdx.files.internal("shaders/white.fs").readString();
        transitionShader = Gdx.files.internal("shaders/transition.fs").readString();
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        shaderProgram2 = new ShaderProgram(vertexShader, whiteFragmentShader);
        transitionProgram = new ShaderProgram(vertexShader, transitionShader);
        uniformTime = 0;
        showTrainer = false;
        if (!shaderProgram.isCompiled()) {
            Gdx.app.log("Error", "Shader Program failed to compile: " + shaderProgram.getLog());
        }

        animationState = START_STATE;

        //Start offscreen on the left and right
        playerX = Math.round(-128 * 4.21875f);
        opponentX = 1080;

        hiddenTrainerElapsedTime = 0;
    }
    
    public void update(float dt) {
        uniformTime -= dt;
        if (uniformTime <= 0) {
            uniformTime = 0;
            if (animationState == SCREEN_FLASH) {
                animationState = TRAINER_STATE;
            } else if (animationState == SECOND_SCREEN_FLASH) {
                animationState = FINAL_DISPLAY;
            }
        }
        if (animationState == START_STATE) {
            battleAnimationBackground.setX(Math.round(battleAnimationBackground.getX() + dt * 1600));
            battleAnimationBackground2.setX(Math.round(battleAnimationBackground2.getX() - dt * 1600));
            if (battleAnimationBackground.getX() >= 0 && battleAnimationBackground2.getX() <= 540) {
                battleAnimationBackground.setX(BattleAnimationBackground.FINAL_LEFT_X_POSITION);
                battleAnimationBackground2.setX(540);
                animationState = SCREEN_FLASH;
                uniformTime = 1;
                battleAnimationBackground.setAnimateBackground(true);
                battleAnimationBackground2.setAnimateBackground(true);
            }
        } else if (animationState == TRAINER_STATE) {
            playerX = (Math.round(playerX + dt * 2900));
            opponentX = (Math.round(opponentX - dt * 2900));
            if (playerX >= 0 && opponentX <= 540) {
                playerX = 0;
                opponentX = 540;
                hiddenTrainerElapsedTime += dt;
                if (hiddenTrainerElapsedTime >= 1.5) {
                    hiddenTrainerElapsedTime = 0;
                    animationState = SECOND_SCREEN_FLASH;
                    uniformTime = 1;
                    showTrainer = true;
                }
            }
        } else if (animationState == FINAL_DISPLAY) {
            hiddenTrainerElapsedTime += dt;
            if (hiddenTrainerElapsedTime >= 1.25) {
                hiddenTrainerElapsedTime = 0;
                //animationState = EXPAND_VS;
            }
        }
        battleAnimationBackground.update(dt);
        battleAnimationBackground2.update(dt);
        elapsedVs += dt;
        if (animationState != EXPAND_VS) {
            if (elapsedVs >= 0.05) {
                if (vsX == 400) {
                    vsX = 395;
                    vsY = 1135;
                } else {
                    vsX = 400;
                    vsY = 1140;
                }
                elapsedVs = 0;
            }
        }
        if (animationState == EXPAND_VS) { //Increase Size
            vsHeight += dt * 800;
            vsWidth += dt * 800;
            vsX -= dt * 420;
            vsY -= dt * 200;
        }
    }

    public void render(float delta, SpriteBatch batch) {
        battleAnimationBackground.render(delta, batch);
        battleAnimationBackground2.render(delta, batch);
        batch.draw(player, playerX, 1300, 128 * 4.21875f, 64 * 4.21875f);
        if (shaderProgram.isCompiled()) {
            if (!showTrainer) {
               batch.setShader(shaderProgram);
            }
        }
        //shaderProgram.setUniformf("u_time", uniformTime);
        batch.draw(opponent, opponentX, 1300, 128 * 4.21875f, 64 * 4.21875f);
        batch.setShader(null);
        if (showTrainer) {
            batch.draw(vs, vsX, vsY, vsWidth, vsHeight);
        }

        if (animationState == SCREEN_FLASH || animationState == SECOND_SCREEN_FLASH) {
            batch.setShader(shaderProgram2);
            shaderProgram2.setUniformf("u_time", uniformTime);
            batch.draw(white, 0, 960);
        }
        batch.setShader(null);       
    }
}
