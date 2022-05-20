package com.anime.arena.objects;

import com.anime.arena.tools.SpriteUtils;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;

public class PlayerBody extends Sprite {
    private TextureRegion standUp;
    private TextureRegion standDown;
    private TextureRegion standLeft;
    private TextureRegion standRight;
    private TextureRegion jumpingDown;

    private Animation<TextureRegion> walkingDown;
    private Animation<TextureRegion> walkingUp;
    private Animation<TextureRegion> walkingRight;
    private Animation<TextureRegion> walkingLeft;

    public PlayerBody(TextureAtlas playerBodiesAtlas, String bodyType) {
        super(playerBodiesAtlas.findRegion(bodyType));
        initStaticSprites();
        initAnimations();
    }

    public void setFrame(State currentState, Direction direction, float stateTimer) {
        TextureRegion region;
        switch(currentState) {
            case WALKING_DOWN:
                region = walkingDown.getKeyFrame(stateTimer, true);
                break;
            case WALKING_UP:
                region = walkingUp.getKeyFrame(stateTimer, true);
                break;
            case WALKING_RIGHT:
                region = walkingRight.getKeyFrame(stateTimer, true);
                break;
            case WALKING_LEFT:
                region = walkingLeft.getKeyFrame(stateTimer, true);
                break;
            case JUMPING_DOWN:
                region = jumpingDown;
                break;
            case STANDING:
            default:
                if (direction == Direction.RIGHT) {
                    region = standRight;
                } else if (direction == Direction.LEFT) {
                    region = standLeft;
                } else if (direction == Direction.UP) {
                    region = standUp;
                } else {
                    region = standDown;
                }
                break;
        }
        setRegion(region);
    }

    private void initAnimations() {
        initWalkingDownAnimation();
        initWalkingRightAnimation();
        initWalkingLeftAnimation();
        initWalkingUpAnimation();
    }

    private void initWalkingDownAnimation() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 0), SpriteUtils.getSpriteY(this, 0), 64, 64));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 64), SpriteUtils.getSpriteY(this, 0), 64, 64));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 128), SpriteUtils.getSpriteY(this, 0), 64, 64));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 192), SpriteUtils.getSpriteY(this, 0), 64, 64));
        walkingDown = new Animation<TextureRegion>(0.15f, frames);
    }

    private void initWalkingRightAnimation() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 0), SpriteUtils.getSpriteY(this, 128), 64, 64));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 64), SpriteUtils.getSpriteY(this, 128), 64, 64));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 128), SpriteUtils.getSpriteY(this, 128), 64, 64));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 192), SpriteUtils.getSpriteY(this, 128), 64, 64));
        walkingRight = new Animation<TextureRegion>(0.15f, frames);
    }

    private void initWalkingLeftAnimation() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 0), SpriteUtils.getSpriteY(this, 64), 64, 64));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 64), SpriteUtils.getSpriteY(this, 64), 64, 64));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 128), SpriteUtils.getSpriteY(this, 64), 64, 64));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 192), SpriteUtils.getSpriteY(this, 64), 64, 64));
        walkingLeft = new Animation<TextureRegion>(0.15f, frames);
    }

    private void initWalkingUpAnimation() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 0), SpriteUtils.getSpriteY(this, 192), 64, 64));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 64), SpriteUtils.getSpriteY(this, 192), 64, 64));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 128), SpriteUtils.getSpriteY(this, 192), 64, 64));
        frames.add(new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 192), SpriteUtils.getSpriteY(this, 192), 64, 64));
        walkingUp = new Animation<TextureRegion>(0.15f, frames);
    }

    private void initStaticSprites() {
        jumpingDown = new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 0), SpriteUtils.getSpriteY(this, 65), 64, 64);
        standUp = new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 0), SpriteUtils.getSpriteY(this, 192), 64, 64);
        standLeft = new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 0), SpriteUtils.getSpriteY(this, 64), 64, 64);
        standRight = new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 0), SpriteUtils.getSpriteY(this, 128), 64, 64);
        standDown = new TextureRegion(getTexture(), SpriteUtils.getSpriteX(this, 0), SpriteUtils.getSpriteY(this, 0), 64, 64);
    }


}
