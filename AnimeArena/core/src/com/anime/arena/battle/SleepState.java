package com.anime.arena.battle;

import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.StatusCondition;
import com.anime.arena.skill.Skill;

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
    }

    public void update(float dt) {
        if (isDoneSleeping()) {
            executeWakingUp();
        } else if (isSleeping()) {
            updateSleeping();
        } else if (sleepingPokemon.isRecharging()) {
            updateRecharging();
        } else {
            goToConfusionCheck();
        }
    }

    private void executeWakingUp() {
        sleepingPokemon.wakeUp();
        if (sleepingPokemon.isRecharging()) {
            sleepingPokemon.recharge();
            skipPendingAttack();
        } else {
            goToConfusionCheck();
        }
    }

    private void updateSleeping() {
        sleepingPokemon.reduceSleepTime();
        if (sleepingPokemon.isRecharging()) {
            sleepingPokemon.recharge();
            skipPendingAttack();
        } else {
            if (skillIsUsableInSleep()) {
                goToConfusionCheck();
            } else {
                skipPendingAttack();
            }
        }
    }

    private void updateRecharging() {
        sleepingPokemon.recharge();
        skipPendingAttack();
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
        if (isFirstMove) {

        }
    }

    /**
     * Skip the current attack of this sleeping pokemon and start the next SleepState if there's another attack or
     * start the EndTurnState
     */
    private void skipPendingAttack() {
        if (isFirstMove) {
            battleStateManager.setState(new SleepState(battleStateManager, false));
        } else {
            battleStateManager.setState(new EndTurnState(battleStateManager));
        }
    }

    private boolean isDoneSleeping() {
        return sleepingPokemon.getPokemon().getUniqueVariables().getStatus() == StatusCondition.SLEEP &&
                sleepingPokemon.getSleepTime() == 0;
    }

    private boolean isSleeping() {
        return sleepingPokemon.getPokemon().getUniqueVariables().getStatus()== StatusCondition.SLEEP &&
                sleepingPokemon.getSleepTime() > 0;
    }
}
