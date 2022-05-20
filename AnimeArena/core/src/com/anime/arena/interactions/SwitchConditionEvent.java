package com.anime.arena.interactions;

import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SwitchConditionEvent extends Event {

    private int switchID;
    private Event trueEvent;
    private Event falseEvent;
    public SwitchConditionEvent(PlayScreen screen, int switchID, Event trueEvent, Event falseEvent) {
        super(screen);
        this.switchID = switchID;
        this.trueEvent = trueEvent;
        this.falseEvent = falseEvent;
    }

    @Override
    public void setNextEvent(Event nextEvent) {

    }

    @Override
    public void update(float dt) {
        if (screen.getPlayer().getSwitches().isActive(switchID)) {
            screen.setEvent(trueEvent);
        } else {
            screen.setEvent(falseEvent);
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
