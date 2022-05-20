package com.anime.arena.interactions;

import com.anime.arena.pokemon.PokemonUtils;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GiveItemEvent extends Event {
    private int itemID;
    private int amount;
    public GiveItemEvent(PlayScreen screen, int itemID, int amount) {
        super(screen);
        this.itemID = itemID;
        this.amount = amount;
    }
    @Override
    public void setNextEvent(Event nextEvent) {
        this.nextEvent = nextEvent;
    }

    @Override
    public void update(float dt) {
        screen.getPlayer().getBag().addItem(screen.getItemFactory(), itemID, amount);
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
