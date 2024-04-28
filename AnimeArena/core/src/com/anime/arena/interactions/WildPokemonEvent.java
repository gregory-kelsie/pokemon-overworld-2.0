package com.anime.arena.interactions;

import com.anime.arena.animation.WildPokemonTransition;
import com.anime.arena.pokemon.Pokemon;
import com.anime.arena.screens.PlayScreen;
import com.anime.arena.screens.PokemonBattleScreen;
import com.anime.arena.screens.PokemonTestScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WildPokemonEvent extends Event {
    //private Music newBGM;
    private WildPokemonTransition wildPokemonTransition;
    private Pokemon wildPokemon;
    private String battleBackground;
    public WildPokemonEvent(PlayScreen screen, Pokemon wildPokemon, String battleBackground) {
        super(screen);
        this.wildPokemon = wildPokemon;
        this.battleBackground = battleBackground;
        //this.newBGM = Gdx.audio.newMusic(Gdx.files.internal(bgm));
        this.wildPokemonTransition = new WildPokemonTransition();
        //screen.setBgm(newBGM);
    }
    @Override
    public void setNextEvent(Event nextEvent) {
        this.nextEvent = nextEvent;
    }

    @Override
    public void update(float dt) {
        wildPokemonTransition.update(dt);
        if (wildPokemonTransition.isFinished()) {
            //screen.stopBgm();
            //screen.playMapBgm();
            screen.toggleBlackScreen();
            screen.setEvent(nextEvent);
            screen.getGame().setScreen(new PokemonBattleScreen(screen.getGame(), screen, wildPokemon, battleBackground));
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
        wildPokemonTransition.render(batch);
    }
}
