package com.anime.arena.battle;

import com.anime.arena.battle.ui.BattleTextBoxComponent;
import com.anime.arena.field.SubField;

import java.util.List;

public class FaintState extends BattleState {
    private BattleStatePokemon attackingPokemon;
    private BattleStatePokemon defendingPokemon;
    private SubField attackerField;
    private SubField defenderField;
    private boolean isFirstMove;
    private boolean isWildBattle;
    private boolean isTrainerBattle;

    private boolean isDoubleKnockout;
    private boolean isFirstAttackerKnockedOut;
    private boolean isSecondAttackerKnockedOut;

    /**
     * Faint Steps
     * 1. Pokemon Cry (super effective is still on the screen)
     * 2. Pokemon Animation to go down
     * 3. Pokemon disappears
     * 4. Text saying: The wild (opposing for trainer) Tentacruel\nfainted! or Gyarados\nfainted! for user pokemon and the text box is a trigger
     * 6. If user pokemon faints check to see if we have another Pokemon (If we do and it's a wild battle display Use next Pokemon? with a Yes No Box
     * 6.5 If it's a trainer battle go to pokemon screen
     * 7. If No then display Got away safely! Trigger Box
     * 5. ExpState
     */

    //Double Knockout from explosion - Faint defending pokemon
    //Electivire\nfainted! trigger text box
    //Drop electivire animation
    //Decrease Ferrothorn hp
    //Drop Ferrothorn
    //The opposing Ferrothorn\nfainted!

    public FaintState(BattleStateManager battleStateManager, boolean isFirstMove) {
        this.isFirstMove = isFirstMove;
        this.battleStateManager = battleStateManager;
        this.attackingPokemon = isFirstMove ? battleStateManager.getFirstAttacker() : battleStateManager.getSecondAttacker();
        this.defendingPokemon = isFirstMove ? battleStateManager.getSecondAttacker() : battleStateManager.getFirstAttacker();

        this.isDoubleKnockout = attackingPokemon.getPokemon().getPokemon().isFainted() && defendingPokemon.getPokemon().getPokemon().isFainted();
        this.isFirstAttackerKnockedOut = battleStateManager.getFirstAttacker().getPokemon().getPokemon().isFainted();
        this.isSecondAttackerKnockedOut = battleStateManager.getSecondAttacker().getPokemon().getPokemon().isFainted();
        if (isFirstMove) {
            uiComponent = new BattleTextBoxComponent(this, battleStateManager.getFirstAttacker().getPokemon().getName() + " fainted!");
        } else {
            uiComponent = new BattleTextBoxComponent(this, battleStateManager.getSecondAttacker().getPokemon().getName() + " fainted!");
        }

    }

    public void update(float dt) {
        if (currentStep == null) {

        }
        if (uiComponent != null) {
            uiComponent.update(dt);
        }

        if (finishedUpdatingUI()) {

        }
    }
}
