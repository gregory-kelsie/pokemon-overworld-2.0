package com.anime.arena.battle;

import com.anime.arena.battle.ui.*;
import com.anime.arena.field.SubField;
import com.anime.arena.field.WeatherType;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.StatusCondition;
import com.anime.arena.skill.Skill;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

import static com.anime.arena.battle.BattleStep.ENEMY_FAINTED;
import static com.anime.arena.battle.BattleStep.ENEMY_FAINT_TEXT;

public class ExecuteSkillState extends BattleState {
    private BattleStatePokemon attackingPokemon;
    private BattleStatePokemon defendingPokemon;
    private SubField attackerField;
    private SubField defenderField;
    private boolean isFirstMove;
    private BattleStatePokemon faintingPokemon;

    private List<String> moveMessages;

    public ExecuteSkillState(BattleStateManager battleStateManager, boolean isFirstMove) {
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
        Gdx.app.log("ExecuteSkillState", str);
    }


    private void useMove() {
        moveMessages = attackingPokemon.getSkill().use(attackingPokemon.getPokemon(), defendingPokemon.getPokemon(),
                0, 0, battleStateManager.getField(), attackerField,
                defenderField, isFirstMove,
                defendingPokemon.getSkill(), new ArrayList<BattlePokemon>(), new ArrayList<BattlePokemon>());
        logPokemonInfo();
        if (defendingPokemon.getPokemon().getAnimationHealth() != defendingPokemon.getPokemon().getCurrentHealth()) {
            //Update the health bars and then check the move results
            currentStep = BattleStep.CHECK_MOVE_MESSAGES;
            uiComponent = new HealthUpdater(this, defendingPokemon.getPokemon());
        } else {
            //When the move does not deal damage to the defending pokemon
            if (moveMessages != null && moveMessages.size() > 0) { //There are move results
                //Display the move text results and then check to see if the ability had contact
                currentStep = BattleStep.CHECK_ABILITY_CONTACT;
                uiComponent = new BattleTextBoxComponent(this, moveMessages);
            } else {
                checkAfterMoveEffects();
            }
        }
    }

    /**
     * Apply ability contact effects from the attacker's move
     * If there are no strikes left, apply drain / recoil
     * If no drain / recoil exit to the next step (sleep state or end turn)
     */
    private void checkAfterMoveEffects() {
        if (attackingPokemon.getSkill().isMakesPhysicalContact() && isTargetingDefender(attackingPokemon.getSkill())) {
            checkAbilityContact();
        }
        if (attackingPokemon.getSkill().getStrikesLeft() > 0) {
            useMove();
        } else {
            attackingPokemon.getSkill().resetStrikes();
            if (attackingPokemon.getSkill().hasRecoil()) {

            } else if (attackingPokemon.getSkill().hasDrain()) {

            } //TODO: Add powder scenario

            if (isFirstMove) {
                if (hasFainted(defendingPokemon)) {
                    currentStep = BattleStep.ENEMY_FAINTED;
                    faintingPokemon = defendingPokemon;
                    uiComponent = new FaintAnimation(this, defendingPokemon.getPokemon(), defendingPokemon.isUser());
                } else {
                    if (attackingPokemon.getSkill().hasDrain() && !attackingPokemon.getPokemon().isMaxHealth()) {
                        uiComponent = new HealthUpdater(this, attackingPokemon.getPokemon());
                        currentStep = BattleStep.DRAIN_TEXT;
                    } else if (attackingPokemon.getSkill().hasRecoil() &&
                            attackingPokemon.getPokemon().getAnimationHealth() != attackingPokemon.getPokemon().getCurrentHealth()) {
                        uiComponent = new HealthUpdater(this, attackingPokemon.getPokemon());
                        currentStep = BattleStep.RECOIL_TEXT;
                    } else {
                        battleStateManager.setState(new SleepState(battleStateManager, false));
                    }
                }
            } else {
                if (hasFainted(defendingPokemon)) {
                    currentStep = BattleStep.ENEMY_FAINTED;
                    faintingPokemon = defendingPokemon;
                    uiComponent = new FaintAnimation(this, defendingPokemon.getPokemon(), defendingPokemon.isUser());
                } else {
                    if (attackingPokemon.getSkill().hasDrain() && !attackingPokemon.getPokemon().isMaxHealth()) {
                        uiComponent = new HealthUpdater(this, attackingPokemon.getPokemon());
                        currentStep = BattleStep.DRAIN_TEXT;
                    } else if (attackingPokemon.getSkill().hasRecoil() &&
                            attackingPokemon.getPokemon().getAnimationHealth() != attackingPokemon.getPokemon().getCurrentHealth()) {
                        uiComponent = new HealthUpdater(this, attackingPokemon.getPokemon());
                        currentStep = BattleStep.RECOIL_TEXT;
                    } else {
                        battleStateManager.setState(new EndTurnState2(battleStateManager));
                    }
                }
            }
        }
    }

