package com.anime.arena.interactions;

import com.anime.arena.animation.WildPokemonTransition;
import com.anime.arena.pokemon.Pokemon;
import com.anime.arena.pokemon.PokemonUtils;
import com.anime.arena.screens.PlayScreen;
import com.anime.arena.screens.PokemonBattleScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WildPokemonEvent extends Event {
    private WildPokemonTransition wildPokemonTransition;
    private Pokemon wildPokemon;
    private String battleBackground;
    public WildPokemonEvent(PlayScreen screen, Pokemon wildPokemon, String battleBackground) {
        super(screen);
        this.wildPokemon = wildPokemon;
        this.battleBackground = battleBackground;
        this.wildPokemonTransition = new WildPokemonTransition();
    }
    @Override
    public void setNextEvent(Event nextEvent) {
        this.nextEvent = nextEvent;
    }

    @Override
    public void update(float dt) {
        wildPokemonTransition.update(dt);
        if (wildPokemonTransition.isFinished()) {
            resetWildPokemonEventValues();
            screen.toggleBlackScreen();
            screen.setEvent(nextEvent);
            screen.getGame().setScreen(new PokemonBattleScreen(screen.getGame(), screen, wildPokemon, battleBackground));
        }
    }

    /**
     * Reset the wild pokemon event values in the case that this WildPokemonEvent is reused after.
     * We don't want the Pokemon to have leftover hp and the animation complete upon retriggering the event.
     */
    private void resetWildPokemonEventValues() {
        wildPokemonTransition.resetAnimationVariables();
        PokemonUtils.maxHealPokemon(wildPokemon);
        PokemonUtils.recoverStatus(wildPokemon);
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
