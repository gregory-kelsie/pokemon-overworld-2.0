package com.anime.arena.interactions;

import com.anime.arena.pokemon.PokemonUtils;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HealPartyEvent extends Event {

    private Music healSoundEffect;
    private boolean playedJingle;
    public HealPartyEvent(PlayScreen screen) {
        super(screen);
        this.healSoundEffect = Gdx.audio.newMusic(Gdx.files.internal("audio/SE/healPokemon.wav"));
        this.playedJingle = false;
    }
    @Override
    public void setNextEvent(Event nextEvent) {
        this.nextEvent = nextEvent;
    }

    @Override
    public void update(float dt) {
        if (!playedJingle) {
            screen.stopBgm();
            playedJingle = true;
            healSoundEffect.play();
        } else {
            if (!healSoundEffect.isPlaying()) {
                PokemonUtils.healParty(screen.getPlayer().getPokemonParty());
                screen.playBgm();
                playedJingle = false;
                healSoundEffect.setPosition(0f);
                screen.setEvent(nextEvent);
            }
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
        this.healSoundEffect.dispose();
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