    private boolean hasFainted(BattleStatePokemon pokemon) {
        return pokemon.getPokemon().hasFainted();
    }

    public void update(float dt) {
        if (currentStep == null) {
            logPokemonInfo();
            int previousCurrentHealth;
            int previousStatus;
            battleLog(attackingPokemon.getPokemon().getName() + " USED " + attackingPokemon.getSkill().getName());
            attackingPokemon.getSkill().setTotalStrikes(attackingPokemon, defendingPokemon, battleStateManager.getField(), attackerField, defenderField, isFirstMove);
            //TODO: Use the attack here
            useMove();
        }

        if (uiComponent != null) {
            uiComponent.update(dt);
        }

        if (finishedUpdatingUI()) {
            if (currentStep == BattleStep.CHECK_MOVE_MESSAGES) {
                if (moveMessages != null && moveMessages.size() > 0) {
                    currentStep = BattleStep.CHECK_ABILITY_CONTACT;
                    uiComponent = new BattleTextBoxComponent(this, moveMessages);
                } else {
                    currentStep = BattleStep.CHECK_ABILITY_CONTACT;
                    //uiComponent = new PauseUpdater(this, 1.5f);
                }
            } else if (currentStep == BattleStep.CHECK_ABILITY_CONTACT) {
                checkAfterMoveEffects();
            } else if (currentStep == BattleStep.ENEMY_FAINTED) {
                battleLog("ENEMY POKEMON FAINTED");
                BattleTextBoxComponent btc = new BattleTextBoxComponent(this, createMoveMessages(faintingPokemon.getPokemon().getName() + " fainted!"));
                btc.setFinishType(BattleTextBox.BattleTextBoxFinish.TRIGGER);
                uiComponent = btc;
                currentStep = BattleStep.ENEMY_FAINT_TEXT;
            } else if (currentStep == BattleStep.ENEMY_FAINT_TEXT) {
                battleLog("ENEMY FAINT TEXT");
                endState();
            } else if (currentStep == BattleStep.DRAIN_TEXT) {
                uiComponent = new BattleTextBoxComponent(this, createMoveMessages("The wild " + defendingPokemon.getPokemon().getName() + " had its\nenergy drained!"));
                currentStep = BattleStep.END_STATE;
            } else if (currentStep == BattleStep.RECOIL_TEXT) {
                uiComponent = new BattleTextBoxComponent(this, createMoveMessages(attackingPokemon.getPokemon().getName() + " is damaged\nby recoil!"));
                if (hasFainted(attackingPokemon)) {
                    faintingPokemon = attackingPokemon;
                    currentStep = ENEMY_FAINTED;
                    uiComponent = new FaintAnimation(this, attackingPokemon.getPokemon(), attackingPokemon.isUser());
                } else {
                    currentStep = BattleStep.END_STATE;
                }
            } else if (currentStep == BattleStep.END_STATE) {
                endState();
            }
        }
    }

    private void endState() {
        if (isFirstMove) {
            if (hasFainted(defendingPokemon)) {
                if (attackingPokemon.isUser()) {
                    battleStateManager.setState(new ExpState(battleStateManager, attackingPokemon.getPokemon(), defendingPokemon.getPokemon(), isFirstMove));
                } else {
                    if (!battleStateManager.isPlayerPartyWiped()) {
                        battleStateManager.setState(new SleepState(battleStateManager, false));
                    }
                }
            } else if (hasFainted(attackingPokemon)) {
                if (defendingPokemon.isUser()) {
                    battleStateManager.setState(new ExpState(battleStateManager, defendingPokemon.getPokemon(), attackingPokemon.getPokemon(), isFirstMove));
                } else {
                    if (!battleStateManager.isPlayerPartyWiped()) {
                        battleStateManager.setState(new SleepState(battleStateManager, false));
                    }
                }
            } else {
                battleStateManager.setState(new SleepState(battleStateManager, false));
            }
        } else {
            if (hasFainted(defendingPokemon)) {
                if (attackingPokemon.isUser()) {
                    battleStateManager.setState(new ExpState(battleStateManager, attackingPokemon.getPokemon(), defendingPokemon.getPokemon(), isFirstMove));
                } else {
                    if (!battleStateManager.isPlayerPartyWiped()) {
                        battleStateManager.setState(new EndTurnState2(battleStateManager));
                    }
                }
            } else if (hasFainted(attackingPokemon)) {
                if (defendingPokemon.isUser()) {
                    battleStateManager.setState(new ExpState(battleStateManager, defendingPokemon.getPokemon(), attackingPokemon.getPokemon(), isFirstMove));
                } else {
                    if (!battleStateManager.isPlayerPartyWiped()) {
                        battleStateManager.setState(new EndTurnState2(battleStateManager));
                    }
                }
            } else {
                battleStateManager.setState(new EndTurnState2(battleStateManager));
            }
        }
    }


