package com.anime.arena.interactions;

import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class IfEvent extends Event {
    private Event defaultEvent; //Else block event
    private List<ConditionalEvent> conditionalEvents;
    public IfEvent(PlayScreen screen) {
        super(screen);
        this.conditionalEvents = new ArrayList<ConditionalEvent>();
    }

    public void addConditionalEvent(ConditionalEvent event) {
        conditionalEvents.add(event);
    }

    public void setDefaultEvent(Event event) {
        this.defaultEvent = event;
    }

    public void appendAfterIfEvent(Event event) {
        for (ConditionalEvent e : conditionalEvents) {
            e.getEvent().appendEvent(event);
        }
        if (defaultEvent != null) {
            defaultEvent.appendEvent(event);
        }
    }


    @Override
    public void update(float dt) {
        boolean setEvent = false;
        for (ConditionalEvent e : conditionalEvents) {
            if (e.isTrue(screen.getPlayer().getSwitches())) {
                screen.setEvent(e.getEvent());
                setEvent = true;
            }
        }
        if (!setEvent) {
            screen.setEvent(defaultEvent);
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
