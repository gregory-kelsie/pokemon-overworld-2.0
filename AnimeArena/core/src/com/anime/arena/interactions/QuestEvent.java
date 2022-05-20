package com.anime.arena.interactions;

import com.anime.arena.quest.Quest;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class QuestEvent extends Event {
    private int questID;
    private Event completeEvent;
    private Event inProgressEvent;
    private Event notStartedEvent;
    private Event finishedEvent;
    public QuestEvent(PlayScreen screen, int questID, Event completeEvent, Event inProgressEvent, Event notStartedEvent, Event finishedEvent) {
        super(screen);
        this.questID = questID;
        this.completeEvent = completeEvent;
        this.inProgressEvent = inProgressEvent;
        this.notStartedEvent = notStartedEvent;
        this.finishedEvent = finishedEvent;
    }


    @Override
    public void update(float dt) {
        Quest quest = screen.getPlayer().getQuest(questID);
        if (quest != null) {
            if (quest.isComplete()) {
                screen.setEvent(completeEvent);
            } else {
                if (quest.isRequirementsComplete(player.getSwitches())) {
                    screen.setEvent(finishedEvent);
                } else {
                    screen.setEvent(inProgressEvent);
                }
            }
        } else {
            screen.setEvent(notStartedEvent);
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
