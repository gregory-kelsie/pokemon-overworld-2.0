package com.anime.arena.interactions;

import com.anime.arena.objects.Direction;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ChangeBGMEvent extends Event {

    private Music newBGM;
    public ChangeBGMEvent(PlayScreen screen, String bgm) {
        super(screen);
        this.newBGM = Gdx.audio.newMusic(Gdx.files.internal(bgm));
    }
    @Override
    public void setNextEvent(Event nextEvent) {
        this.nextEvent = nextEvent;
    }

    @Override
    public void update(float dt) {
        screen.setBgm(newBGM);
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
