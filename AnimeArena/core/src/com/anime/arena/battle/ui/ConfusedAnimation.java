package com.anime.arena.battle.ui;

import com.anime.arena.battle.BattleState;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;

public class ConfusedAnimation implements UIComponent {

    private float stateTimer;
    private float elapsedTime;
    private TextureAtlas confusedAtlas;
    private Animation<TextureRegion> confusedAnimation;
    private Sprite confusedSprite;
    private BattleState currentState;

    public ConfusedAnimation(BattleState currentState) {
        this.currentState = currentState;
        this.confusedAtlas = new TextureAtlas("animation/battle/confused/Confused.atlas");
        this.elapsedTime = 0.0f;
        confusedSprite = new Sprite(confusedAtlas.findRegion("confused1"));
        confusedSprite.setX(120);
        confusedSprite.setY(1331);
        confusedSprite.setSize(464, 464);
        initAnimations();
    }

    private void initAnimations() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(confusedAtlas.findRegion("confused1"));
        frames.add(confusedAtlas.findRegion("confused2"));
        frames.add(confusedAtlas.findRegion("confused3"));
        frames.add(confusedAtlas.findRegion("confused4"));
        frames.add(confusedAtlas.findRegion("confused5"));
        frames.add(confusedAtlas.findRegion("confused6"));
        frames.add(confusedAtlas.findRegion("confused7"));
        frames.add(confusedAtlas.findRegion("confused8"));
        frames.add(confusedAtlas.findRegion("confused9"));
        frames.add(confusedAtlas.findRegion("confused10"));
        frames.add(confusedAtlas.findRegion("confused11"));
        confusedAnimation = new Animation<TextureRegion>(0.065f, frames);
        this.stateTimer = 0.0f;
    }

    public void update(float dt) {
        if (!isFinished()) {
            componentUpdate(dt);
        } else {
            returnToBattleState();
        }
    }

    public boolean isFinished() {
        return elapsedTime >= 2f;
    }



    @Override
    public void render(Batch batch) {
        confusedSprite.draw(batch);
    }

    @Override
    public void componentUpdate(float dt) {
        if (stateTimer >= confusedAnimation.getAnimationDuration()) {
            stateTimer = 0f;
        }
        TextureRegion region;
        region = confusedAnimation.getKeyFrame(stateTimer, true);
        confusedSprite.setRegion(region);
        stateTimer += dt;
        elapsedTime += dt;
    }


    @Override
    public void returnToBattleState() {
        currentState.removeUIComponent();
    }

    @Override
    public void click() {

    }

    @Override
    public void clickUp() {

    }

    @Override
    public void clickDown() {

    }
}