    private void other(float dt) {
        logPokemonInfo();
        int previousCurrentHealth;
        int previousStatus;
        battleLog(attackingPokemon.getPokemon().getName() + " USED " + attackingPokemon.getSkill().getName());
        attackingPokemon.getSkill().setTotalStrikes(attackingPokemon, defendingPokemon, battleStateManager.getField(), attackerField, defenderField, isFirstMove);
        do {
            //TODO: Use the attack here
            attackingPokemon.getSkill().use(attackingPokemon.getPokemon(), defendingPokemon.getPokemon(),
                    0, 0, battleStateManager.getField(), attackerField,
                    defenderField, isFirstMove,
                    defendingPokemon.getSkill(), new ArrayList<BattlePokemon>(), new ArrayList<BattlePokemon>());
            logPokemonInfo();
            if (attackingPokemon.getSkill().isMakesPhysicalContact() && isTargetingDefender(attackingPokemon.getSkill())) {
                checkAbilityContact();
            }
        } while (attackingPokemon.getSkill().getStrikesLeft() > 0);
        attackingPokemon.getSkill().resetStrikes();
        if (attackingPokemon.getSkill().hasRecoil()) {

        } else if (attackingPokemon.getSkill().hasDrain()) {

        } //TODO: Add powder scenario

        if (isFirstMove) {
            battleStateManager.setState(new SleepState(battleStateManager, false));
        } else {
            battleStateManager.setState(new EndTurnState2(battleStateManager));
        }
    }

    private void checkAbilityContact() {
        AbilityId defendingAbility = defendingPokemon.getPokemon().getAbility();
        battleLog("CHECK ABILITY CONTACT");
        if (defendingAbility== AbilityId.STATIC) {
            useStatic();
        } else if (defendingAbility == AbilityId.POISON_POINT) {
            usePoisonPoint();
        } else if (attackingPokemon.getPokemon().getAbility() == AbilityId.POISON_TOUCH) {
            usePoisonTouch();
        } else if (defendingAbility == AbilityId.FLAME_BODY) {
            useFlameBody();
        } else if (defendingAbility == AbilityId.EFFECT_SPORE) {
            useEffectSpore();
        } else if (defendingAbility == AbilityId.ROUGH_SKIN) {
            useRoughSkin();
        }
        battleLog("COMPLETED ABILITY CONTACT CHECK");
    }

    private void useRoughSkin() {

    }

    private void useEffectSpore() {
        if (!attackingPokemon.getPokemon().isStatused()) {
            double rand = Math.random();
            if (rand <= .09) {
                if (attackingPokemon.getPokemon().isPoisonable()) {
                    battleLog(attackingPokemon.getPokemon().getName() + " WAS POISONED FROM CONTACT WITH ABILITY: EFFECT SPORE");
                    attackingPokemon.getPokemon().setPreStatus(StatusCondition.POISON);
                }
            } else if (rand > .09 && rand <= .19) {
                if (attackingPokemon.getPokemon().isParalyzable()) {
                    battleLog(attackingPokemon.getPokemon().getName() + " WAS PARALYZED FROM CONTACT WITH ABILITY: EFFECT SPORE");
                    attackingPokemon.getPokemon().setPreStatus(StatusCondition.PARALYSIS);
                }
            } else if (rand > .19 && rand <= .3) {
                if (attackingPokemon.getPokemon().isSleepable()) {
                    battleLog(attackingPokemon.getPokemon().getName() + " WAS PUT TO SLEEP FROM CONTACT WITH ABILITY: EFFECT SPORE");
                    attackingPokemon.getPokemon().induceSleep();
                }
            } else {
                battleLog(attackingPokemon.getPokemon().getName() + " PASSED EFFECT SPORE CHECK");
            }
        } else {
            battleLog(attackingPokemon.getPokemon().getName() + " IS IMMUNE TO EFFECT SPORE BECAUSE IT IS ALREADY STATUSED");
        }
    }

