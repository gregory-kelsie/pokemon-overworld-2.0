package com.anime.arena.battle.ui;

import com.anime.arena.battle.BattleState;
import com.anime.arena.battle.BattleStep;
import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * UI Components for the battle
 * Animation Health updating, Fainting Pokemon Animation, Flashing Pokemon after taking damage
 */
public interface UIComponent {


    public void update(float dt);
    public void render(Batch batch);
    public void componentUpdate(float dt);

    public boolean isFinished();

    public void returnToBattleState();
    public void click();
    public void clickUp();
    public void clickDown();




}
