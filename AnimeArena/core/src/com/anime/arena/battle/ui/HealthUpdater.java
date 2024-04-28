package com.anime.arena.battle.ui;

import com.anime.arena.battle.BattleState;
import com.anime.arena.pokemon.BattlePokemon;
import com.badlogic.gdx.graphics.g2d.Batch;

public class HealthUpdater implements UIComponent {
    private BattlePokemon pokemon;
    private double rate; //health per second
    private BattleState currentState;
    public HealthUpdater(BattleState currentState, BattlePokemon pokemon) {
        this.currentState = currentState;
        this.pokemon = pokemon;
        this.rate = pokemon.getPokemon().getHealthStat() / 1.0;
    }

    public void update(float dt) {
        if (!isFinished()) {
            componentUpdate(dt);
        } else {
            returnToBattleState();
        }
    }

    public void render(Batch batch) {

    }

    public void componentUpdate(float dt) {
        if (pokemon.getAnimationHealth() > pokemon.getCurrentHealth()) {
            decreaseHealth(dt);
        } else if (pokemon.getAnimationHealth() < pokemon.getCurrentHealth()) {
            increaseHealth(dt);
        }
    }

    public void returnToBattleState() {
        currentState.removeUIComponent();
    }

    public boolean isFinished() {
        return pokemon.getAnimationHealth() == pokemon.getCurrentHealth();
    }

    private void decreaseHealth(float dt) {
        double decreaseAmount = rate * (dt);
        pokemon.subtractAnimationHealth(decreaseAmount);
    }

    private void increaseHealth(float dt) {
        double increaseAmount = rate * dt;
        pokemon.addAnimationHealth(increaseAmount);
    }

    @Override
    public void click() {

    }

    @Override
    public void clickUp() {

    }

    @Override
    public void clickDown() {

    }
}
