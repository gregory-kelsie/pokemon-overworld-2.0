package com.anime.arena.battle;

import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.PokemonType;
import com.anime.arena.pokemon.PokemonUtils;
import com.anime.arena.skill.Skill;
import com.anime.arena.skill.SkillCategory;
import com.badlogic.gdx.Gdx;

public class AbsorbSkillState extends BattleState {
    private BattleStatePokemon attackingPokemon;
    private BattleStatePokemon defendingPokemon;
    private SubField attackerField;
    private SubField defenderField;
    private boolean isFirstMove;

    public AbsorbSkillState(BattleStateManager battleStateManager, boolean isFirstMove) {
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
        Gdx.app.log("AbsorbSkillState", str);
    }

    public void update(float dt) {
        boolean hasAbsorbedAttack = false;
        Skill attackingMove = attackingPokemon.getSkill();
        AbilityId defendingAbility = defendingPokemon.getPokemon().getAbility();
        if (attackingMove.getMoveType() == PokemonType.ELECTRIC && attackingMove.getCategory() != SkillCategory.MISC) {
            if (defendingAbility == AbilityId.LIGHTNINGROD) {
                useLightningRodAbsorbEffect();
            } else if (defendingAbility == AbilityId.MOTOR_DRIVE) {
                useMotorDriveAbsorbEffect();
            } else if (defendingAbility == AbilityId.VOLT_ABSORB) {
                useVoltAbsorbAbsorbEffect();
            }
        } else if (attackingMove.getMoveType() == PokemonType.WATER && attackingMove.getCategory() != SkillCategory.MISC) {
            if (defendingAbility == AbilityId.DRY_SKIN) {
                useDrySkinAbsorbEffect();
            } else if (defendingAbility == AbilityId.WATER_ABSORB) {
                useWaterAbsorbAbsorbEffect();
            }
        }
        if (hasAbsorbedAttack) {
            if (isFirstMove) {
                battleLog("ATTACK WAS ABSORBED - GOING TO SECOND ATTACKER'S SLEEP CHECK");
                battleStateManager.setState(new SleepState(battleStateManager, false));
            } else {
                battleLog("ATTACK WAS ABSORBED - GOING TO END TURN STATE");
                battleStateManager.setState(new EndTurnState2(battleStateManager));
            }
        } else {
            battleLog("PASSED ABSORB CHECK - GOING TO EXECUTE SKILL");
            battleStateManager.setState(new ExecuteSkillState(battleStateManager, isFirstMove));
        }
    }

    /**
     * Lightning Rod absorbs an
     * attack successfully while gaining Lightning Rod's absorbtion
     * effects.
     */
    private void useLightningRodAbsorbEffect() {
        battleLog(defendingPokemon.getPokemon().getName() + " TAKES NO DAMAGE DUE TO THEIR ABILITY: LIGHTNINGROD");
        if (defendingPokemon.getPokemon().getSpecialAttackStage() < 6) {
            battleLog(defendingPokemon.getPokemon().getName() + "'s Special Attack rose! - LIGHTNING ROD");
            defendingPokemon.getPokemon().increaseSpAttackStage(1);
        } else {
            battleLog(defendingPokemon.getPokemon().getName() + "'s Special Attack cannot go higher. - LIGHTNING ROD");
        }
    }

    /**
     * Motor Drive absorbs an
     * attack successfully while gaining Motor Drive's absorbtion
     * effects.
     * @return Motor Drive's successful AbsorbResults.
     */
    private void useMotorDriveAbsorbEffect() {
        battleLog(defendingPokemon.getPokemon().getName() + " TAKES NO DAMAGE DUE TO THEIR ABILITY:  MOTOR DRIVE.");
        if (defendingPokemon.getPokemon().getSpeedStage() < 6) {
            battleLog(defendingPokemon.getPokemon().getName() + "'s SPEED INCREASED BY 1 STAGE - MOTOR DRIVE");
            defendingPokemon.getPokemon().increaseSpeedStage(1);
        } else {
            battleLog(defendingPokemon.getPokemon().getName() + "'s SPEED CANNOT GO HIGHER THAN 6 - MOTOR DRIVE");
        }
    }

