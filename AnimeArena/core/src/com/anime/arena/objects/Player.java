package com.anime.arena.objects;

import com.anime.arena.AnimeArena;
import com.anime.arena.dto.PlayerProfile;
import com.anime.arena.interactions.EncounterEvent;
import com.anime.arena.interactions.WarpEvent;
import com.anime.arena.items.Bag;
import com.anime.arena.items.Clothing;
import com.anime.arena.pokedex.Pokedex;
import com.anime.arena.pokemon.Pokemon;
import com.anime.arena.quest.Quest;
import com.anime.arena.screens.PlayScreen;
import com.anime.arena.tools.Movement;
import com.anime.arena.tools.MovementScript;
import com.anime.arena.tools.PokemonMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gregorykelsie on 2018-07-32.
 */

public class Player {

    public State currentState;
    public State previousState;
    private float stateTimer;

    private Direction direction;
    
    private PlayScreen screen;

    private PlayerOutfit outfit;

    private PlayerProfile playerProfile;

    //Sprites
    private PlayerBody body;
    private PlayerBody hair;
    private PlayerBody top;
    private PlayerBody bottom;
    private PlayerBody bag;
    private PlayerBody swimmingBody;



    private Texture shadow;

    private boolean isMoving;
    private int jumpState;
    private float initialJumpY;
    private float initialJumpX;

    private Vector2 velocity;
    private float moveButtonDownTime;
    private float speed = 60 * 1f;
    private boolean running = false;
    private OrthographicCamera gameCam;
    private final float SCALE = 1f;

    //Stop Variables
    private boolean isStopping; //Released move button but not in the middle of a tile yet
    private float stoppedX;
    private float stoppedY;


    //Tile Variables
    private int yTile;
    private int xTile;

    private int stoppingX;
    private int stoppingY;

    //Jumping Variables
    private boolean jumping;
    private boolean jumpingLeft;
    private boolean jumpingRight;
    private float jumpUpSpeed;
    private float fallingSpeed;
    private float swimSpeed;
    private float maxJumpHeight;
    private boolean reachedMaxHeight;
    private float jumpLeftSpeed;
    private float jumpRightSpeed;

    //Swimming Variables
    private boolean swimmingUp;
    private boolean swimmingDown;
    private boolean swimmingLeft;
    private boolean swimmingRight;

    //Sliding Variables
    private boolean sliding = false;
    private float slideSpeed;

    //Swimming Variables
    private boolean swimming;

    //Event Variables
    private boolean inEvent;
    private MovementScript eventMovement;
    private boolean getNextMovement;

    private String mapName; //The map the player is on
    private PokemonMap pokemonMap;

    private NPCObject interactingNPC;

    private PlayerSwitches switches;
    private List<Quest> quests;

    private Pokedex pokedex;
    private List<Pokemon> pokemonParty;

    private Bag inventory;




    public Player(PlayScreen screen, PlayerProfile playerProfile, PokemonMap map, OrthographicCamera gameCam) {
        this.playerProfile = playerProfile;
        this.screen = screen;
        initOutfit();
        this.pokemonMap = map;
        moveButtonDownTime = 0;
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        isMoving = true;
        running = false;
        direction = Direction.DOWN;
        velocity = new Vector2(0f, 0f);
        this.gameCam = gameCam;
        shadow = new Texture("sprites/shadow3.png");


        initSpritePositions(0f, 0f);
        jumpState = 0;

        yTile = 0;
        xTile = 0;

        stoppingX = -100;
        stoppingY = -100;



        //Jumping Variables
        jumping = false;
        jumpingLeft = false;
        jumpingRight = false;
        jumpUpSpeed = 60 * 2.0f;
        jumpLeftSpeed = -60 * 3.0f;
        jumpRightSpeed = 60 * 3.0f;
        fallingSpeed = -60 * 3.0f;
        swimSpeed = -30 * 3.0f;
        maxJumpHeight = 0;
        reachedMaxHeight = false;

        //Sliding Variables
        sliding = false;
        slideSpeed = 60 * 2.25f;

        //Swimming Variables
        swimming = false;
        swimmingUp = false;
        swimmingDown = false;
        swimmingLeft = false;
        swimmingRight = false;

        //Event Variables
        inEvent = false;
        getNextMovement = false;

        //Refactor Variables - NEed to reorganize
        this.mapName = "maps/3.0.tmx";

        this.switches = new PlayerSwitches();
        this.quests = new ArrayList<Quest>();
        pokedex = new Pokedex(screen.getBasePokemonFactory());

        this.pokemonParty = new ArrayList<Pokemon>();
        this.inventory = new Bag();
    }

    public Bag getBag() {
        return inventory;
    }

    public List<Pokemon> getPokemonParty() {
        return pokemonParty;
    }

    public Pokedex getPokedex() {
        return pokedex;
    }

    public void startMovementEvent(MovementScript eventMovement) {
        this.eventMovement = eventMovement;
    }

    public boolean isInMovementEvent() {
        if (eventMovement != null) {
            return true;
        }
        return false;
    }

    public float getY2() {
        return body.getY();
    }

    public float getX2() {
        return body.getX();
    }

    public float getX() {
        return getX2();
    }

    public float getY() {
        return getY2();
    }

    public void setX(float x) {
        setX2(x);
    }

    public void setY(float y) {
        setY2(y);
    }

    public void setY2(float y) {
        if (body != null) {
            body.setY(y);
            swimmingBody.setY(y);
        }
        if (hair != null) {
            hair.setY(y);
        }
        if (top != null) {
            top.setY(y);
        }
        if (bottom != null) {
            bottom.setY(y);
        }
        if (bag != null) {
            bag.setY(y);
        }
    }
    public void setX2(float x) {
        if (body != null) {
            body.setX(x);
            swimmingBody.setX(x);
        }
        if (hair != null) {
            hair.setX(x);
        }
        if (top != null) {
            top.setX(x);
        }
        if (bottom != null) {
            bottom.setX(x);
        }
        if (bag != null) {
            bag.setX(x);
        }
    }

