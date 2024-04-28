package com.anime.arena.battle;

import com.anime.arena.battle.ui.*;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.skill.Skill;
import com.badlogic.gdx.Gdx;

import java.util.List;
import java.util.Map;

import static com.anime.arena.battle.BattleStep.*;

public class ExpState extends BattleState {

    private final double WILD_MODIFIER = 1.0;
    private final double TRAINER_MODIFIER = 1.5;

    private boolean isFirstMove;
    private BattleStateManager bsm;
    private BattlePokemon expReceiver;
    private long gainedExp;
    private long remainingExpToDistribute;
    private List<Integer> currentLevelMoves;
    private Skill learningMove;
    private int forgottenMoveIndex;
    private int currentLevelMovesIndex; //If we learn two moves at one level, we need to keep track of which move we're learning. 0 for first move, 1 for second move etc
    private boolean choseToReplaceMove; //When saying yes I want to replace a move. Set before going to the move screen
    public ExpState(BattleStateManager bsm, BattlePokemon expReceiver, BattlePokemon faintedPokemon, boolean isFirstMove) {
        this.isFirstMove = isFirstMove;
        this.bsm = bsm;
        this.expReceiver = expReceiver;
        currentStep = BattleStep.DISPLAY_EXP_GAINED_TEXT;
        double battleTypeModifier = bsm.isWildBattle() ? WILD_MODIFIER : TRAINER_MODIFIER;
        this.gainedExp = faintedPokemon.calculateExp(1, battleTypeModifier);
        this.remainingExpToDistribute = gainedExp;
        this.currentLevelMovesIndex = 0;
        this.choseToReplaceMove = false;
        BattleTextBoxComponent btc = new BattleTextBoxComponent(this, createMoveMessages(expReceiver.getName() + " has gained " + gainedExp + " Experience\nPoints!"));
        btc.setFinishType(BattleTextBox.BattleTextBoxFinish.TRIGGER);
        uiComponent = btc;
    }

    @Override
    public void update(float dt) {
        if (uiComponent != null) {
            uiComponent.update(dt);
        }

        if (finishedUpdatingUI()) {
            if (currentStep == BattleStep.DISPLAY_EXP_GAINED_TEXT) {
                currentStep = BattleStep.UPDATE_EXP_BAR;
                int expToNextLevel = expReceiver.getPokemon().getNextLevelExp();
                int remainingExpToNextLevel = expToNextLevel - (int) Math.round(expReceiver.getCurrentExp());
                ExpUpdater expUpdater = new ExpUpdater(this, expReceiver, remainingExpToDistribute, expToNextLevel);
                uiComponent = expUpdater;
                if (remainingExpToDistribute >= remainingExpToNextLevel) {
                    remainingExpToDistribute -= remainingExpToNextLevel;
                } else {
                    remainingExpToDistribute = 0;
                }

            } else if (currentStep == BattleStep.UPDATE_EXP_BAR) {
                if (remainingExpToDistribute > 0) {
                    expReceiver.levelUp();
                    BattleTextBoxComponent btc = new BattleTextBoxComponent(this, createMoveMessages(expReceiver.getName() + " leveled up!"));
                    btc.setFinishType(BattleTextBox.BattleTextBoxFinish.TRIGGER);
                    uiComponent = btc;
                    int currentLevel = expReceiver.getPokemon().getUniqueVariables().getLevel();
                    Map<Integer, List<Integer>> levelUpMoves = expReceiver.getPokemon().getConstantVariables().getLevelUpMoves();
                    if (levelUpMoves.containsKey(currentLevel)) {
                        currentLevelMoves = levelUpMoves.get(currentLevel);
                        currentLevelMovesIndex = 0;
                        learningMove = bsm.getPokemonFactory().getMove(currentLevelMoves.get(currentLevelMovesIndex));
                        currentStep = BattleStep.CHECK_NEW_MOVES;
                    } else {
                        currentStep = BattleStep.DISPLAY_EXP_GAINED_TEXT;
                    }
                } else {
                    //TODO: Distribute exp to other pokemon, level them up if any level, check if learned move, exit when all are levelled up and complete
                    if (bsm.isWildBattle()) {
                        bsm.setExitingBattle(true);
                    }
                    //TODO: If trainer battle check to see if they have another pokemon. Finish turn if they have more pokemon, go to end trainer battle phase when no more pokemon
                }
            } else if (currentStep == BattleStep.CHECK_NEW_MOVES) {
                if (expReceiver.getPokemon().getUniqueVariables().getMoves().size() == 4) {
                    CustomBattleText t = createNextMoveText();
                    CustomBattleTextController ctr = new CustomBattleTextController(this, t);
                    uiComponent = ctr;
                    currentStep = EVALUATE_NEW_MOVE_RESULT;
                } else {
                    BattleTextBoxComponent btc = new BattleTextBoxComponent(this, createMoveMessages(expReceiver.getName() + " learned " + learningMove.getName() + "!"));
                    btc.setFinishType(BattleTextBox.BattleTextBoxFinish.TRIGGER);
                    uiComponent = btc;
                    currentLevelMovesIndex++;
                    expReceiver.getPokemon().getUniqueVariables().getMoves().add(learningMove);
                    if (currentLevelMoves.size() > currentLevelMovesIndex) {
                        currentStep = CHECK_NEW_MOVES;
                    } else {
                        currentStep = BattleStep.DISPLAY_EXP_GAINED_TEXT;
                    }
                }
            } else if (currentStep == EVALUATE_NEW_MOVE_RESULT) {
                if (customBattleTextResult == CustomBattleTextController.CustomBattleTextResult.STOPPED_LEARNING_MOVE) {
                    currentStep = BattleStep.DISPLAY_EXP_GAINED_TEXT; //continue gaining exp
                } else if (customBattleTextResult == CustomBattleTextController.CustomBattleTextResult.OPEN_MOVE_LEARNER) {
                    bsm.openLearningMoveScreen(expReceiver.getPokemon(), learningMove, this);
                }
                customBattleTextResult = null; //reset it
            } else if (currentStep == LEARN_NEW_MOVE) {
                CustomBattleText t = createLearnedMoveText();
                CustomBattleTextController ctr = new CustomBattleTextController(this, t);
                expReceiver.getPokemon().getUniqueVariables().getMoves().set(forgottenMoveIndex, learningMove);
                uiComponent = ctr;
                currentStep = BattleStep.DISPLAY_EXP_GAINED_TEXT;
            }
        }
    }

