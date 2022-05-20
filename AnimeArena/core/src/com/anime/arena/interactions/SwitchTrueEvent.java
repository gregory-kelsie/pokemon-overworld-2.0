package com.anime.arena.interactions;

import com.anime.arena.quest.Quest;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SwitchTrueEvent extends Event {
    private String[] switchArray;
    public SwitchTrueEvent(PlayScreen screen, String switches) {
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
            screen.getPlayer().getSwitches().activateSwitch(switchNum);
            Quest completedQuest = screen.getPlayer().getCompletedQuestWithQuestSwitch(switchNum);
            if (completedQuest != null) {
                //Display complete quest animation in the screen.
            }
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
