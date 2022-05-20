package com.anime.arena.interactions;

import com.anime.arena.quest.Quest;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ToggleSwitchEvent extends Event {

    private int switchID;
    public ToggleSwitchEvent(PlayScreen screen, int switchID) {
        super(screen);
        this.switchID = switchID;
    }

    @Override
    public void setNextEvent(Event nextEvent) {
        this.nextEvent = nextEvent;
    }

    @Override
    public void update(float dt) {
        screen.getPlayer().getSwitches().activateSwitch(switchID);
        Quest completedQuest = screen.getPlayer().getCompletedQuestWithQuestSwitch(switchID);
        if (completedQuest != null) {
            //Display complete quest animation in the screen.
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
