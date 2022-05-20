package com.anime.arena.battle;

public class ConfusionState extends BattleState {
    private boolean isFirstMove;
    private static final float CONFUSION_RATE = 0.33f;
    private BattleStatePokemon confusedPokemon;
    public ConfusionState(BattleStateManager battleStateManager, boolean isFirstMove) {
        this.isFirstMove = isFirstMove;
        this.battleStateManager = battleStateManager;
        this.confusedPokemon = isFirstMove ? battleStateManager.getFirstAttacker() : battleStateManager.getSecondAttacker();
    }

    public void update(float dt) {
        if (confusedPokemon.getPokemon().isConfused()) {
            if (failedConfusionCheck()) {

            } else {
                //battleStateManager.setState(new ParalysisState(battleStateManager, isFirstMove));
            }
        } else {
            //battleStateManager.setState(new ParalysisState(battleStateManager, isFirstMove));
        }
    }

    /**
     * Check if the confused pokemon will hit himself of not.
     */
    private boolean failedConfusionCheck() {
        double rand = Math.random();
        if (rand <= CONFUSION_RATE) {
            return true;
        } else {
            return false;
        }
    }

}
