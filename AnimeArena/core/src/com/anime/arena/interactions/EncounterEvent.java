package com.anime.arena.interactions;

import com.anime.arena.objects.TrainerObject;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class EncounterEvent extends Event {

    private TrainerObject trainer;
    private Music encounterBgm;
    private boolean startedAnimation;
    public EncounterEvent(PlayScreen screen, TrainerObject trainer) {
        super(screen);
        this.trainer = trainer;
        encounterBgm = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/encounter/" + trainer.getEncounterBGM()));
        this.startedAnimation = false;
    }

    public Music getMusic() {
        return encounterBgm;
    }

    @Override
    public void update(float dt) {
        if (!startedAnimation) {
            trainer.setDrawEncounter(true);
            startedAnimation = true;
        }
        if (!trainer.isDrawingEncounter()) {
            Event movementEvent = new NPCMovementEvent(screen, trainer, trainer.getEventMovement());
            Event changeDirectionEvent = new ChangePlayerDirectionEvent(screen, trainer.getOppositeDirection());
            movementEvent.setNextEvent(changeDirectionEvent);
            changeDirectionEvent.setNextEvent(trainer.getInteractionEvent());
            Event lastEvent = trainer.getInteractionEvent().getLastEvent();
            lastEvent.setNextEvent(new ChangeBGMEvent(screen, "audio/bgm/battle/" + trainer.getBattleBGM()));
            screen.setEvent(movementEvent);
        }
    }

    public void setNextEvent(Event nextEvent) {
        this.nextEvent = nextEvent;
    }

    @Override
    public void interact() {

    }

    @Override
    public boolean isFinishedEvent() {
        return false;
    }

    @Override
    public Event getNextEvent() {
        return null;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void clickedUp() {

    }

    @Override
    public void clickedDown() {

    }

    @Override
    public void clickedLeft() {

    }

    @Override
    public void clickedRight() {

    }

    @Override
    public void render(SpriteBatch batch) {

    }
}