    private void useStatic() {
        battleLog("CHECKING CONTACT WITH ABILITY: STATIC");
        if (attackingPokemon.getPokemon().isParalyzable()) {
            double rand = Math.random();
            if (rand <= .3) {
                battleLog(attackingPokemon.getPokemon().getName() + " WAS PARALYZED FROM CONTACT WITH ABILITY: STATIC");
                attackingPokemon.getPokemon().setPreStatus(StatusCondition.PARALYSIS);
            } else {
                battleLog(attackingPokemon.getPokemon().getName() + " PASSED STATIC CHECK");
            }
        } else {
            battleLog(attackingPokemon.getPokemon().getName() + " CANNOT BE PARALYZED BY STATIC");
        }
    }

    private void usePoisonPoint() {
        battleLog("CHECKING CONTACT WITH ABILITY: POISON POINT");
        if (attackingPokemon.getPokemon().isPoisonable()) {
            double rand = Math.random();
            if (rand <= .30) {
                battleLog(attackingPokemon.getPokemon().getName() + " WAS POISONED FROM CONTACT WITH ABILITY: POISON POINT");
                attackingPokemon.getPokemon().setPreStatus(StatusCondition.POISON);
            } else {
                battleLog(attackingPokemon.getPokemon().getName() + " PASSED POISON POINT CHECK");
            }
        } else {
            battleLog(attackingPokemon.getPokemon().getName() + " CANNOT BE POISONED BY POISON POINT");
        }
    }

    private void usePoisonTouch() {
        battleLog("CHECKING CONTACT WITH ABILITY: POISON TOUCH");
        if (defendingPokemon.getPokemon().isPoisonable()) {
            double rand = Math.random();
            if (rand <= .30) {
                battleLog(defendingPokemon.getPokemon().getName() + " WAS POISONED FROM CONTACT WITH ABILITY: POISON TOUCH");
                defendingPokemon.getPokemon().setPreStatus(StatusCondition.POISON);
            } else {
                battleLog(defendingPokemon.getPokemon().getName() + " PASSED POISON TOUCH CHECK");
            }
        } else {
            battleLog(defendingPokemon.getPokemon().getName() + " CANNOT BE POISONED BY POISON TOUCH");
        }
    }

    private void useFlameBody() {
        battleLog("CHECKING CONTACT WITH ABILITY: FLAME BODY");
        if (attackingPokemon.getPokemon().isBurnable()) {
            double rand = Math.random();
            if (rand <= .30) {
                battleLog(attackingPokemon.getPokemon().getName() + " WAS BURNED FROM CONTACT WITH ABILITY: FLAME BODY");
                attackingPokemon.getPokemon().setPreStatus(StatusCondition.BURN);
            } else {
                battleLog(attackingPokemon.getPokemon().getName() + " PASSED FLAME BODY CHECK");
            }
        } else {
            battleLog(attackingPokemon.getPokemon().getName() + " CANNOT BE BURNED BY FLAME BODY");
        }
    }

    private boolean isTargetingDefender(Skill skill) {
        if (skill.isSolarBeam() && battleStateManager.getField().getWeatherType() == WeatherType.SUN) {
            return skill.isTargetingEnemy();
        } else if (skill.isChargingSkill() && attackingPokemon.getPokemon().getNextTurnSkill() == null) {
            //Sky Drop, Solar Beam, Fly, Dig, Sky Attack, Dive, Bounce, Skull Bash
            return false;
        } else {
            return skill.isTargetingEnemy();
        }
    }

    private void logPokemonInfo() {
        battleLog("================== ATTACKING POKEMON ==================");
        logPokemonBasicInfo(attackingPokemon.getPokemon());
        battleLog("================== DEFENDING POKEMON ==================");
        logPokemonBasicInfo(defendingPokemon.getPokemon());
        battleLog("================== ================= ==================");
    }

    private void logPokemonBasicInfo(BattlePokemon pokemon) {
        battleLog(pokemon.getName() + " (" +
                pokemon.getCurrentHealth() + "/" + pokemon.getPokemon().getHealthStat() + ")");
        battleLog("STATUS: " + pokemon.getPokemon().getUniqueVariables().getStatus());
        battleLog("STAT STAGES: ATK: " + pokemon.getAttackStage() + ", " +
                "DEF: " + pokemon.getDefenseStage() + ", " +
                "SP ATK: " + pokemon.getSpecialAttackStage() + ", " +
                "SP DEF: " + pokemon.getSpecialDefenseStage() + ", " +
                "SPD: " + pokemon.getSpeedStage() + ", " +
                "ACC: " + pokemon.getAccuracyStage() + ", " +
                "EVA: " + pokemon.getEvasionStage());
    }
}
