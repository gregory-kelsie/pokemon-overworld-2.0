package com.anime.arena.battle;

import com.anime.arena.battle.ui.BattleTextBoxComponent;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.PokemonType;
import com.anime.arena.pokemon.PokemonUtils;
import com.anime.arena.pokemon.StanceForme;
import com.anime.arena.skill.EffectSkill;
import com.anime.arena.skill.Skill;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class UseSkillState extends BattleState {
    private BattleStatePokemon attackingPokemon;
    private BattleStatePokemon defendingPokemon;
    private SubField attackerField;
    private SubField defenderField;
    private boolean isFirstMove;

    public UseSkillState(BattleStateManager battleStateManager, boolean isFirstMove) {
        this.isFirstMove = isFirstMove;
        this.battleStateManager = battleStateManager;
        this.attackingPokemon = isFirstMove ? battleStateManager.getFirstAttacker() : battleStateManager.getSecondAttacker();
        this.defendingPokemon = isFirstMove ? battleStateManager.getSecondAttacker() : battleStateManager.getFirstAttacker();
        initSubFields();
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

    private void battleLog(String str) {
        Gdx.app.log("UseSkillState", str);
    }

    public void update(float dt) {
        if (currentStep == null) {
            updateStanceChange();


            Skill attackingSkill = attackingPokemon.getSkill();
            if (!attackingSkill.damagesEnemy() || attackingSkill.isContinuingThroughNoEffect() || isDamagingTheReceivingPokemon(attackingSkill)) {
                if (!isMoveMissing(attackingSkill)) {
                    battleStateManager.setState(new AbsorbSkillState(battleStateManager, isFirstMove));
                } else {
                    currentStep = BattleStep.MISSED;
                    List<String> textboxes = new ArrayList<String>();
                    textboxes.add("It missed...");
                    uiComponent = new BattleTextBoxComponent(this, textboxes);
                }
            } else {
                currentStep = BattleStep.NO_EFFECT;
                List<String> textboxes = new ArrayList<String>();
                textboxes.add("It failed...");
                uiComponent = new BattleTextBoxComponent(this, textboxes);
            }
        }

        if (uiComponent != null) {
            uiComponent.update(dt);
        }

        if (finishedUpdatingUI()) {
            if (currentStep == BattleStep.MISSED) {
                skipExecutingAttack("MISSED");
            } else if (currentStep == BattleStep.NO_EFFECT) {
                skipExecutingAttack("HAD NO EFFECT");
            }
        }
    }

    private void skipExecutingAttack(String skipReason) {
        if (isFirstMove) {
            battleLog("ATTACK " + skipReason + " - GOING TO SECOND ATTACKER'S SLEEP CHECK");
            battleStateManager.setState(new SleepState(battleStateManager, false));
        } else {
            battleLog("ATTACK " + skipReason + "  - GOING TO END TURN STATE");
            battleStateManager.setState(new EndTurnState2(battleStateManager));
        }
    }

    private boolean isMoveMissing(Skill attackingSkill) {
        return !attackingSkill.willHitEnemy(attackingPokemon.getPokemon(), defendingPokemon.getPokemon(), battleStateManager.getField(),
                attackerField, defenderField, isFirstMove);
    }

    private boolean isDamagingTheReceivingPokemon(Skill attackingSkill) {
        if (attackingSkill.damagesEnemy()) {
            if (defendingPokemon.getPokemon().getResistances().get(attackingSkill.getMoveType()) != 0) {
                //Check if the receiving pokemon has wonder guard
                if (defendingPokemon.getPokemon().getResistances().get(attackingSkill.getMoveType()) <= 1 &&
                        defendingPokemon.getPokemon().getAbility() == AbilityId.WONDER_GUARD) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }


    private void updateStanceChange() {
        if (attackingPokemon.getPokemon().getAbility() == AbilityId.STANCE_CHANGE) {
            if (attackingPokemon.getPokemon().getStanceForme() != StanceForme.ATTACK && attackingPokemon.getSkill().getSubtype() == 0) {
                //Change Stance to Attack
            } else if (attackingPokemon.getPokemon().getStanceForme() == StanceForme.ATTACK && attackingPokemon.getSkill().getName().equals("King's Shield")) {
                //Change Stance to Defense
            }
        }
    }
}
