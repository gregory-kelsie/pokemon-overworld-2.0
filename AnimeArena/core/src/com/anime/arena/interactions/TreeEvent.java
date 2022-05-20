package com.anime.arena.interactions;

import com.anime.arena.objects.Player;
import com.anime.arena.objects.TreeObject;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TreeEvent extends Event {

    private TreeObject tree;
    public TreeEvent(PlayScreen screen, TreeObject tree) {
        super(screen);
        this.tree = tree;
    }
    @Override
    public void update(float dt) {
        tree.cut();
        dispose();
        screen.playCutSound();
        screen.setEvent(nextEvent);
    }

    public void setNextEvent(Event nextEvent) {

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
