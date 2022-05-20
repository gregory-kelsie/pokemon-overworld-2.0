package com.anime.arena.objects;

import com.anime.arena.AnimeArena;
import com.anime.arena.interactions.ChangeBGMEvent;
import com.anime.arena.interactions.Event;
import com.anime.arena.screens.PlayScreen;
import com.anime.arena.tools.MovementScript;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;
import java.util.List;

public class TrainerObject extends NPCObject {

    //TODO: Pokemon Team

    private int trainerID;
    private String title;
    private String name;
    private String afterBattleText; //The text the trainer says after beating them. Money is given afterwards.
    private Event afterDefeatEvent; //Ex: Talking to the trainer after they've been defeated.
    private int money;
    private String battleSprite;
    private String encounterBGM;
    private String battleBGM;

    //How many tiles the Trainer checks for the player in each direction
    private int upVision;
    private int rightVision;
    private int downVision;
    private int leftVision;

    private boolean drawEncounter;
    private float encounterYOffset;
    private float finishDrawingEncounterTimer;
    private Texture exclamationMark;
    private static final int EXCLAMATION_SPEED = 200;

    //Trainers will need a notice event to start which will then trigger the regular event when the notice event is finished.
    //Player Notice Distance (Up, Down, Left, Right) Default 0 if you have to walk up to start the battle.
    public TrainerObject(int x, int y, PlayScreen screen, Sprite npcSprite, MovementScript movementScript,
                         int trainerID, String title, String name, int money, String battleSprite,
                         String encounterBGM, String battleBGM, int upVision, int rightVision, int downVision, int leftVision,
                         String afterBattleText, Event beforeBattleEvent, Event afterDefeatEvent) {
        super(x, y, screen, npcSprite, beforeBattleEvent, movementScript, new ArrayList<List<Integer>>());
        this.npcSprite.setBounds(x * 16 - 2, y * 16, 28 / AnimeArena.PPM, 32 / AnimeArena.PPM);
        this.npcSprite.setSize(28, 32);
        this.trainerID = trainerID;
        this.title = title;
        this.name = name;
        this.afterBattleText = afterBattleText;
        this.afterDefeatEvent = afterDefeatEvent;
        this.money = money;
        this.battleSprite = battleSprite;
        this.encounterBGM = encounterBGM;
        this.battleBGM = battleBGM;
        this.upVision = upVision;
        this.rightVision = rightVision;
        this.downVision = downVision;
        this.leftVision = leftVision;

        this.drawEncounter = false;
        this.encounterYOffset = 0;
        this.finishDrawingEncounterTimer = 0;
        this.exclamationMark = new Texture("sprites/encounter.png");
    }

    public String getEncounterBGM() {
        return encounterBGM;
    }

    public String getBattleBGM() { return battleBGM; }

    public void setDrawEncounter(boolean drawEncounter) {
        this.drawEncounter = drawEncounter;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }


    public String getAfterBattleText() {
        return afterBattleText;
    }

    public Event getAfterDefeatEvent() {
        return afterDefeatEvent;
    }

    public int getMoney() {
        return money;
    }

    public int getUpVision() {
        return upVision;
    }

    public int getRightVision() {
        return rightVision;
    }

    public int getDownVision() {
        return downVision;
    }

    public int getLeftVision() {
        return leftVision;
    }

    public void manualInteract(Player player, Direction newDirection) {
        if (isVisible()) {
            screen.playSelectSound();
            this.direction = newDirection;
            if (player.getSwitches().battledTrainer(trainerID)) {
                screen.setEvent(afterDefeatEvent);
            } else {
                //Set the bgm here.
                screen.setBgm(Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/encounter/" + encounterBGM)));
                Event lastEvent = event.getLastEvent();
                lastEvent.setNextEvent(new ChangeBGMEvent(screen, "audio/bgm/battle/" + battleBGM));
                screen.setEvent(event);
            }
            this.interactingWithPlayer = true;
        }
    }

    public void update(float dt) {
        npcSprite.setRegion(getFrame(dt));
        if (!interactingWithPlayer) {
            setVelocity();
            collisionDetection(dt);
        }
        updateEncounterAnimation(dt);
    }

    private void updateEncounterAnimation(float dt) {
        if (drawEncounter) {
            if (encounterYOffset >= 25) {
                finishDrawingEncounterTimer += dt;
                if (finishDrawingEncounterTimer >= 1.5) {
                    //Reset remaining encounter variables
                    drawEncounter = false;
                    finishDrawingEncounterTimer = 0;
                    encounterYOffset = 0;
                }
            } else {
                encounterYOffset = encounterYOffset + (dt * EXCLAMATION_SPEED);
            }
        }

    }

    public boolean isDrawingEncounter() {
        return drawEncounter;
    }

    public void draw(Batch batch) {
        if (drawEncounter) {
            batch.draw(exclamationMark, npcSprite.getX() + 7, npcSprite.getY() + encounterYOffset + 2);
        }
        npcSprite.draw(batch);

    }

}