    /**
     * Set the next battle step in the ExpState.
     * This method is used by the LearnMoveScreen so that we know what step to go to when the learn move screen closes.
     * @param nextStep
     */
    public void setBattleStep(BattleStep nextStep) {
        currentStep = nextStep;
    }

    /**
     * Set the forgottenMoveIndex.
     * This method is used by the LearnMoveScreen to send the index of the move we are planning to replace.
     * @param forgottenMoveIndex
     */
    public void setForgottenMoveIndex(int forgottenMoveIndex) {
        this.forgottenMoveIndex = forgottenMoveIndex;
    }

    private CustomBattleText createNextMoveText() {
        CustomBattleText tryingToLearnText = new CustomBattleText(getTryingToLearnText());
        CustomBattleText moreThanFourMovesText = new CustomBattleText(getMoreThanFourMovesText());
        tryingToLearnText.setNextBattleText(moreThanFourMovesText);
        CustomBattleText deleteMoveText = new CustomBattleText(getDeleteMoveText(), true);
        moreThanFourMovesText.setNextBattleText(deleteMoveText);

        CustomBattleText stopLearningText = new CustomBattleText(getStopLearningText(), true);
        CustomBattleText didNotLearnText = new CustomBattleText(getDidNotLearnText());
        stopLearningText.setNextBattleTextForYes(didNotLearnText);
        stopLearningText.setNextBattleTextForNo(tryingToLearnText);
        didNotLearnText.setDefaultResult(CustomBattleTextController.CustomBattleTextResult.STOPPED_LEARNING_MOVE);

        deleteMoveText.setYesResult(CustomBattleTextController.CustomBattleTextResult.OPEN_MOVE_LEARNER);
        deleteMoveText.setNextBattleTextForNo(stopLearningText);
        return tryingToLearnText;
    }

    private CustomBattleText createLearnedMoveText() {
        CustomBattleText poofText = new CustomBattleText(getPoofText());
        CustomBattleText forgotText = new CustomBattleText(getForgotText());
        CustomBattleText andText = new CustomBattleText(getAndText());
        CustomBattleText learnedMoveText = new CustomBattleText(getLearnedMoveText());

        poofText.setNextBattleText(forgotText);
        forgotText.setNextBattleText(andText);
        andText.setNextBattleText(learnedMoveText);
        return poofText;
    }

    private String getTryingToLearnText() {
        return expReceiver.getName() + " is trying to\nlearn " + learningMove.getName();
    }

    private String getMoreThanFourMovesText() {
        return "But, " + expReceiver.getName() + " can't learn\nmore than four moves.";
    }

    private String getDeleteMoveText() {
        return "Delete a move to make\nroom for " + learningMove.getName() + "?";
    }

    private String getStopLearningText() {
        return "Stop learning\n" + learningMove.getName() + "?";
    }

    private String getDidNotLearnText() {
        return expReceiver.getName() + " did not learn\n" + learningMove.getName() + ".";
    }

    private String getPoofText() {
        return "1, 2, and... ... ... Poof!";
    }

    private String getForgotText() {
        return expReceiver.getName() + " forgot\n" + expReceiver.getPokemon().getUniqueVariables().getMoves().get(forgottenMoveIndex).getName() + ".";
    }

    private String getAndText() {
        return "And...";
    }

    private String getLearnedMoveText() {
        return expReceiver.getName() + " learned\n" + learningMove.getName() + "!";
    }

    public void setChoseToReplaceMove(boolean choseToReplaceMove) {
        this.choseToReplaceMove = choseToReplaceMove;
    }
}
