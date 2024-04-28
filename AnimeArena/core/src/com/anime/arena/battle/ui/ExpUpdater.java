package com.anime.arena.battle.ui;

import com.anime.arena.battle.BattleState;
import com.anime.arena.pokemon.BattlePokemon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;

public class ExpUpdater implements UIComponent {
    private BattlePokemon pokemon;
    private double rate; //exp per second
    private double expToDistribute;
    private int expToNextLevel;
    private BattleState currentState;
    public ExpUpdater(BattleState currentState, BattlePokemon pokemon, double expToDistribute, int expToNextLevel) {
        Gdx.app.log("ExpUpdater", "Entered ExpUpdater, expToDistribute: " + expToDistribute + ", expToNextLevel: " + expToNextLevel);
        this.currentState = currentState;
        this.pokemon = pokemon;
        this.expToDistribute = expToDistribute;
        this.expToNextLevel = expToNextLevel;
        this.rate = expToNextLevel / 1.5; //Exp per second
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
        if (pokemon.getCurrentExp() < expToNextLevel) {
            addExp(dt);
        } else {
            pokemon.setExp(expToNextLevel);
        }
    }

    public void returnToBattleState() {
        currentState.removeUIComponent();
    }

    public boolean isFinished() {
        return expToDistribute == 0 || pokemon.getCurrentExp() == expToNextLevel;
    }

    private void addExp(float dt) {
        double expAmount = rate * (dt);
        expAmount = Math.min(expAmount, expToDistribute);
        Gdx.app.log("ExpUpdater", "Adding Exp: " + expAmount);
        pokemon.addExp(expAmount);
        expToDistribute =  Math.max(0, expToDistribute - expAmount);
        Gdx.app.log("ExpUpdater", "Exp to distribute: " + expToDistribute);
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
