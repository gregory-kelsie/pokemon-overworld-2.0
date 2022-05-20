package com.anime.arena.interactions;

import com.anime.arena.objects.Direction;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SwimEvent extends Event {

    public SwimEvent(PlayScreen screen) {
        super(screen);

    }
    @Override
    public void setNextEvent(Event nextEvent) {
        this.nextEvent = nextEvent;
    }

    @Override
    public void update(float dt) {
        player.startSwimming();
        if (player.getDirection() == Direction.UP) {
            player.swimUp();
        } else if (player.getDirection() == Direction.DOWN) {
            player.swimDown();
        } else if (player.getDirection() == Direction.LEFT) {
            player.swimLeft();
        } else {
            player.swimRight();
        }
        screen.setBgm(Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/surf.mp3")));
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
