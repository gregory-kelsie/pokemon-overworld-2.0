package com.anime.arena.objects;

import com.anime.arena.AnimeArena;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;

public class CustomPlayer {

    private PlayerOutfit outfit;
    private PlayerBody body;
    private PlayerBody hair;
    private PlayerBody top;
    private PlayerBody bottom;
    private PlayerBody bag;

    private PlayerBody swimmingBody;

    public State currentState;
    public State previousState;
    private float stateTimer;

    private Direction direction;
    private boolean isMoving;
    private boolean isSwimming;

    private Vector2 velocity;
    private float speed = 60 * 1f;

    private Texture shadow;
    public CustomPlayer(PlayerOutfit outfit) {
        this.outfit = outfit;
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        isMoving = true;
        isSwimming = true;
        direction = Direction.DOWN;
        this.velocity = new Vector2(0f, 0f);
        initSpritePosition(0f, 0f);
        shadow = new Texture("sprites/shadow3.png");

    }

    public PlayerOutfit getOutfit() {
        return outfit;
    }

    public void setBody(PlayerBody body) {
        this.body = body;
    }

    public void setSwimmingBody(PlayerBody swimmingBody) {
        this.swimmingBody = swimmingBody;
    }

    public void setHair(PlayerBody hair) {
        this.hair = hair;
    }

    public void setTop(PlayerBody top) {
        this.top = top;
    }

    public void setBottom(PlayerBody bottom) {
        this.bottom = bottom;
    }

    public void setBag(PlayerBody bag) {
        this.bag = bag;
    }

    public State getState() {
        if (isMoving && direction == Direction.UP) {
            return State.WALKING_UP;
        } else if (isMoving && direction == Direction.DOWN) {
            return State.WALKING_DOWN;
        } else if (isMoving && direction == Direction.RIGHT) {
            return State.WALKING_RIGHT;
        } else if (isMoving && direction == Direction.LEFT) {
            return State.WALKING_LEFT;
        } else {
            return State.STANDING;
        }
    }

    private void collisionDetection(float dt) {

    }

    public void update(float dt) {
        updateSprite(dt);
        collisionDetection(dt);
        updateSpritePosition();
    }

    private void updateSprite(float dt) {
        currentState = getState();
        if (!isSwimming) {
            body.setFrame(currentState, direction, stateTimer);
            if (hair != null) {
                hair.setFrame(currentState, direction, stateTimer);
            }
            if (top != null) {
                top.setFrame(currentState, direction, stateTimer);
            }
            if (bottom != null) {
                bottom.setFrame(currentState, direction, stateTimer);
            }
            if (bag != null) {
                bag.setFrame(currentState, direction, stateTimer);
            }
        } else {
            swimmingBody.setFrame(currentState, direction, stateTimer);
        }
        updateStateTimer(dt);
    }

    private void updateStateTimer(float dt) {
        //Increment stateTimer if the currentState is the previous state, otherwise reset it.
        stateTimer = currentState == previousState ? stateTimer + dt: 0;
        previousState = currentState;
    }

    public void initSpritePosition(float x, float y) {
        if (body != null) {
            body.setSize(28, 32);
            body.setX(x);
            body.setY(y);
            swimmingBody.setSize(28, 32);
            swimmingBody.setX(x);
            swimmingBody.setY(y);
            //body.setBounds(x, y, 32 / AnimeArena.PPM, 32 / AnimeArena.PPM);
        }
        if (hair != null) {
            hair.setBounds(x, y, 28 / AnimeArena.PPM, 32 / AnimeArena.PPM);
        }
        if (top != null) {
            top.setBounds(x, y, 28 / AnimeArena.PPM, 32 / AnimeArena.PPM);
        }
        if (bottom != null) {
            bottom.setBounds(x, y, 28 / AnimeArena.PPM, 32 / AnimeArena.PPM);
        }
        if (bag != null) {
            bag.setBounds(x, y, 28 / AnimeArena.PPM, 32 / AnimeArena.PPM);
        }
    }

    public void updateSpritePosition() {
        //Update each sprite part based on the body position
        if (hair != null) {
            hair.setX(body.getX());
        }
        if (top != null) {
            top.setX(body.getX());
        }
        if (bottom != null) {
            bottom.setX(body.getX());
        }
        if (bag != null) {
            bag.setX(body.getX());
        }
        if (swimmingBody != null) {
            swimmingBody.setX(body.getX());
        }
    }


    private void drawShadow(Batch spriteBatch) {
        spriteBatch.draw(shadow, body.getX() + 8, body.getY() - 1);
    }

    public void draw(Batch batch) {
        if (!isSwimming) {
            drawShadow(batch);
            if (body != null) {
                body.draw(batch);
            }
            if (bottom != null) {
                bottom.draw(batch);
            }
            if (top != null) {
                top.draw(batch);
            }
            if (bag != null) {
                bag.draw(batch);
            }
            if (hair != null) {
                hair.draw(batch);
            }
        } else {
            if (swimmingBody != null) {
                swimmingBody.draw(batch);
            }
        }
    }

}
