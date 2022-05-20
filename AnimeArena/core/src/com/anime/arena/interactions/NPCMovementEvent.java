package com.anime.arena.interactions;

import com.anime.arena.objects.NPCObject;
import com.anime.arena.objects.Player;
import com.anime.arena.screens.PlayScreen;
import com.anime.arena.tools.MovementScript;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NPCMovementEvent extends Event {

    private NPCObject npc;
    private MovementScript movements;
    public NPCMovementEvent(PlayScreen screen, NPCObject npc, MovementScript movements) {
        super(screen);
        this.npc = npc;
        this.npc.startMovementEvent(movements);
    }
    @Override
    public void setNextEvent(Event nextEvent) {
        this.nextEvent = nextEvent;
    }

    @Override
    public void update(float dt) {
        if (npc.isDoneEventMovement()) {
            npc.clearEventMovement();
            screen.setEvent(nextEvent);
        }
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
        return nextEvent;
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
