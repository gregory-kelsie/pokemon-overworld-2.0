package com.anime.arena.battle.ui;

import com.anime.arena.battle.BattleState;
import com.anime.arena.pokemon.BattlePokemon;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class FaintAnimation implements UIComponent {

    private BattlePokemon pokemon;
    private Sprite pokemonSprite;
    private BattleState state;
    private final float FINAL_ENEMY_Y_FAINTED_POSITION = 1320.0f;
    private final float FINAL_USER_Y_FAINTED_POSITION = 910.0f;
    private float finalFaintedPosition;
    public FaintAnimation(BattleState state, BattlePokemon pokemon, boolean isUser) {
        this.pokemon = pokemon;
        this.pokemonSprite = pokemon.getSprite();
        this.state = state;
        this.finalFaintedPosition = isUser ? FINAL_USER_Y_FAINTED_POSITION : FINAL_ENEMY_Y_FAINTED_POSITION;
    }
    @Override
    public void update(float dt) {
        if (!isFinished()) {
            componentUpdate(dt);
        } else {
            returnToBattleState();
        }
    }

    @Override
    public void render(Batch batch) {

    }

    @Override
    public void componentUpdate(float dt) {
        float newY = pokemonSprite.getY() - (dt * 1000);
        pokemonSprite.setY(Math.max(finalFaintedPosition, newY));
    }

    @Override
    public boolean isFinished() {
        return pokemonSprite.getY() == finalFaintedPosition;
    }

    @Override
    public void returnToBattleState() {
        pokemon.setVisibility(false);
        state.removeUIComponent();
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
