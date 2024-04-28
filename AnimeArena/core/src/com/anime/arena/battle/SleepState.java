package com.anime.arena.battle;

import com.anime.arena.battle.ui.BattleTextBoxComponent;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.StatusCondition;
import com.anime.arena.skill.Skill;
import com.badlogic.gdx.Gdx;

public class SleepState extends BattleState {
    private boolean isFirstMove;
    private BattlePokemon sleepingPokemon;
    private Skill sleepingSkill;
    public SleepState(BattleStateManager battleStateManager, boolean isFirstMove) {
        this.battleStateManager = battleStateManager;
        this.isFirstMove = isFirstMove;
        this.sleepingPokemon = isFirstMove ? battleStateManager.getFirstAttacker().getPokemon()
                : battleStateManager.getSecondAttacker().getPokemon();
        this.sleepingSkill = isFirstMove ? battleStateManager.getFirstAttacker().getSkill()
                : battleStateManager.getSecondAttacker().getSkill();
        setInitialStep();
        logSleepStatus();
    }

    private void setInitialStep() {
        if (isDoneSleeping()) {
            currentStep = BattleStep.DONE_SLEEPING;
            uiComponent = new BattleTextBoxComponent(this, createMoveMessages(sleepingPokemon.getName() + " woke up!"));
        } else if (isSleeping()) {
            currentStep = BattleStep.STILL_SLEEPING;
            uiComponent = new BattleTextBoxComponent(this, createMoveMessages(sleepingPokemon.getName() + " is fast asleep."));
        } else if (sleepingPokemon.isRecharging()) {
            currentStep = BattleStep.RECHARGING;
            uiComponent = new BattleTextBoxComponent(this, createMoveMessages(sleepingPokemon.getName() + " needs to recharge."));
        } else {
            currentStep = BattleStep.DONE_SLEEP_STATE;
        }
    }

    private void logSleepStatus() {
        boolean isSleeping = sleepingPokemon.getPokemon().getUniqueVariables().getStatus() == StatusCondition.SLEEP;
        Gdx.app.log("SleepState", "SLEEP CHECK FOR: " + sleepingPokemon.getName());
        Gdx.app.log("SleepState", "IS SLEEPING?: " + isSleeping);
        Gdx.app.log("SleepState", "IS RECHARGING?: " + sleepingPokemon.isRecharging());
        Gdx.app.log("SleepState", "SLEEP TIME: " + sleepingPokemon.getSleepTime());
    }

    public void update(float dt) {
        if (uiComponent != null) {
            uiComponent.update(dt);
        }
        if (finishedUpdatingUI()) {
            if (currentStep == BattleStep.DONE_SLEEPING) {
                executeWakingUp();
            } else if (currentStep == BattleStep.STILL_SLEEPING) {
                updateSleeping();
            } else if (currentStep == BattleStep.RECHARGING) {
                updateRecharging();
            } else if (currentStep == BattleStep.DONE_SLEEP_STATE) {
                goToConfusionCheck();
            }else {
                Gdx.app.log("SleepState", "currentStep doesn't have a match in logic, currentStep: " + currentStep);
            }
        }
    }



    private void executeWakingUp() {
        Gdx.app.log("SleepState", "EXECUTE WAKING UP");
        sleepingPokemon.wakeUp();
        if (sleepingPokemon.isRecharging()) {
            currentStep = BattleStep.RECHARGING;
            uiComponent = new BattleTextBoxComponent(this, createMoveMessages(sleepingPokemon.getName() + " needs to recharge."));
        } else {
            goToConfusionCheck();
        }
    }

    private void updateSleeping() {
        Gdx.app.log("SleepState", "UPDATE SLEEPING VARIABLES");
        sleepingPokemon.reduceSleepTime();
        Gdx.app.log("SleepState", "UPDATED SLEEP TIME TO: " + sleepingPokemon.getSleepTime());
        if (sleepingPokemon.isRecharging()) {
            updateRecharging(); //Silently recharge. We don't need to say anything in UI
        } else {
            if (skillIsUsableInSleep()) {
                Gdx.app.log("SleepState", "SKILL IS USABLE IN SLEEP: " + sleepingSkill.getName());
                goToConfusionCheck();
            } else {
                Gdx.app.log("SleepState", "SKILL IS NOT USABLE WHILE SLEEPING: " + sleepingSkill.getName());
                skipSleepingPokemonAttack();
            }
        }
    }

    private void updateRecharging() {
        Gdx.app.log("SleepState", "UPDATED RECHARGE");
        sleepingPokemon.recharge();
        skipSleepingPokemonAttack();
    }

    /**
     * Return whether or not the skill the sleeping pokemon is using
     * can be used while they are asleep.
     * @return Whether or not the sleeping pokemon's skill is usable
     * in their sleep.
     * TODO: Change to check id when the skills are implemented.
     */
    private boolean skillIsUsableInSleep() {
        if (sleepingSkill.getName().equals("Snore") || sleepingSkill.getName().equals("Sleep Talk")) {
            return true;
        }
        return false;
    }

    /**
     * Passed the sleep check so go to the next check, the confusion
     * check.
     */
    private void goToConfusionCheck() {
        Gdx.app.log("SleepState", "PASSED SLEEP CHECK - GOING TO CONFUSION CHECK");
        battleStateManager.setState(new ConfusionState(battleStateManager, isFirstMove));
    }

    /**
     * Skip the current attack of this sleeping pokemon and start the next SleepState if there's another attack or
     * start the EndTurnState
     */
    private void skipSleepingPokemonAttack() {
        if (isFirstMove) {
            battleStateManager.setState(new SleepState(battleStateManager, false));
        } else {
            battleStateManager.setState(new EndTurnState2(battleStateManager));
        }
    }

    private boolean isDoneSleeping() {
        return sleepingPokemon.getPokemon().getUniqueVariables().getStatus() == StatusCondition.SLEEP &&
                sleepingPokemon.getSleepTime() == 0;
    }

    private boolean isSleeping() {
        return sleepingPokemon.getPokemon().getUniqueVariables().getStatus() == StatusCondition.SLEEP &&
                sleepingPokemon.getSleepTime() > 0;
    }
}
