package com.anime.arena.interactions;

import com.anime.arena.quest.Quest;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SwitchFalseEvent extends Event {
    private String[] switchArray;
    public SwitchFalseEvent(PlayScreen screen, String switches) {
        super(screen);
        this.switchArray = switches.split(",");
    }

    @Override
    public void setNextEvent(Event nextEvent) {
        this.nextEvent = nextEvent;
    }

    @Override
    public void update(float dt) {
        for (String switchID : switchArray) {
            int switchNum = Integer.parseInt(switchID);
            screen.getPlayer().getSwitches().deactivateSwitch(switchNum);
        }
        screen.setEvent(nextEvent);
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
