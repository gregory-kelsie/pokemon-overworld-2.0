package com.anime.arena.interactions;

import com.anime.arena.objects.Player;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Event {

    protected Player player;
    protected boolean finishedEvent;
    protected PlayScreen screen;
    protected Event nextEvent;


    public Event(PlayScreen screen) {
        this.player = screen.getPlayer();
        this.finishedEvent = false;
        this.screen = screen;
    }

    public void setNextEvent(Event nextEvent) {
        this.nextEvent = nextEvent;
    }

    public void appendEvent(Event event) {
        if (this.nextEvent != null) {
            this.nextEvent.appendEvent(event);
        } else {
            this.nextEvent = event;
        }
    }

    public abstract void update(float dt);

    public abstract void interact(); //Clicked Z

    public abstract boolean isFinishedEvent();

    public Event getNextEvent() {
        return nextEvent;
    }

    public Event getLastEvent() {
        if (this.nextEvent == null) {
            return this;
        } else {
            return this.nextEvent.getLastEvent();
        }
    }

    public abstract void dispose();

    public abstract void clickedUp();

    public abstract void clickedDown();

    public abstract void clickedLeft();

    public abstract void clickedRight();

    public abstract void render(SpriteBatch batch);


}
