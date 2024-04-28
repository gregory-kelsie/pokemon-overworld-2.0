package com.anime.arena.battle.ui;

import com.anime.arena.battle.BattleState;
import com.anime.arena.pokemon.BattlePokemon;
import com.badlogic.gdx.graphics.g2d.Batch;

public class PauseUpdater  implements UIComponent {
    private float pauseDuration;
    private BattleState currentState;
    private float elapsedTime;
    public PauseUpdater(BattleState currentState, float pauseDuration) {
        this.currentState = currentState;
        this.pauseDuration = pauseDuration;
        this.elapsedTime = 0.0f;
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
       elapsedTime += dt;
    }

    public void returnToBattleState() {
        currentState.removeUIComponent();
    }

    public boolean isFinished() {
        return elapsedTime >= pauseDuration;
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

