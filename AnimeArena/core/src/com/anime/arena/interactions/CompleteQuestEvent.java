package com.anime.arena.interactions;

import com.anime.arena.quest.Quest;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CompleteQuestEvent extends Event {
    private int questID;
    public CompleteQuestEvent(PlayScreen screen, int questID) {
        super(screen);
        this.questID = questID;
    }

    @Override
    public void setNextEvent(Event nextEvent) {
        this.nextEvent = nextEvent;
    }

    @Override
    public void update(float dt) {
        Quest quest = screen.getPlayer().getQuest(questID);
        if (quest != null) {
            quest.completeQuest();
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
