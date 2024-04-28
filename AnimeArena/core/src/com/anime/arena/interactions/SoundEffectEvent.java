package com.anime.arena.interactions;

import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SoundEffectEvent extends Event {

    private Music soundEffect;
    public SoundEffectEvent(PlayScreen screen, String soundEffect) {
        super(screen);
        this.soundEffect = Gdx.audio.newMusic(Gdx.files.internal("audio/SE/cry/015.ogg"));
    }
    @Override
    public void setNextEvent(Event nextEvent) {
        this.nextEvent = nextEvent;
    }

    @Override
    public void update(float dt) {
        soundEffect.play();
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
        return null;
    }

    @Override
    public void dispose() {
        this.soundEffect.dispose();
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
