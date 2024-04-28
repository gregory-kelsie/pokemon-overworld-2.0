package com.anime.arena.battle;

import com.anime.arena.battle.ui.BattleTextBoxComponent;
import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;
import com.anime.arena.pokemon.PokemonUtils;
import com.anime.arena.skill.DamageSkill;
import com.anime.arena.skill.EffectSkill;
import com.anime.arena.skill.Skill;
import com.anime.arena.skill.effect.BurnEffect;
import com.anime.arena.skill.effect.Effect;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class MoveFailState extends BattleState {
    private BattleStatePokemon attackingPokemon;
    private BattleStatePokemon defendingPokemon;
    private SubField attackerField;
    private SubField defenderField;
    private boolean isFirstMove;

    public MoveFailState(BattleStateManager battleStateManager, boolean isFirstMove) {
        this.isFirstMove = isFirstMove;
        this.battleStateManager = battleStateManager;
        this.attackingPokemon = isFirstMove ? battleStateManager.getFirstAttacker() : battleStateManager.getSecondAttacker();
        this.defendingPokemon = isFirstMove ? battleStateManager.getSecondAttacker() : battleStateManager.getFirstAttacker();
        initSubFields();
    }

    private void battleLog(String str) {
        Gdx.app.log("MoveFailState", str);
    }

    public void update(float dt) {
        if (currentStep == null) {
            currentStep = BattleStep.CHECK_MOVE_FAIL;
            List<String> usedMoveText = new ArrayList<String>();
            usedMoveText.add(attackingPokemon.getPokemon().getName() + " used\n" + attackingPokemon.getSkill().getName() + "!");
            uiComponent = new BattleTextBoxComponent(this,usedMoveText);
        }
        if (finishedUpdatingUI()) {
            if (currentStep == BattleStep.CHECK_MOVE_FAIL) {
                boolean isMoveFailing = willMoveFail();
                if (isMoveFailing) {
                    currentStep = BattleStep.DISPLAY_FAILED_MOVE;
                    List<String> failedText = new ArrayList<String>();
                    failedText.add("It failed...");
                    uiComponent = new BattleTextBoxComponent(this, failedText);
                } else {
                    battleLog("PASSED MOVE FAIL CHECK - GOING TO USE SKILL");
                    battleStateManager.setState(new UseSkillState(battleStateManager, isFirstMove));
                }
            } else if (currentStep == BattleStep.DISPLAY_FAILED_MOVE) {
                if (isFirstMove) {
                    battleLog("MOVE FAILED - GOING TO SLEEP CHECK FOR SECOND ATTACKER");
                    battleStateManager.setState(new SleepState(battleStateManager, false));
                } else {
                    battleLog("MOVE FAILED - GOING TO END TURN STATE");
                    battleStateManager.setState(new EndTurnState2(battleStateManager));
                }
            }
        }
        if (uiComponent != null) {
            uiComponent.update(dt);
        }
    }

    private void initSubFields() {
        if (attackingPokemon.isUser()) {
            attackerField = battleStateManager.getField().getPlayerField();
            defenderField = battleStateManager.getField().getOpponentField();
        } else {
            attackerField = battleStateManager.getField().getOpponentField();
            defenderField = battleStateManager.getField().getPlayerField();
        }
    }

    private boolean requiresFirstMove(Skill attackingMove) {
        if (attackingMove.requiresFirstMove() && !isFirstMove) {
            battleLog(attackingMove.getName() + " FAILED BECAUSE IT MUST BE THE FIRST ATTACK");
            return true;
        }
        return false;
    }

    private boolean requiresBelowFullHealth(Skill attackingMove) {
        if (attackingMove.isSingleRecoveryMove() && PokemonUtils.isFullHealth(attackingPokemon.getPokemon().getPokemon())) {
            battleLog(attackingMove.getName() + " FAILED BECAUSE THE ATTACKING POKEMON IS FULL HEALTH.");
            return true;
        }
        return false;
    }

    private boolean isPowderMoveFailing(Skill attackingMove) {
        if (attackingMove.isPowderMove()) {
            if (defendingPokemon.getPokemon().isPowdered()) {
                battleLog(attackingMove.getName() + " FAILED BECAUSE THE DEFENDING POKEMON IS ALREADY POWDERED");
                return true;
            } else if (defendingPokemon.getPokemon().getFirstType()== PokemonType.GRASS ||
                    defendingPokemon.getPokemon().getSecondType() == PokemonType.GRASS ||
                    defendingPokemon.getPokemon().getAbility() == AbilityId.OVERCOAT) {
                battleLog(attackingMove.getName() + " FAILED BECAUSE THE DEFENDING POKEMON IS GRASS TYPE OR HAS THE ABILITY OVERCOAT");
                return true;
            }
        }
        return false;
    }

    private boolean isSpiderWebMoveFailing(Skill attackingMove) {
        if (attackingMove.isSpiderWebMove()) {
            if (defendingPokemon.getPokemon().isSpiderWebbed()) {
                battleLog(attackingMove.getName() + " FAILED BECAUSE THE DEFENDING POKEMON IS ALREADY SPIDER WEBBED.");
                return true;
            } else if (defendingPokemon.getPokemon().getFirstType()== PokemonType.GHOST ||
                    defendingPokemon.getPokemon().getSecondType() == PokemonType.GHOST) {
                battleLog(attackingMove.getName() + " FAILED BECAUSE THE DEFENDING POKEMON IS GHOST TYPE");
                return true;
            }
        }
        return false;
    }

    private boolean isRequiringDamageEnemyMove(Skill attackingMove) {
        if (attackingMove.requiresEnemyDamageAttack()) {
            if (!defendingPokemon.getSkill().damagesEnemy()) {
                battleLog(attackingMove.getName() + " FAILED BECAUSE THE DEFENDING POKEMON'S MOVE DOES NOT DEAL DAMAGE (damagesEnemy=false)");
                return true;
            }
        }
        return false;
    }

    private boolean isStickyWebMoveFailing(Skill attackingMove) {
        if (attackingMove.isStickyWeb()) {
            if (defenderField.hasStickyWeb()) {
                battleLog(attackingMove.getName() + " FAILED BECAUSE THE DEFENDER'S FIELD IS ALREADY STICKY WEBBED");
                return true;
            }
        }
        return false;
    }

    private boolean isSleepRequiringMove(Skill attackingMove) {
        if (attackingMove.requiresSleep()) {
            if (!defendingPokemon.getPokemon().isSleeping()) {
                battleLog(attackingMove.getName() + " FAILED BECAUSE IT REQUIRES THE DEFENDING POKEMON TO BE SLEEPING");
                return true;
            }
            if (attackingMove.requiresNoNightmares() && defendingPokemon.getPokemon().hasNightmares()) {
                battleLog(attackingMove.getName() + " FAILED BECAUSE THE DEFENDING POKEMON ALREADY HAS NIGHTMARES");
                return true;
            }
        }
        return false;
    }

    private boolean isNonGrassDefenderMove(Skill attackingMove) {
        if (attackingMove.requiresNonGrassDefender() &&
                (defendingPokemon.getPokemon().getFirstType() == PokemonType.GRASS || defendingPokemon.getPokemon().getSecondType() == PokemonType.GRASS)) {
            battleLog(attackingMove.getName() + " FAILED BECAUSE THE DEFENDING POKEMON MUST NOT BE GRASS TYPE");
            return true;
        }
        return false;
    }

    private boolean isLeechSeedMoveFailing(Skill attackingMove) {
        if (attackingMove.isLeechSeedMove() && defendingPokemon.getPokemon().isSeeded()) {
            battleLog(attackingMove.getName() + " FAILED BECAUSE THE DEFENDING POKEMON IS ALREADY SEEDED");
            return true;
        }
        return false;
    }

    private boolean requiresEnemyTarget(Skill attackingMove) {
        return attackingMove.isTargetingEnemy();
    }

    private boolean willMoveFail() {
        Skill attackingMove = attackingPokemon.getSkill();
        if (defendingPokemon.getPokemon().hasFainted() && requiresEnemyTarget(attackingMove)) { return true; }
        if (requiresFirstMove(attackingMove)) { return true; }
        if (requiresBelowFullHealth(attackingMove)) { return true; }
        if (isPowderMoveFailing(attackingMove)) { return true; }
        if (isSpiderWebMoveFailing(attackingMove)) { return true; }
        if (isStickyWebMoveFailing(attackingMove)) { return true; }
        if (isRequiringDamageEnemyMove(attackingMove)) { return true; }
        if (isSleepRequiringMove(attackingMove)) { return true; }
        if (isNonGrassDefenderMove(attackingMove)) { return true; }
        if (isLeechSeedMoveFailing(attackingMove)) { return true; }

        if (attackingMove.getSubtype() == 5) {
            EffectSkill effectSkill = (EffectSkill) attackingMove;
            if (!PokemonUtils.isStatusFree(defendingPokemon.getPokemon().getPokemon())) {
                return true;
            }
        }

        if (!(attackingMove instanceof DamageSkill) && attackingMove instanceof EffectSkill) {
            EffectSkill effectSkill = (EffectSkill) attackingMove;
            if (effectSkill.hasOneEffect() && effectSkill.getEffects().get(0).willFailAsSingleEffect(attackingPokemon.getPokemon(), defendingPokemon.getPokemon(),
                            battleStateManager.getField(), attackerField, defenderField, isFirstMove)) {
                return true;

            }
        }

        return false;
    }
}