    /**
     * Return the AbsorbResults when Dry Skin absorbs an
     * attack successfully while gaining Dry Skin's absorbtion
     * effects.
     * @return Dry Skin's successful AbsorbResults.
     */
    private void useDrySkinAbsorbEffect() {
        battleLog(defendingPokemon.getPokemon().getName() + " TAKES NO DAMAGE DUE TO THEIR ABILITY:  DRY SKIN");
        if (!defendingPokemon.getPokemon().hasFullHealth()) {
            int currentHealth = defendingPokemon.getPokemon().getCurrentHealth();
            int healingAmount = (int)Math.round(defendingPokemon.getPokemon().getPokemon().getHealthStat() / 4.0);
            battleLog("OLD HEALTH: " + currentHealth);
            PokemonUtils.healPokemon(defendingPokemon.getPokemon().getPokemon(), healingAmount);
            battleLog(defendingPokemon.getPokemon().getName() + " RECOVERED " + healingAmount + " HEALTH DUE TO THEIR ABILITY DRY SKIN");
            battleLog("NEW HEALTH: " + defendingPokemon.getPokemon().getCurrentHealth());
        } else {
            battleLog(defendingPokemon.getPokemon().getName() + " ABSORBED THE ATTACK WITH DRY SKIN - NO HEALING DUE TO HAVING FULL HEALTH");
        }
    }

    /**
     * Return the AbsorbResults when Water Absorb absorbs an
     * attack successfully while gaining Water Absorb's absorbtion
     * effects.
     * @return Water Absorbs successful AbsorbResults.
     */
    private void useWaterAbsorbAbsorbEffect() {
        battleLog(defendingPokemon.getPokemon().getName() + " TAKES NO DAMAGE DUE TO THEIR ABILITY:  WATER ABSORB.");
        if (!defendingPokemon.getPokemon().hasFullHealth()) {
            int currentHealth = defendingPokemon.getPokemon().getCurrentHealth();
            int healingAmount = (int)Math.round(defendingPokemon.getPokemon().getPokemon().getHealthStat() / 4.0);
            battleLog("OLD HEALTH: " + currentHealth);
            PokemonUtils.healPokemon(defendingPokemon.getPokemon().getPokemon(), healingAmount);
            battleLog(defendingPokemon.getPokemon().getName() + " RECOVERED " + healingAmount + " HEALTH DUE TO THEIR ABILITY WATER ABSORB");
            battleLog("NEW HEALTH: " + defendingPokemon.getPokemon().getCurrentHealth());
        } else {
            battleLog(defendingPokemon.getPokemon().getName() + " ABSORBED THE ATTACK WITH WATER ABSORB - NO HEALING DUE TO HAVING FULL HEALTH");
        }
    }

    /**
     * Return the AbsorbResults when Volt Absorb absorbs an
     * attack successfully while gaining Volt Absorb's absorbtion
     * effects.
     * @return Volt Absorbs successful AbsorbResults.
     */
    private void useVoltAbsorbAbsorbEffect() {
        battleLog(defendingPokemon.getPokemon().getName() + " TAKES NO DAMAGE DUE TO THEIR ABILITY:  VOLT ABSORB.");
        if (!defendingPokemon.getPokemon().hasFullHealth()) {
            int currentHealth = defendingPokemon.getPokemon().getCurrentHealth();
            int healingAmount = (int)Math.round(defendingPokemon.getPokemon().getPokemon().getHealthStat() / 4.0);
            battleLog("OLD HEALTH: " + currentHealth);
            PokemonUtils.healPokemon(defendingPokemon.getPokemon().getPokemon(), healingAmount);
            battleLog(defendingPokemon.getPokemon().getName() + " RECOVERED " + healingAmount + " HEALTH DUE TO THEIR ABILITY VOLT ABSORB");
            battleLog("NEW HEALTH: " + defendingPokemon.getPokemon().getCurrentHealth());
        } else {
            battleLog(defendingPokemon.getPokemon().getName() + " ABSORBED THE ATTACK WITH VOLT ABSORB - NO HEALING DUE TO HAVING FULL HEALTH");
        }
    }
}
