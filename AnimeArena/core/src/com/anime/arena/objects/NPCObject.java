package com.anime.arena.objects;

import com.anime.arena.AnimeArena;
import com.anime.arena.emojis.ExclamationEmoji;
import com.anime.arena.interactions.Event;
import com.anime.arena.screens.PlayScreen;
import com.anime.arena.tools.Movement;
import com.anime.arena.tools.MovementScript;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class NPCObject extends WorldObject {
    //Movement Script
    //Random movement with parameters
    //Static true false
    //Direction to start with
    protected Sprite npcSprite;
    protected Vector2 velocity;

    protected MovementScript movementScript;
    protected Direction direction;

    protected MovementScript eventMovement;
    protected boolean inMovementEvent;

    //Sprites
    protected TextureRegion standUp;
    protected TextureRegion standDown;
    protected TextureRegion standLeft;
    protected TextureRegion standRight;

    protected Animation<TextureRegion> walkingDown;
    protected Animation<TextureRegion> walkingUp;
    protected Animation<TextureRegion> walkingRight;
    protected Animation<TextureRegion> walkingLeft;

    //Sprite States
    public State currentState;
    public State previousState;
    protected float stateTimer;

    protected boolean interactingWithPlayer;

    protected float speed;

    protected Event event;

    protected ExclamationEmoji questAnimation;
    protected float emojiTimer;

    //A list of a list of switches that determines if the quest emoji is displayed
    // Ex [[0], [1,2], [3]] Emoji is displayed if the switch 0 is true, 1 and 2 is true, or 3 is true
    protected List<List<Integer>> displayQuestSwitches;

    private boolean drawEmoji;


    public NPCObject(int x, int y, PlayScreen screen, Sprite npcSprite, Event event, MovementScript movementScript, List<List<Integer>> npcEmojiSwitches) {
        super(x, y, screen);
        Gdx.app.log("x", x + "");
        this.event = event;
        this.velocity = new Vector2(0f, 0f);
        this.npcSprite = npcSprite;

       this.npcSprite.setBounds(x * 16 - 5, y * 16, 28 / AnimeArena.PPM, 32 / AnimeArena.PPM);

        //this.npcSprite.setSize(28, 32);
        this.movementScript = movementScript;//new MovementScript(movements, 2);
        this.direction = movementScript.getInitialDirection();
        speed = 60 * 1f;
        this.interactingWithPlayer = false;
        initAnimations();

        initStaticSprites();
        initSpriteStates();

        this.npcSprite.setRegion(standLeft);
        displayQuestSwitches = npcEmojiSwitches;

        questAnimation = new ExclamationEmoji(screen.getEmojiAtlas());
        emojiTimer = 0;
        drawEmoji = false;
        //TODO: In collision detection instead of having stoppingX / stoppingY, check the next movemeent. If different stop if not keep going
    }

    public void startMovementEvent(MovementScript eventMovement) {
        this.eventMovement = eventMovement;
        inMovementEvent = true;
    }

    public Event getInteractionEvent() {
        return event;
    }

    public void setMovementPath(Direction direction, int tiles) {
        List<Movement> newMovements = new ArrayList<>();
        Movement m;
        if (direction == Direction.DOWN) {
            m = Movement.MOVE_DOWN;
        } else if (direction == Direction.LEFT) {
            m = Movement.MOVE_LEFT;
        } else if (direction == Direction.RIGHT) {
            m = Movement.MOVE_RIGHT;
        } else {
            m = Movement.MOVE_UP;
        }
        for (int i = 0; i < tiles; i++) {
            newMovements.add(m);
        }
        this.eventMovement = new MovementScript(newMovements, 0);
    }

    public MovementScript getEventMovement() {
        return eventMovement;
    }

    public boolean isExecutingMovementEvent() {
        return inMovementEvent;
    }

    private void initSpriteStates() {
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
    }

    protected void initAnimations() {
        initWalkingDownAnimation();
        initWalkingRightAnimation();
        initWalkingLeftAnimation();
        initWalkingUpAnimation();
    }

    public Sprite getSprite() {
        return npcSprite;
    }

    private int getSpriteX(int spriteXOffset) {
        return npcSprite.getRegionX() + spriteXOffset;
    }
    private int getSpriteY(int spriteYOffset) {
        return npcSprite.getRegionY() + spriteYOffset;
    }

    protected void initStaticSprites() {
        standUp = new TextureRegion(npcSprite.getTexture(), getSpriteX(0), getSpriteY(192), 64, 64);
        standLeft = new TextureRegion(npcSprite.getTexture(), getSpriteX(0), getSpriteY(64), 64, 64);
        standRight = new TextureRegion(npcSprite.getTexture(), getSpriteX(0), getSpriteY(128), 64, 64);
        standDown = new TextureRegion(npcSprite.getTexture(), getSpriteX(0), getSpriteY(0), 64, 64);

    }

    protected void initWalkingDownAnimation() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(0), getSpriteY(0), 64, 64));
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(64), getSpriteY(0), 64, 64));
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(128), getSpriteY(0), 64, 64));
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(192), getSpriteY(0), 64, 64));

        walkingDown = new Animation<TextureRegion>(0.15f, frames);
    }

    protected void initWalkingRightAnimation() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(0), getSpriteY(128), 64, 64));
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(64), getSpriteY(128), 64, 64));
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(128), getSpriteY(128), 64, 64));
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(192), getSpriteY(128), 64, 64));
        walkingRight = new Animation<TextureRegion>(0.15f, frames);
    }

    protected void initWalkingLeftAnimation() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(0), getSpriteY(64), 64, 64));
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(64), getSpriteY(64), 64, 64));
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(128), getSpriteY(64), 64, 64));
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(192), getSpriteY(64), 64, 64));
        walkingLeft = new Animation<TextureRegion>(0.15f, frames);
    }

    protected void initWalkingUpAnimation() {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(0), getSpriteY(192), 64, 64));
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(64), getSpriteY(192), 64, 64));
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(128), getSpriteY(192), 64, 64));
        frames.add(new TextureRegion(npcSprite.getTexture(), getSpriteX(192), getSpriteY(192), 64, 64));
        walkingUp = new Animation<TextureRegion>(0.15f, frames);
    }

    public void update(float dt) {
        npcSprite.setRegion(getFrame(dt));
        if (!interactingWithPlayer) {
            emojiTimer += dt;
            if (emojiTimer >= 3) {
                emojiTimer = 0;
            }
            //npcSprite.setRegion(getFrame(dt));
            setVelocity();
            collisionDetection(dt);
        }
    }


    public void draw(Batch batch) {
        //Gdx.app.log("npcx: " , "" + npcSprite.getX());
        npcSprite.draw(batch);
        if (!interactingWithPlayer) {

        }
    }

    public void drawEmoji(Batch batch, PlayerSwitches playerSwitches) {
        if (!interactingWithPlayer) {
            if (canDisplayQuestAnimation(playerSwitches)) {
                questAnimation.draw(batch, emojiTimer, npcSprite.getX() + 6, npcSprite.getY() + 28);
            }
        }
    }

    private boolean canDisplayQuestAnimation(PlayerSwitches playerSwitches) {
        for (List<Integer> requiredSwitches : displayQuestSwitches) {
            int switchesOn = 0;
            for (Integer sw : requiredSwitches) {
                if (playerSwitches.isActive(sw)) {
                    switchesOn++;
                } else {
                    break;
                }
            }
            if (switchesOn == requiredSwitches.size()) {
                return true;
            }
        }
        return false;
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();
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

        //Increment stateTimer if the currentState is the previous state, otherwise reset it.
        stateTimer = currentState == previousState ? stateTimer + dt: 0;
        previousState = currentState;

        return region;
    }

    public State getState() {
        if (isMoving() && direction == Direction.UP) {
            return State.WALKING_UP;
        } else if (isMoving() && direction == Direction.DOWN) {
            return State.WALKING_DOWN;
        } else if (isMoving() && direction == Direction.RIGHT) {
            return State.WALKING_RIGHT;
        } else if (isMoving() && direction == Direction.LEFT) {
            return State.WALKING_LEFT;
        } else {
            return State.STANDING;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public Direction getOppositeDirection() {
        if (direction == Direction.DOWN) {
            return Direction.UP;
        } else if (direction == Direction.UP) {
            return Direction.DOWN;
        } else if (direction == Direction.LEFT) {
            return Direction.RIGHT;
        } else {
            return Direction.LEFT;
        }
    }

    public boolean isMoving() {
        //TODO: Add event movement to this as well
        if (movementScript.isDoneMoving() || interactingWithPlayer || (velocity.x == 0 && velocity.y == 0)) {
            return false;
        }
        return true;
    }

    public boolean isDoneEventMovement() {
        if (eventMovement != null) {
            return eventMovement.isDoneMoving() || eventMovement.getMovement() == null;
        }
        return true;
    }

    public void clearEventMovement() {
        eventMovement = null;
        inMovementEvent = false;
    }

    protected void setVelocity() {
        if (getMovementScript() != null) {
            if (getMovementScript().getMovement() == Movement.MOVE_LEFT) {
                velocity.y = 0;
                direction = Direction.LEFT;
                velocity.x = -speed;
            } else if (getMovementScript().getMovement() == Movement.MOVE_RIGHT) {
                velocity.y = 0;
                direction = Direction.RIGHT;
                velocity.x = speed;
            } else if (getMovementScript().getMovement() == Movement.MOVE_UP) {
                velocity.x = 0;
                direction = Direction.UP;
                velocity.y = speed;
            } else if (getMovementScript().getMovement() == Movement.MOVE_DOWN) {
                velocity.x = 0;
                direction = Direction.DOWN;
                velocity.y = -speed;
            } else {
                velocity.x = 0;
                velocity.y = 0;
            }
        } else {
            velocity.x = 0;
            velocity.y = 0;
        }
    }

    public boolean occupiesCell(int cellX, int cellY) {
        if (super.occupiesCell(cellX, cellY)) {
            return true;
        }
        if (velocity.x > 0) {
            if (x + 1 == cellX && y == cellY) {
                return true;
            }
        } else if (velocity.x < 0) {
            if (x - 1 == cellX && y == cellY) {
                return true;
            }
        } else if (velocity.y > 0) {
            if (x == cellX && y + 1 == cellY) {
                return true;
            }
        } else if (velocity.y < 0) {
            if (x == cellX && y - 1 == cellY) {
                return true;
            }
        }
        return false;
    }

    private MovementScript getMovementScript() {
        if (inMovementEvent) {
            return eventMovement;
        } else {
            return movementScript;
        }
    }

    private boolean collidesWithCell(TiledMapTileLayer.Cell cell) {
        if (inMovementEvent) {
            return false;
        }
        if (cell != null) {
            int tileType = ((int)cell.getTile().getProperties().get("tileType"));
            if (tileType == 8) {
                return true;
            } else if (tileType == 4 && velocity.y >= 0) {
                return true;
            } else if (tileType == 3 && velocity.x >= 0) {
                return true;
            } else if (tileType == 5 && velocity.x <= 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private int xFormula(int tile) {
        return -5 + (16 * tile);
    }

    private int yFormula(int tile) {
        return (16 * tile);
    }

    //Grid Detection getLeft
    private TiledMapTileLayer.Cell getLeftCell2() {
        return screen.getPokemonMap().getCollisionLayer() != null ? screen.getPokemonMap().getCollisionLayer().getCell(x - 1, y) : null;
    }

    //Grid Detection getRight
    private TiledMapTileLayer.Cell getRightCell2() {
        return screen.getPokemonMap().getCollisionLayer() != null ? screen.getPokemonMap().getCollisionLayer().getCell(x + 1, y) : null;
    }

    //Grid Detection getLeft
    private TiledMapTileLayer.Cell  getTopCell2() {
        return screen.getPokemonMap().getCollisionLayer() != null ? screen.getPokemonMap().getCollisionLayer().getCell(x, y + 1) : null;
    }

    //Grid Detection getRight
    private TiledMapTileLayer.Cell getBottomCell2() {
        return screen.getPokemonMap().getCollisionLayer() != null ? screen.getPokemonMap().getCollisionLayer().getCell(x, y - 1) : null;
    }

    private boolean collidesWithObject(int collisionX, int collisionY) {
         if (inMovementEvent) {
            return false;
        }
        if (screen.getPlayer().getVelocityX() == 0 && screen.getPlayer().getVelocityY() == 0 && screen.getPlayer().getYTile() == collisionY && screen.getPlayer().getXTile() == collisionX) {

            return true; // Collides with the player
        } else {
            if (screen.getPlayer().getVelocityX() > 0 && screen.getPlayer().getYTile() == collisionY
                    && screen.getPlayer().getXTile() == collisionX) {
                return true;
            } else if (screen.getPlayer().getVelocityX() < 0 && screen.getPlayer().getYTile() == collisionY
                    && screen.getPlayer().getXTile() == collisionX) {
                return true;
            } else if (screen.getPlayer().getVelocityY() > 0 && screen.getPlayer().getXTile() == collisionX
                    && screen.getPlayer().getYTile() == collisionY) {
                return true;
            } else if (screen.getPlayer().getVelocityY() < 0 && screen.getPlayer().getXTile() == collisionX
                    && screen.getPlayer().getYTile() == collisionY) {
                return true;
            }
        }
        return false;
    }

    //Grid Collision Detection
    protected void collisionDetection(float dt) {

        float oldX = npcSprite.getX();
        float oldY = npcSprite.getY();
        //Gdx.app.log("oldX", oldX + "");
        boolean collisionX = false;
        boolean collisionY = false;

        boolean getNextMovement = false;

        int tileHeight = 16;
        int tileWidth = 16;

        //Attempt to Move in the X axis
        npcSprite.setX(npcSprite.getX() + velocity.x * dt);

        //Check collision on the X axis
        if (velocity.x < 0) { //LEFT
            collisionX = collidesWithCell(getLeftCell2());
            if (!collisionX) {
                collisionX = collidesWithObject(x - 1, y);
            }

        } else if (velocity.x > 0) { //RIGHT
            collisionX = collidesWithCell(getRightCell2());
            if (!collisionX) {
                collisionX = collidesWithObject(x + 1, y);
            }
        }

        //Keep old position in the X axis if there is a collision
        if (collisionX) {
            npcSprite.setX(oldX);
            npcSprite.setX(xFormula(x));
            velocity.x = 0;
        } else if (velocity.x != 0) {

            if (velocity.x > 0 && npcSprite.getX() > xFormula(x + 1)) {
                x++;
                getNextMovement = true;
                if (!getMovementScript().hasNextMovement() || !getMovementScript().hasSameNextMovement()) {
                    npcSprite.setX(xFormula(x));
                    velocity.x = 0;
                }
                else if (collidesWithCell(getRightCell2())) {
                    npcSprite.setX(xFormula(x));
                    velocity.x = 0;
                }
            } else if (velocity.x < 0 && npcSprite.getX() < xFormula(x - 1)) {
                x--;
                getNextMovement = true;
                if (!getMovementScript().hasNextMovement() || !getMovementScript().hasSameNextMovement()) {
                    npcSprite.setX(xFormula(x));
                    velocity.x = 0;
                } else if (collidesWithCell(getLeftCell2())) {
                    npcSprite.setX(xFormula(x));
                    velocity.x = 0;
                }
            }
        } else {
            velocity.x = 0;
            npcSprite.setX(xFormula(x));
        }


        //Attempt to Move in the Y axis
        npcSprite.setY(npcSprite.getY() + velocity.y * dt);

        //Check collision on the Y axis
        if (velocity.y < 0) { //DOWN
            collisionY = collidesWithCell(getBottomCell2());
            if (!collisionY) {
                collisionY = collidesWithObject(x, y - 1);
            }
        } else if (velocity.y > 0) { //UP
            collisionY = collidesWithCell(getTopCell2());
            if (!collisionY) {
                collisionY = collidesWithObject(x, y + 1);
            }
        }

        //Keep old position in the Y axis if there is a collision
        if (collisionY) {
            npcSprite.setY(oldY);
            npcSprite.setY(yFormula(y));
            velocity.y = 0;
        } else if (velocity.y != 0) {
            if (velocity.y > 0 && npcSprite.getY() > yFormula(y + 1)) {
                y++;
                getNextMovement = true;
                if (!getMovementScript().hasNextMovement() || !getMovementScript().hasSameNextMovement()) {
                    npcSprite.setY(yFormula(y));
                    velocity.y = 0;
                } else if (collidesWithCell(getTopCell2())) {
                    npcSprite.setY(yFormula(y));
                    velocity.y = 0;
                }
            } else if (velocity.y < 0 && npcSprite.getY() < yFormula(y - 1)) {
                y--;
                getNextMovement = true;
                if (!getMovementScript().hasNextMovement() || !getMovementScript().hasSameNextMovement()) {
                    npcSprite.setY(yFormula(y));
                    velocity.y = 0;
                } else if (collidesWithCell(getBottomCell2())) {
                    npcSprite.setY(yFormula(y));
                    velocity.y = 0;
                }

            }
        } else {
            velocity.y = 0;
            npcSprite.setY(yFormula(y));
        }

        if (getNextMovement) {
            getMovementScript().setNextMovement();
            updateDirection();
        }

    }

    private void updateDirection() {
        if (!interactingWithPlayer) {
            if (movementScript.getMovement() == Movement.MOVE_UP || movementScript.getMovement() == Movement.LOOK_UP) {
                direction = Direction.UP;
            } else if (movementScript.getMovement() == Movement.MOVE_DOWN || movementScript.getMovement() == Movement.LOOK_DOWN) {
                direction = Direction.DOWN;
            } else if (movementScript.getMovement() == Movement.MOVE_LEFT || movementScript.getMovement() == Movement.LOOK_LEFT) {
                direction = Direction.LEFT;
            } else if (movementScript.getMovement() == Movement.MOVE_RIGHT || movementScript.getMovement() == Movement.LOOK_RIGHT) {
                direction = Direction.RIGHT;
            }
        }
    }

    public void stopInteractingWithPlayer() {
        interactingWithPlayer = false;
    }

    public void manualInteract(Player player, Direction newDirection) {
        if (isVisible()) {
            screen.playSelectSound();
            this.direction = newDirection;
            screen.setEvent(event);
            interactingWithPlayer = true;
        }
    }

    public float getVelocityX() { return velocity.x; }
    public float getVelocityY() { return velocity.y; }
}