    public PlayerOutfit getOutfit() {
        return outfit;
    }


    /******************************************************************************************************************
     * SPRITE SETTERS
     *****************************************************************************************************************/

    public void initSpritePositions(float x, float y) {
        if (body != null) {
            body.setSize(28, 32);
            body.setX(x);
            body.setY(y);
            swimmingBody.setSize(28, 32);
            swimmingBody.setX(x);
            swimmingBody.setY(y);
        }
        if (hair != null) {
            hair.setSize(28,32);
            hair.setX(x);
            hair.setY(y);
            //hair.setBounds(x, y, 28 / AnimeArena.PPM, 32 / AnimeArena.PPM);
        }
        if (top != null) {
            top.setSize(28, 32);
            top.setX(x);
            top.setY(y);
        }
        if (bottom != null) {
            bottom.setBounds(x, y, 28 / AnimeArena.PPM, 32 / AnimeArena.PPM);
        }
        if (bag != null) {
            bag.setBounds(x, y, 28 / AnimeArena.PPM, 32 / AnimeArena.PPM);
        }
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

    /******************************************************************************************************************
     * END SPRITE SETTERS
     *****************************************************************************************************************/

    private void initOutfit() {
        if (playerProfile == null) {
            String body = "male-dark";
            String hairType = "MaleHair4";
            String hairColour = "red";
            String top = "t-shirt-green";
            String bottom = "shorts-red";
            outfit = new PlayerOutfit();
            outfit.setBodyType(body);
            outfit.setHairType(hairType);
            outfit.setHairColour(hairColour);
            outfit.setTop(top);
            outfit.setBottom(bottom);
        } else {
            outfit = new PlayerOutfit();
            outfit.setBodyType(playerProfile.getSkinTone());
            outfit.setHairType(playerProfile.getHairStyle());
            outfit.setHairColour(playerProfile.getHairColour());
            Clothing top = (Clothing) screen.getItemFactory().createItem(playerProfile.getTopID());
            Clothing bottom = (Clothing) screen.getItemFactory().createItem(playerProfile.getBottomID());
            outfit.setTop(top.getClothingID());
            outfit.setBottom(bottom.getClothingID());
        }
    }

    public PlayerSwitches getSwitches() {
        return switches;
    }

    public PlayerProfile getPlayerProfile() {
        return playerProfile;
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public Quest getCompletedQuestWithQuestSwitch(int switchID) {
        for (Quest quest : quests) {
            if (quest.hasQuestSwitch(switchID) && quest.isRequirementsComplete(switches)) {
                return quest;
            }
        }
        return null;
    }

    public boolean hasQuest(int questID) {
        for (Quest quest : quests) {
            if (quest.getQuestID() == questID) {
                return true;
            }
        }
        return false;
    }

    public Quest getQuest(int questID) {
        for (Quest quest : quests) {
            if (quest.getQuestID() == questID) {
                return quest;
            }
        }
        return null;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void startEvent() {
        inEvent = true;
    }

    public void endEvent() {
        if (interactingNPC != null) {
            interactingNPC.stopInteractingWithPlayer();
            interactingNPC = null;
        }
        inEvent = false;
    }

    public void setYTile(int y) {
        yTile = y;
    }

    public void setXTile(int x) {
        xTile = x;
    }

    public float getMoveButtonDownTime() {
        return moveButtonDownTime;
    }

    public void resetMoveButtonDownTime() {
        moveButtonDownTime = 0;
    }

    public boolean isMoving() {
        if (velocity.x == 0 && velocity.y == 0) {
            return false;
        }
        return true;
    }

    public boolean isMoving(Direction direction) {
        if (velocity.x > 0 && direction == Direction.RIGHT) {
            return true;
        } else if (velocity.x < 0 && direction == Direction.LEFT) {
            return true;
        } else if (velocity.y > 0 && direction == Direction.UP) {
            return true;
        } else if (velocity.y < 0 && direction == Direction.DOWN) {
            return true;
        }
        return false;
    }

    //Stopping vs Stopped is different.
    public boolean isStopped() {
        if (stoppingX == -100 && stoppingY == -100) {
            return true;
        }
        return false;
    }

    public void draw(Batch spriteBatch) {

        if (!swimming) {
            drawShadow(spriteBatch);
            if (body != null) {
                body.draw(spriteBatch);
            }
            if (bottom != null) {
                bottom.draw(spriteBatch);
            }
            if (top != null) {
                top.draw(spriteBatch);
            }
            if (bag != null) {
                bag.draw(spriteBatch);
            }
            if (hair != null) {
                hair.draw(spriteBatch);
            }
        } else {
            if (swimmingBody != null) {
                swimmingBody.draw(spriteBatch);
                Gdx.app.log("", swimmingBody.getHeight() + "");
            }
        }
    }

    private void drawShadow(Batch spriteBatch) {
        spriteBatch.draw(shadow, getX() + 7, getY());
    }

    public void jumpDown() {
        jumping = true;
        velocity.y = jumpUpSpeed;
        maxJumpHeight = getY() + 8; //Add half a tile to the current height
        screen.playJumpSound();
    }

    public void swimUp() {
        swimmingUp = true;
        screen.playJumpSound();
        velocity.y = -swimSpeed;
    }

    public void swimDown() {
        swimmingDown = true;
        screen.playJumpSound();
        velocity.y = swimSpeed;
    }

    public void swimLeft() {
        swimmingLeft = true;
        screen.playJumpSound();
        velocity.x = swimSpeed;
    }

    public void swimRight() {
        swimmingRight = true;
        screen.playJumpSound();
        velocity.x = -swimSpeed;
    }

    public void jumpLeft() {
        jumpingLeft = true;
        velocity.x = jumpLeftSpeed;
        velocity.y = jumpUpSpeed;
        maxJumpHeight = getY() + 8;
        screen.playJumpSound();
    }

    public void jumpRight() {
        jumpingRight = true;
        velocity.x = jumpRightSpeed;
        velocity.y = jumpUpSpeed;
        maxJumpHeight = getY() + 8;
        screen.playJumpSound();
    }

    public void startSwimming() {
        swimming = true;
    }

    public void interact() {
        if (direction == Direction.UP) {
            if (!swimming && isSwimmingTile(getTopCell2())) {
                Gdx.app.log("Log", "Start Swimming Event");
                screen.startSwimmingEvent();
            } else {
                TreeObject treeObject = pokemonMap.getTree(xTile, yTile + 1);
                if (treeObject != null) {
                    treeObject.manualInteract(this);
                    return;
                }
                ItemObject itemObject = pokemonMap.getItemObject(xTile, yTile + 1);
                if (itemObject != null) {
                    itemObject.manualInteract(this);
                    return;
                }
                BerryObject berryObject = pokemonMap.getBerryObject(xTile, yTile + 1);
                if (berryObject != null) {
                    berryObject.manualInteract(this);
                    return;
                }
                NPCObject npcObject = pokemonMap.getNPCObject(xTile, yTile + 1);
                if (npcObject != null) {
                    npcObject.getSprite().setX(xFormula(xTile));
                    npcObject.manualInteract(this, Direction.DOWN);
                    interactingNPC = npcObject;
                    return;
                }
                TrainerObject trainerObject = pokemonMap.getTrainerObject(xTile, yTile + 1);
                if (trainerObject != null) {
                    trainerObject.getSprite().setX(xFormula(xTile));
                    trainerObject.manualInteract(this, Direction.DOWN);
                    interactingNPC = trainerObject;
                    return;
                }

            }
        } else if (direction == Direction.DOWN) {
            if (!swimming && isSwimmingTile(getBottomCell2())) {
                Gdx.app.log("Log", "Start Swimming Event");
                screen.startSwimmingEvent();
            } else {
                TreeObject treeObject = pokemonMap.getTree(xTile, yTile - 1);
                if (treeObject != null) {
                    treeObject.manualInteract(this);
                    return;
                }
                ItemObject itemObject = pokemonMap.getItemObject(xTile, yTile - 1);
                if (itemObject != null) {
                    itemObject.manualInteract(this);
                    return;
                }
                NPCObject npcObject = pokemonMap.getNPCObject(xTile, yTile - 1);
                if (npcObject != null) {
                    npcObject.getSprite().setX(xFormula(xTile));
                    npcObject.manualInteract(this, Direction.UP);
                    interactingNPC = npcObject;
                    return;
                }
                TrainerObject trainerObject = pokemonMap.getTrainerObject(xTile, yTile - 1);
                if (trainerObject != null) {
                    trainerObject.getSprite().setX(xFormula(xTile));
                    trainerObject.manualInteract(this, Direction.UP);
                    interactingNPC = trainerObject;
                    return;
                }
            }
        } else if (direction == Direction.LEFT) {
            if (!swimming && isSwimmingTile(getLeftCell2())) {
                Gdx.app.log("Log", "Start Swimming Event");
                screen.startSwimmingEvent();
            } else {
                TreeObject treeObject = pokemonMap.getTree(xTile - 1, yTile);
                if (treeObject != null) {
                    treeObject.manualInteract(this);
                    return;
                }
                ItemObject itemObject = pokemonMap.getItemObject(xTile - 1, yTile);
                if (itemObject != null) {
                    itemObject.manualInteract(this);
                    return;
                }
                NPCObject npcObject = pokemonMap.getNPCObject(xTile - 1, yTile);
                if (npcObject != null) {
                    npcObject.getSprite().setY(yFormula(yTile));
                    npcObject.manualInteract(this, Direction.RIGHT);
                    interactingNPC = npcObject;
                    return;
                }
                TrainerObject trainerObject = pokemonMap.getTrainerObject(xTile - 1, yTile);
                if (trainerObject != null) {
                    trainerObject.getSprite().setY(yFormula(yTile));
                    trainerObject.manualInteract(this, Direction.RIGHT);
                    interactingNPC = trainerObject;
                    return;
                }
            }
        } else if (direction == Direction.RIGHT) {
            if (!swimming && isSwimmingTile(getRightCell2())) {
                Gdx.app.log("Log", "Start Swimming Event");
                screen.startSwimmingEvent();
            } else {
                TreeObject treeObject = pokemonMap.getTree(xTile + 1, yTile);
                if (treeObject != null) {
                    treeObject.manualInteract(this);
                    return;
                }
                ItemObject itemObject = pokemonMap.getItemObject(xTile + 1, yTile);
                if (itemObject != null) {
                    itemObject.manualInteract(this);
                    return;
                }
                NPCObject npcObject = pokemonMap.getNPCObject(xTile + 1, yTile);
                if (npcObject != null) {
                    npcObject.getSprite().setY(yFormula(yTile));
                    npcObject.manualInteract(this, Direction.LEFT);
                    interactingNPC = npcObject;
                    return;
                }
                TrainerObject trainerObject = pokemonMap.getTrainerObject(xTile + 1, yTile);
                if (trainerObject != null) {
                    trainerObject.getSprite().setY(yFormula(yTile));
                    trainerObject.manualInteract(this, Direction.LEFT);
                    interactingNPC = trainerObject;
                    return;
                }
            }
        } else {
            jumpRight();
        }
    }


    public void endJump() {
        currentState = State.STANDING;
    }

    public boolean isJumping() {
        if (jumping || jumpingRight || jumpingLeft) {
            return true;
        }
        return false;
    }

    /**
     * Return whether or not this player can be moved using the arrow keys.
     * The player cannot be moved when: Jumping, Being Approached By A Trainer, A Menu is open etc
     * @return True if the player is movable with the arrow keys.
     */
    public boolean isMovable() {
        if (jumping || jumpingLeft || jumpingRight || sliding || inEvent || swimmingUp) {
            return false;
        }
        return true;
    }

    public int getXTile() {
        return xTile;
    }

    public int getYTile() {
        return yTile;
    }

    public float getVelocityX() { return velocity.x; }

    public float getVelocityY() { return velocity.y; }

    private WarpObject collidesWithWarp(int collisionX, int collisionY) {
        if (!isJumping()) {
            for (WarpObject warp : pokemonMap.getWarps()) {
                if (warp.occupiesCell(collisionX, collisionY)) {
                    return warp;
                }
            }
        }
        return null;
    }

    private EventObject collidesWithEvent(int collisionX, int collisionY) {
        if (!isJumping()) {
            for (EventObject eventObject : pokemonMap.getEventObjects()) {
                if (eventObject.occupiesCell(collisionX, collisionY)) {
                    return eventObject;
                }
            }
        }
        return null;
    }

    private boolean collidesWithObject(int collisionX, int collisionY) {
        if (!isJumping()) {
            int potentialCollisionX = collisionX;
            int potentialCollisionY = collisionY;
            if (velocity.x > 0) {
                potentialCollisionX++;
            } else if (velocity.x < 0) {
                potentialCollisionX--;
            } else if (velocity.y > 0) {
                potentialCollisionY++;
            } else if (velocity.y < 0) {
                potentialCollisionY--;
            }

            for (TreeObject tree : pokemonMap.getTrees()) {
                if (tree.isVisible() && tree.occupiesCell(collisionX, collisionY)) {
                    return true;
                }
            }
            for (BerryObject berry : pokemonMap.getBerryTrees()) {
                if (berry.isVisible() && berry.occupiesCell(collisionX, collisionY)) {
                    return true;
                }
            }
            for (ItemObject item : pokemonMap.getItems()) {
                if (item.isVisible() && item.occupiesCell(collisionX, collisionY)) {
                    return true;
                }
            }
            for (NPCObject npc : pokemonMap.getNPCs()) {
                if (npc.isVisible() && npc.occupiesCell(collisionX, collisionY)) {
                    return true;
                }
            }
            for (TrainerObject trainer : pokemonMap.getTrainers()) {
                if (trainer.isVisible() && trainer.occupiesCell(collisionX, collisionY)) {
                    return true;
                }
            }
        }
        return false;
    }

    //Grid Collision Detection
    private void collisionDetection(float dt) {

        float oldX = getX();
        float oldY = getY();

        boolean collisionX = false;
        boolean collisionY = false;

        boolean collisionDownLedge = false;
        boolean collisionRightLedge = false;
        boolean collisionLeftLedge = false;

        boolean collisionWithSlidingCell = false;


        WarpObject warpObject;
        EventObject eventObject;
        int tileHeight = 16;
        int tileWidth = 16;



        //Attempt to Move in the X axis
        setX(getX() + velocity.x * dt);

        //Check collision on the X axis
        if (velocity.x < 0) { //LEFT
            collisionX = collidesWithCell(getLeftCell2());
            if (!collisionX) {
                collisionX = collidesWithObject(xTile - 1, yTile);
            }
            collisionWithSlidingCell = collidesWithSlidingCell(getLeftCell2());
            collisionLeftLedge = collidesWithLeftLedgeCell(getLeftCell2());
            if (swimming && collidesWithLandCell(getLeftCell2())) {
                swimming = false;
                screen.playJumpSound();
                screen.playMapBgm();
            }

        } else if (velocity.x > 0) { //RIGHT
            collisionX = collidesWithCell(getRightCell2());
            if (!collisionX) {
                collisionX = collidesWithObject(xTile + 1, yTile);
            }
            collisionWithSlidingCell = collidesWithSlidingCell(getRightCell2());
            collisionRightLedge = collidesWithRightLedgeCell(getRightCell2());
            if (swimming && collidesWithLandCell(getRightCell2())) {
                swimming = false;
                screen.playJumpSound();
                screen.playMapBgm();
            }
        }

        //Keep old position in the X axis if there is a collision
        if (collisionX) {
            setX(oldX);
            setX(xFormula(xTile));
            velocity.x = 0;
            screen.playBumpSound();
        } else if (collisionLeftLedge) {
            setX(oldX);
            velocity.x = 0;
            jumpLeft();
        } else if (collisionRightLedge) {
            setX(oldX);
            velocity.x = 0;
            jumpRight();
        } else if (velocity.x != 0) {
            if (collisionWithSlidingCell) {
                sliding = true;
                velocity.x = velocity.x > 0 ? slideSpeed : -slideSpeed;
            }
            if (velocity.x > 0 && getX() > xFormula(xTile + 1)) {
                xTile++;
                warpObject = collidesWithWarp(xTile, yTile);
                eventObject = collidesWithEvent(xTile, yTile);
                if (warpObject != null) {
                    setX(xFormula(xTile));
                    velocity.x = 0;
                    stoppingX = -100;
                    screen.setEvent(new WarpEvent(screen, warpObject.getMapName(), warpObject.getPositionX(), warpObject.getPositionY()));
                }
                else if (eventObject != null) {
                    setX(xFormula(xTile));
                    velocity.x = 0;
                    stoppingX = -100;
                    screen.setEvent(eventObject.getEvent());
                } else {
                    TrainerObject trainerInSight = getInSightTrainer();
                    if (trainerInSight != null) {
                        Gdx.app.log("Trainer Sight", "true");
                        setX(xFormula(xTile));
                        velocity.x = 0;
                        stoppingX = -100;
                        screen.startEncounterEvent(new EncounterEvent(screen, trainerInSight));
                    } else if (collidesWithGrassCell(getCurrentCell()) && triggerWildPokemon()) {
                        setX(xFormula(xTile));
                        velocity.x = 0;
                        stoppingX = -100;
                        screen.startWildPokemonEvent();
                    }
                }
                if (xTile == stoppingX) {
                    if (sliding) {
                        stoppingX++;
                    } else {
                        stoppingX = -100;
                        velocity.x = 0;
                        moveButtonDownTime = 0;
                    }
                }
                else if (collidesWithCell(getRightCell2())) {
                    setX(xFormula(xTile));
                    velocity.x = 0;
                }
            } else if (velocity.x < 0 && getX() < xFormula(xTile - 1)) {
                xTile--;
                warpObject = collidesWithWarp(xTile, yTile);
                eventObject = collidesWithEvent(xTile, yTile);
                if (warpObject != null) {
                    setX(xFormula(xTile));
                    velocity.x = 0;
                    stoppingX = -100;
                    screen.setEvent(new WarpEvent(screen, warpObject.getMapName(), warpObject.getPositionX(), warpObject.getPositionY()));
                } else if (eventObject != null) {
                    setX(xFormula(xTile));
                    velocity.x = 0;
                    stoppingX = -100;
                    screen.setEvent(eventObject.getEvent());
                } else {
                    TrainerObject trainerInSight = getInSightTrainer();
                    if (trainerInSight != null) {
                        Gdx.app.log("Trainer Sight", "true");
                        setX(xFormula(xTile));
                        velocity.x = 0;
                        stoppingX = -100;
                        screen.startEncounterEvent(new EncounterEvent(screen, trainerInSight));
                    } else if (collidesWithGrassCell(getCurrentCell()) && triggerWildPokemon()) {
                        setX(xFormula(xTile));
                        velocity.x = 0;
                        stoppingX = -100;
                        screen.startWildPokemonEvent();
                    } else {
                        if (xTile == stoppingX) {
                            if (sliding) {
                                stoppingX--;
                            } else {
                                velocity.x = 0;
                                stoppingX = -100;
                                moveButtonDownTime = 0;
                            }
                        } else if (collidesWithCell(getLeftCell2())) {
                            setX(xFormula(xTile));
                            velocity.x = 0;
                        }
                    }
                }
            }
        } else {
            velocity.x = 0;
            setX(xFormula(xTile));
            stoppingX = -100;
            //moveButtonDownTime = 0;
        }


        //Attempt to Move in the Y axis
        setY(getY() + velocity.y * dt);

        //Check collision on the Y axis
        if (velocity.y < 0) { //DOWN
            collisionY = collidesWithCell(getBottomCell2());
            if (!collisionY) {
                collisionY = collidesWithObject(xTile, yTile - 1);
            }
            collisionWithSlidingCell = collidesWithSlidingCell(getBottomCell2());
            collisionDownLedge = collidesWithJumpDownCell(getBottomCell2());
            if (swimming && collidesWithLandCell(getBottomCell2())) {
                swimming = false;
                screen.playJumpSound();
                screen.playMapBgm();
            }

        } else if (velocity.y > 0) { //UP
            collisionY = collidesWithCell(getTopCell2());
            if (!collisionY) {
                collisionY = collidesWithObject(xTile, yTile + 1);
            }
            collisionWithSlidingCell = collidesWithSlidingCell(getTopCell2());
            if (swimming && collidesWithLandCell(getTopCell2())) {
                swimming = false;
                screen.playJumpSound();
                screen.playMapBgm();
            }
        }

        //Keep old position in the Y axis if there is a collision
        if (collisionY) {
            setY(oldY);
            setY(yFormula(yTile));
            velocity.y = 0;
            screen.playBumpSound();
        }
        else if (collisionDownLedge) {
          setY(oldY);
          velocity.y = 0;
          jumpDown();
        } else if (velocity.y != 0) {
            if (collisionWithSlidingCell) {
                sliding = true;
                velocity.y = velocity.y > 0 ? slideSpeed : -slideSpeed;
            }
            if (velocity.y > 0 && getY() > yFormula(yTile + 1)) {
                yTile++;
                warpObject = collidesWithWarp(xTile, yTile);
                eventObject = collidesWithEvent(xTile, yTile);
                if (warpObject != null) {
                    setY(yFormula(yTile));
                    velocity.y = 0;
                    stoppingY = -100;
                    screen.setEvent(new WarpEvent(screen, warpObject.getMapName(), warpObject.getPositionX(), warpObject.getPositionY()));
                } else if (eventObject != null) {
                    setY(yFormula(yTile));
                    velocity.y = 0;
                    stoppingY = -100;
                    screen.setEvent(eventObject.getEvent());
                } else {
                    TrainerObject trainerInSight = getInSightTrainer();
                    if (trainerInSight != null) {
                        Gdx.app.log("Trainer Sight", "true");
                        setY(yFormula(yTile));
                        velocity.y = 0;
                        stoppingY = -100;
                        screen.startEncounterEvent(new EncounterEvent(screen, trainerInSight));
                    } else if (collidesWithGrassCell(getCurrentCell()) && triggerWildPokemon()) {
                        setY(yFormula(yTile));
                        velocity.y = 0;
                        stoppingY = -100;
                        screen.startWildPokemonEvent();
                    } else {
                        if (yTile == stoppingY) {
                            if (sliding) {
                                stoppingY++;
                            } else {
                                velocity.y = 0;
                                stoppingY = -100;
                                moveButtonDownTime = 0;
                            }
                        } else if (collidesWithCell(getTopCell2())) {
                            setY(yFormula(yTile));
                            velocity.y = 0;
                        }
                    }
                }
            } else if (velocity.y < 0 && getY() < yFormula(yTile - 1)) {
                yTile--;
                warpObject = collidesWithWarp(xTile, yTile);
                eventObject = collidesWithEvent(xTile, yTile);
                if (warpObject != null) {
                    setY(yFormula(yTile));
                    velocity.y = 0;
                    stoppingY = -100;
                    screen.setEvent(new WarpEvent(screen, warpObject.getMapName(), warpObject.getPositionX(), warpObject.getPositionY()));
                } else if (eventObject != null) {
                    setY(yFormula(yTile));
                    velocity.y = 0;
                    stoppingY = -100;
                    screen.setEvent(eventObject.getEvent());
                } else {
                    TrainerObject trainerInSight = getInSightTrainer();
                    if (trainerInSight != null) {
                        Gdx.app.log("Trainer Sight", "true");
                        setY(yFormula(yTile));
                        velocity.y = 0;
                        stoppingY = -100;
                        screen.startEncounterEvent(new EncounterEvent(screen, trainerInSight));
                    } else if (collidesWithGrassCell(getCurrentCell()) && triggerWildPokemon()) {
                        setY(yFormula(yTile));
                        velocity.y = 0;
                        stoppingY = -100;
                        screen.startWildPokemonEvent();
                    } else {
                        if (yTile == stoppingY) {
                            if (sliding) {
                                stoppingY--;
                            } else {
                                velocity.y = 0;
                                stoppingY = -100;
                                moveButtonDownTime = 0;
                            }
                        } else if (collidesWithCell(getBottomCell2())) {
                            setY(yFormula(yTile));
                            velocity.y = 0;
                        }
                    }
                }
            }
        } else {
                velocity.y = 0;
                setY(yFormula(yTile));
                stoppingY = -100;
                //moveButtonDownTime = 0;
        }

    }

    private TrainerObject getInSightTrainer() {
        for (TrainerObject trainer : pokemonMap.getTrainers()) {
            int distance = 0;
            Direction direction = null;
            if (trainer.getDirection() == Direction.DOWN) {
                if (xTile == trainer.getX() && yTile < trainer.getY() && yTile >= trainer.getY() - trainer.getDownVision()) {
                    distance = trainer.getY() - yTile - 1;
                    direction = Direction.DOWN;
                    trainer.setMovementPath(direction, distance);
                    return trainer;
                }
            } else if (trainer.getDirection() == Direction.UP) {
                if (xTile == trainer.getX() && yTile > trainer.getY() && yTile <= trainer.getY() + trainer.getUpVision()) {
                    distance = yTile - trainer.getY() - 1;
                    direction = Direction.UP;
                    trainer.setMovementPath(direction, distance);
                    return trainer;
                }
            } else if (trainer.getDirection() == Direction.RIGHT) {
                if (yTile == trainer.getY() && xTile > trainer.getX() && xTile <= trainer.getX() + trainer.getRightVision()) {
                    distance = xTile - trainer.getX() - 1;
                    direction = Direction.RIGHT;
                    trainer.setMovementPath(direction, distance);
                    return trainer;
                }
            } else if (trainer.getDirection() == Direction.LEFT) {
                if (yTile == trainer.getY() && xTile < trainer.getX() && xTile >= trainer.getX() - trainer.getLeftVision()) {
                    distance = trainer.getX() - xTile - 1;
                    direction = Direction.LEFT;
                    trainer.setMovementPath(direction, distance);
                    return trainer;
                }
            }
        }
        return null;
    }

    private int xFormula(int tile) {
        return -5 + (16 * tile);
    }

    public void clampX() {
        setX(xFormula(xTile));
    }

    public void clampY() {
        setY(yFormula(yTile));
    }

    private int yFormula(int tile) {
        return (16 * tile);
    }

    protected void setVelocity() {
        if (eventMovement != null) {
            if (eventMovement.getMovement() == Movement.MOVE_LEFT) {
                velocity.y = 0;
                direction = Direction.LEFT;
                velocity.x = -speed;
            } else if (eventMovement.getMovement() == Movement.MOVE_RIGHT) {
                velocity.y = 0;
                direction = Direction.RIGHT;
                velocity.x = speed;
            } else if (eventMovement.getMovement() == Movement.MOVE_UP) {
                velocity.x = 0;
                direction = Direction.UP;
                velocity.y = speed;
            } else if (eventMovement.getMovement() == Movement.MOVE_DOWN) {
                velocity.x = 0;
                direction = Direction.DOWN;
                velocity.y = -speed;
            } else {
                velocity.x = 0;
                velocity.y = 0;
            }
        }
    }

    private void updateDirection() {

        if (eventMovement.getMovement() == Movement.MOVE_UP || eventMovement.getMovement() == Movement.LOOK_UP) {
            direction = Direction.UP;
        } else if (eventMovement.getMovement() == Movement.MOVE_DOWN || eventMovement.getMovement() == Movement.LOOK_DOWN) {
            direction = Direction.DOWN;
        } else if (eventMovement.getMovement() == Movement.MOVE_LEFT || eventMovement.getMovement() == Movement.LOOK_LEFT) {
            direction = Direction.LEFT;
        } else if (eventMovement.getMovement() == Movement.MOVE_RIGHT || eventMovement.getMovement() == Movement.LOOK_RIGHT) {
            direction = Direction.RIGHT;
        }
    }

    private void checkNextMovementX() {
        if (!eventMovement.hasNextMovement()) {
            eventMovement.resetMovementScript();
            eventMovement = null;
            getNextMovement = false;
            setX(xFormula(xTile));
            velocity.x = 0;
        } else if (!eventMovement.hasSameNextMovement()) {
            setX(xFormula(xTile));
            velocity.x = 0;
            getNextMovement = true;
        } else {
            getNextMovement = true;
        }
    }

    private void checkNextMovementY() {
        if (!eventMovement.hasNextMovement()) {
            eventMovement.resetMovementScript();
            eventMovement = null;
            getNextMovement = false;
            setY(yFormula(yTile));
            velocity.y = 0;
        } else if (!eventMovement.hasSameNextMovement()) {
            setY(yFormula(yTile));
            velocity.y = 0;
            getNextMovement = true;
        } else {
            getNextMovement = true;
        }
    }

    private void updateEventMovement(float dt) {

        //Attempt to Move in the X axis
        setX(getX() + velocity.x * dt);
        Gdx.app.log("X Coo", "" + getX());
        if (velocity.x != 0) {
            if (velocity.x > 0 && getX() > xFormula(xTile + 1)) {
                xTile++;
                checkNextMovementX();
            } else if (velocity.x < 0 && getX() < xFormula(xTile - 1)) {
                xTile--;
                checkNextMovementX();
            }
        }
        //Attempt to Move in the Y axis
        setY(getY() + velocity.y * dt);
        if (velocity.y != 0) {
            if (velocity.y > 0 && getY() > yFormula(yTile + 1)) {
                yTile++;
                checkNextMovementY();
            } else if (velocity.y < 0 && getY() < yFormula(yTile - 1)) {
                yTile--;
                checkNextMovementY();
            }
        }



    }
        public void update(float dt) {
       getFrame(dt);
        if (jumping) {
            if (!reachedMaxHeight) {
                setY(getY() + velocity.y * dt);
                if (getY() >= maxJumpHeight) {
                    setY(maxJumpHeight);
                    reachedMaxHeight = true;
                    velocity.y = fallingSpeed;
                }
            } else {
                setY(getY() + velocity.y * dt);
                if (getY() <= yFormula(yTile - 2)) {
                    setY(yFormula(yTile - 2));
                    yTile = yTile - 2;
                    jumping = false;
                    reachedMaxHeight = false;
                    velocity.y = 0;
                    stoppingY = -100;
                }
            }
         //if !reachedMaxHeight then velocity.y = jumpUpSpeed
         //else velocity.y = fall speed
         //if new y < tile y (use the yFormula) then snap it to that tile and set jumping = false
        }
        else if (jumpingLeft) {

            if (!reachedMaxHeight) {
                setY(getY() + velocity.y * dt);
                if (getY() >= maxJumpHeight) {
                    setY(maxJumpHeight);
                    reachedMaxHeight = true;
                    velocity.y = fallingSpeed;
                }
            } else {
                setY(getY() + velocity.y * dt);
                if (getY() <= yFormula(yTile)) {
                    setY(yFormula(yTile));
                }
            }
            setX(getX() + jumpLeftSpeed * dt);
            if (getX() <= xFormula(xTile - 2)) {
                setX(xFormula(xTile - 2));
                setY(yFormula(yTile));
                xTile = xTile - 2;
                jumpingLeft = false;
                reachedMaxHeight = false;
                velocity.y = 0;
                velocity.x = 0;
                stoppingX = -100;
                stoppingY = -100;
            }
        }
        else if (jumpingRight) {
            if (!reachedMaxHeight) {
                setY(getY() + velocity.y * dt);
                if (getY() >= maxJumpHeight) {
                    setY(maxJumpHeight);
                    reachedMaxHeight = true;
                    velocity.y = fallingSpeed;
                }
            } else {
                setY(getY() + velocity.y * dt);
                if (getY() <= yFormula(yTile)) {
                    setY(yFormula(yTile));
                }
            }
            setX(getX() + jumpRightSpeed * dt);
            if (getX() >= xFormula(xTile + 2)) {
                setX(xFormula(xTile + 2));
                setY(yFormula(yTile));
                xTile = xTile + 2;
                jumpingRight = false;
                reachedMaxHeight = false;
                velocity.y = 0;
                velocity.x = 0;
                stoppingX = -100;
                stoppingY = -100;
            }
        } else if (swimmingUp) {
            setY(getY() + velocity.y * dt);
            if (getY() >= yFormula(yTile + 1)) {
                setY(yFormula(yTile + 1));
                yTile = yTile + 1;
                swimmingUp = false;
                velocity.y = 0;
                stoppingY = -100;
            }
        }
        else {
            if (eventMovement != null) {
               setVelocity();
               updateEventMovement(dt);
                if (getNextMovement) {
                    eventMovement.setNextMovement();
                    getNextMovement = false;
                    updateDirection();
                }
            } else {
                collisionDetection(dt);
            }

        }


        if (screen.getEvent() == null || !screen.getEvent().isCameraControllingEvent()) {
            //Update Camera based on Player Position - The + 16 and + 6 are random offsets to make sure the sprite is center
            if (getX() >= AnimeArena.V_WIDTH / 2 / AnimeArena.PPM - 16 &&
                    getX() <= (pokemonMap.getMapWidth()) / AnimeArena.PPM - AnimeArena.V_WIDTH / 2 / AnimeArena.PPM - 16) {
                gameCam.position.x = getX() + 16;
            }

            if (getY() >= AnimeArena.V_HEIGHT / 2 / AnimeArena.PPM - 6 &&
                    getY() <= ((pokemonMap.getMapHeight()) / AnimeArena.PPM) - AnimeArena.V_HEIGHT / 2 / AnimeArena.PPM - 6) {
                gameCam.position.y = getY() + 6;
            }
        }
        updateSpritePosition();
        //Update Emojis


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


    public void moveLeft(float dt) {
        if (moveButtonDownTime >= 0.09 || (direction == Direction.LEFT && moveButtonDownTime == 0)) {
            velocity.x = -speed;
        } else {
            moveButtonDownTime += dt;
        }

        setDirection(Direction.LEFT);
    }

    public void moveRight(float dt) {

        if (moveButtonDownTime >= 0.09 || (direction == Direction.RIGHT && moveButtonDownTime == 0)) {
            velocity.x = speed;
        } else {
            moveButtonDownTime += dt;
        }
        setDirection(Direction.RIGHT);
    }

    public void moveUp(float dt) {
        if (moveButtonDownTime >= 0.09 || (direction == Direction.UP && moveButtonDownTime == 0)) {
            velocity.y = speed;
        } else {
            moveButtonDownTime += dt;
        }
        setDirection(Direction.UP);
    }

    public void moveDown(float dt) {
        if (moveButtonDownTime >= 0.09 || (direction == Direction.DOWN && moveButtonDownTime == 0)) {
            velocity.y = -speed;
        } else {
            moveButtonDownTime += dt;
        }
        setDirection(Direction.DOWN);
    }

    public void stopMoving() {
        if (velocity.x > 0) {
          stoppingX = xTile + 1;
        } else if (velocity.x < 0) {
            stoppingX = xTile - 1;
        }
        else if (velocity.y > 0) {
            stoppingY = yTile + 1;
        } else if (velocity.y < 0) {
            stoppingY = yTile - 1;
        }
    }

    public boolean isSliding() {
        return sliding;
    }

    public void run() {
        speed = 60 * 1.5f;
        running = true;
    }

    public void stopRun() {
        speed = 60 * 1f;
        running = false;
    }
    

    public void stopX() {
        stopMoving();
    }

    public void stopY() {
        stopMoving();
    }

    private boolean isSwimmingTile(TiledMapTileLayer.Cell cell) {
        if (cell != null) {
            int tileType = ((int)cell.getTile().getProperties().get("tileType"));
            if (tileType == 9) {
                return true;
            }
        }
        return false;
    }

    private boolean collidesWithCell(TiledMapTileLayer.Cell cell) {
        if (cell != null) {
            int tileType = ((int)cell.getTile().getProperties().get("tileType"));
            if (tileType == 8) {
                if (sliding) { //Sliding and blocked
                    sliding = false;
                    stoppingX = -100;
                    stoppingY = -100;
                }
                return true;
            } else if (tileType == 9 && !swimming) {
                return true;
            } else if (tileType == 4 && velocity.y >= 0) {
               return true;
            } else if (tileType == 3 && velocity.x >= 0) {
                return true;
            } else if (tileType == 5 && velocity.x <= 0) {
                return true;
            } else {
                if (sliding && tileType != 7) {
                    sliding = false;
                    if (velocity.x > 0) {
                        stoppingX++;
                    } else if (velocity.y < 0) {
                        stoppingY--;
                    } else if (velocity.y > 0) {
                        stoppingY++;
                    } else if (velocity.x < 0) {
                        stoppingX--;
                    }
                    stoppingY = -100;
                    return false;
                }
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean collidesWithSlidingCell(TiledMapTileLayer.Cell cell) {
        if (cell != null) {
            return ((int)cell.getTile().getProperties().get("tileType")) == 7;
        } else {
            return false;
        }
    }

    private boolean collidesWithLandCell(TiledMapTileLayer.Cell cell) {
        if (cell != null) {
            int cellType = ((int)cell.getTile().getProperties().get("tileType"));
            return cellType == 1 || cellType == 6;
        }
        return false;
    }

    private boolean collidesWithGrassCell(TiledMapTileLayer.Cell cell) {
        if (cell != null && !isJumping()) {
            if (((int)cell.getTile().getProperties().get("tileType")) == 1) {
                screen.playGrassStepSound();
                return true;
            }
        }
        return false;
    }

    private boolean triggerWildPokemon() {
        int random = (int)(Math.random() * 100 + 1);
        if (random <= 3) {
            return true;
        }
        return false;
    }

    private boolean collidesWithJumpDownCell(TiledMapTileLayer.Cell cell) {
        if (cell != null) {
            return ((int)cell.getTile().getProperties().get("tileType")) == 4;
        } else {
            return false;
        }
    }

    private boolean collidesWithLeftLedgeCell(TiledMapTileLayer.Cell cell) {
        if (cell != null) {
            return ((int)cell.getTile().getProperties().get("tileType")) == 3;
        } else {
            return false;
        }
    }

    private boolean collidesWithRightLedgeCell(TiledMapTileLayer.Cell cell) {
        if (cell != null) {
            return ((int)cell.getTile().getProperties().get("tileType")) == 5;
        } else {
            return false;
        }
    }

    private TiledMapTileLayer.Cell getCurrentCell() {
        return pokemonMap.getCollisionLayer() != null ? pokemonMap.getCollisionLayer().getCell(xTile, yTile) : null;
    }
    //Grid Detection getLeft
    private TiledMapTileLayer.Cell getLeftCell2() {
        return pokemonMap.getCollisionLayer() != null ? pokemonMap.getCollisionLayer().getCell(xTile - 1, yTile) : null;
    }

    //Grid Detection getRight
    private TiledMapTileLayer.Cell getRightCell2() {
        return pokemonMap.getCollisionLayer() != null ? pokemonMap.getCollisionLayer().getCell(xTile + 1, yTile) : null;
    }

    //Grid Detection getLeft
    private TiledMapTileLayer.Cell  getTopCell2() {
        return pokemonMap.getCollisionLayer() != null ? pokemonMap.getCollisionLayer().getCell(xTile, yTile + 1) : null;
    }

    //Grid Detection getRight
    private TiledMapTileLayer.Cell getBottomCell2() {
        return pokemonMap.getCollisionLayer() != null ? pokemonMap.getCollisionLayer().getCell(xTile, yTile - 1) : null;
    }


    private int getTileX() {
        return (int)((getX() + 5) / pokemonMap.getTileWidth());
    }

    private int getTileY() {
        return (int)((getY() + 5) / pokemonMap.getTileHeight());
    }


    public void setDirection(Direction d) {
        this.direction = d;
    }

    public Direction getDirection() { return direction; }

    public void setCurrentState(State newState) {
        currentState = newState;
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region;
        if (!swimming) {
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
            swimmingBody.setFrame(currentState, direction, stateTimer);
        } else {
            swimmingBody.setFrame(currentState, direction, stateTimer);
        }

        //Increment stateTimer if the currentState is the previous state, otherwise reset it.
        stateTimer = currentState == previousState ? stateTimer + dt: 0;
        previousState = currentState;

        return null;
    }


    public State getState() {
        if (isMoving() && !sliding && direction == Direction.UP) {
            return State.WALKING_UP;
        } else if (isMoving() && !sliding && direction == Direction.DOWN) {
            return State.WALKING_DOWN;
        } else if (isMoving() && !sliding && direction == Direction.RIGHT) {
            return State.WALKING_RIGHT;
        } else if (isMoving() && !sliding && direction == Direction.LEFT) {
            return State.WALKING_LEFT;
        } else {
            return State.STANDING;
        }
    }

    public State getCurrentState() {
        return currentState;
    }

    public void dispose() {
        shadow.dispose();
    }
}