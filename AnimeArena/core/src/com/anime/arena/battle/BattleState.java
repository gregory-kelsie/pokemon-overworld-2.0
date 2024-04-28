package com.anime.arena.battle;

import com.anime.arena.battle.ui.CustomBattleTextController;
import com.anime.arena.battle.ui.UIComponent;

import java.util.ArrayList;
import java.util.List;

public class BattleState {
    protected BattleState nextState;
    protected BattleStep currentStep;
    protected BattleStateManager battleStateManager;
    protected UIComponent uiComponent;
    protected CustomBattleTextController.CustomBattleTextResult customBattleTextResult;
    public BattleState() {

    }

    public void update(float dt) {

    }

    protected List<String> createMoveMessages(String str) {
        List<String> moveMessages = new ArrayList<String>();
        moveMessages.add(str);
        return moveMessages;
    }


    public UIComponent getUiComponent() {
        return uiComponent;
    }

    public void removeUIComponent() {
        if (uiComponent != null) {
            uiComponent = null;
        }
    }

    public void updateUIComponent(float dt) {
        if (uiComponent != null) {
            uiComponent.update(dt);
        }
    }

    protected boolean finishedUpdatingUI() {
        return currentStep != null && uiComponent == null;
    }

    public void executeFaintedPokemonLogic(BattleStatePokemon faintingPokemon, BattleStatePokemon otherPokemon) {
        if (!faintingPokemon.isUser()) {
            otherPokemon.getPokemon().freeFromBinds();
            //TODO: Go to Exp Phase and end the battle
        } else {
//            if (battleStateManager.hasMorePokemon()) {
//                //
//            }
        }
    }

    public void setCustomTextResult(CustomBattleTextController.CustomBattleTextResult customBattleTextResult) {
        this.customBattleTextResult = customBattleTextResult;
    }
}
