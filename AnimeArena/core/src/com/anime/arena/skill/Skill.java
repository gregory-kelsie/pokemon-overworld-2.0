package com.anime.arena.skill;

import com.anime.arena.battle.BattleStatePokemon;
import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.field.WeatherType;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;

import java.util.ArrayList;
import java.util.List;

import static com.anime.arena.skill.MultiHitType.TRIPLE_KICK;
import static com.anime.arena.skill.MultiHitType.TWO_TIMES;

public class Skill {
    protected int id;
    protected String name;
    protected String description;
    /**
     * Subtype
     * DamageSkill 0
     * SecondaryEffect 1
     * MultiHitMove - 2
     * BindSkill 3 - Bind - subtype of damageskill
     * CrushGripSkill 6
     * EffectSkill 5
     * FlatDamageSkill 7 (uses a flat damage variable)
     */
    protected int subtype;
    protected SkillCategory category;
    protected int maxPP;
    protected int currentPP;
    protected int basePower;
    protected int accuracy;
    protected int speedPriority;
    protected SkillTarget target;
    protected PokemonType moveType;
    protected int strikesLeft;

    protected boolean damagesEnemy;
    protected boolean makesPhysicalContact;
    protected boolean isSoundMove;
    protected boolean isPunchMove;
    protected boolean isSnatchable;
    protected boolean defrostsPokemon;
    protected boolean isReflected; //Magic Coat, Magic Bounce
    protected boolean isBlockable; //Protect, Detect
    protected boolean isBiteMove;
    protected boolean isOppositeGenderMove; //Captivate
    protected boolean ignoreStateChanges; //Chip Away

    protected boolean hitFlyingPokemon;
    protected boolean hitUnderwaterPokemon;
    protected boolean hitUndergroundPokemon;

    protected boolean isMultiHitMove;
    protected MultiHitType multiHitType;

    protected boolean continuesThroughNoEffect; //self-destruct, explosion
    protected boolean requiresFirstMove; //First-Impression, Sucker Punch etc
    protected boolean isSingleRecoveryMove; //Recover, Heal Order, Moonlight
    protected boolean isPowderMove; //Powder
    protected boolean isSpiderWebMove; //Spider Web
    protected boolean isStickyWebMove; //Sticky Web
    protected boolean requiresEnemyDamageAttack; //Sucker Punch

    protected boolean requiresSleep; //Nightmare
    protected boolean requiresNoNightmares; //Nightmare
    protected boolean requiresNonGrassDefender; //Leech Seed
    protected boolean requiresNonSeeded;
    protected boolean isLeechSeedMove;

    protected boolean isThunderMove;
    protected boolean isHurricaneMove;
    protected boolean isBlizzardMove;

    protected boolean ignoreTargetStatChanges;
    protected int damageTally;


    public Skill(int id, String name, String description, SkillCategory category, int pp, int currentPP, int accuracy, PokemonType moveType,
                 SkillTarget target, int subtype, int speedPriority) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.maxPP = pp;
        this.currentPP = currentPP;
        this.accuracy = accuracy;
        this.moveType = moveType;
        this.target = target;
        this.subtype = subtype;
        this.speedPriority = speedPriority;
        initMisc();
    }

    public Skill(int id, String name, String description, SkillCategory category, int pp, int currentPP, int accuracy, PokemonType moveType,
                 SkillTarget target, int subtype, int speedPriority, int basePower) {
        this(id, name, description, category, pp, currentPP, accuracy, moveType, target, subtype, speedPriority);
        this.basePower = basePower;
    }

    private void initMisc() {
        this.strikesLeft = -1;
        this.damagesEnemy = false;
        this.makesPhysicalContact = false;
        this.isSoundMove = false;
        this.isPunchMove = false;
        this.isSnatchable = false;
        this.defrostsPokemon = false;
        this.isReflected = false; //Magic Coat, Magic Bounce
        this.isBlockable = false; //Protect, Detect
        this.isBiteMove = false;
        this.isOppositeGenderMove = false; //Captivate
        this.ignoreStateChanges = false; //Chip Away

        this.hitFlyingPokemon = false;
        this.hitUnderwaterPokemon = false;
        this.hitUndergroundPokemon = false;

        this.isMultiHitMove = false;
        this.multiHitType = MultiHitType.ONE;
        this.damageTally = 0;

        this.continuesThroughNoEffect = false;
        this.requiresFirstMove = false;
        this.isSingleRecoveryMove = false;
        this.isPowderMove = false;
        this.isSpiderWebMove = false;
        this.isStickyWebMove = false;
        this.requiresEnemyDamageAttack = false;
    }

    /**
     * Use the skill on an enemy pokemon.
     * @param skillUser The Pokemon using the skill
     * @param enemyPokemon The enemy receiving the skill
     * @param skillUserPartyPosition The position of the skill user in their party.
     * @param enemyPokemonPartyPosition The position of the enemy pokemon in their party.
     * @param field The field for the battle.
     * @param userField The field for the battle.
     * @param enemyField The field for the battle.
     * @param isFirstAttack Whether or not the skill was used first in the clash
     * @param targetSkill
     * @param skillUserParty The party for the skill user.
     * @param enemyPokemonParty The party for the skill receiver.
     * @return List of Strings that display the result of using the move. The first list
     * displays misses, and the second
     */
    public List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon,
                    int skillUserPartyPosition, int enemyPokemonPartyPosition, Field field, SubField userField,
                    SubField enemyField, boolean isFirstAttack,
                    Skill targetSkill, List<BattlePokemon> skillUserParty, List<BattlePokemon> enemyPokemonParty) {
        refreshMoveCounters(skillUser);
        return new ArrayList<String>();
    }

    protected void refreshMoveCounters(BattlePokemon skillUser) {
        skillUser.removeRage();
        if (strikesLeft > 0) {
            strikesLeft--;
        }
    }

    /**
     * Return whether or not the move will hit the enemy.
     * @param skillUser The Pokemon using the skill.
     * @param enemyPokemon The Pokemon receiving the skill.
     * @param field The field for the battle.
     * @param userField The field for the battle.
     * @param enemyField The field for the battle.
     * @param isFirstAttack Whether or not the skill was used first in the clash
     * @return Whether or not the skill hits the enemy.
     */
    public boolean willHitEnemy(BattlePokemon skillUser, BattlePokemon enemyPokemon,
                                Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        int currentAccuracy = accuracy;
        if (accuracy != -1) {
            if (skillUser.isSkyDropped() && !hitFlyingPokemon) {
                return false;
            }
            //Init Modifiers
            if (enemyPokemon.isUnderwater() && !hitUnderwaterPokemon) {
                return false;
            }
            if (enemyPokemon.isFlying() && !hitFlyingPokemon) {
                return false;
            }
            if (enemyPokemon.isUnderground() && !hitUndergroundPokemon) {
                return false;
            }
            int accuracyStage = skillUser.getAccuracyStage();
            double attackerAccuracyMod = skillUser.getAccuracyModifier(accuracyStage);
            if (enemyPokemon.getAbility() == AbilityId.SAND_VEIL &&
                    field.getWeatherType() == WeatherType.SAND) {
                attackerAccuracyMod *= 0.8;
            } else if (enemyPokemon.getAbility() == AbilityId.SNOW_CLOAK &&
                    field.getWeatherType() == WeatherType.HAIL) {
                attackerAccuracyMod *= 0.8;
            }
            if (skillUser.getAbility() == AbilityId.HUSTLE) {
                attackerAccuracyMod *= 0.8;
            }
            int evasionStage = 0;
            if (!ignoreTargetStatChanges) {
                evasionStage = enemyPokemon.getEvasionStage();
                if (enemyPokemon.isConfused() && enemyPokemon.getAbility() == AbilityId.TANGLED_FEET) {
                    evasionStage++;
                    evasionStage = Math.min(6, evasionStage);
                }
            }

            if (isThunderMove && (field.getWeatherType() == WeatherType.RAIN ||
                    field.getWeatherType() == WeatherType.HEAVY_RAIN)) {
                currentAccuracy = 100;
            }
            if (isHurricaneMove) {
                if (field.getWeatherType() == WeatherType.RAIN) {
                    currentAccuracy = 100;
                } else if (field.getWeatherType() == WeatherType.SUN) {
                    currentAccuracy = 50;
                }
            }
            if (isBlizzardMove) {
                if (enemyPokemon.isUnderwater() || enemyPokemon.isUnderground() ||
                        enemyPokemon.isFlying()) {
                    //Misses when enemy is semi-invulnerable, even if there is
                    //an accuracy bypass.
                    return false;
                } else {
                    if (field.getWeatherType() == WeatherType.HAIL) {
                        //Bypass accuracy check
                        return true;
                    }
                }
            }

            double enemyEvasionMod = enemyPokemon.getEvasionModifier(evasionStage);
            double result = getAccuracyMod(currentAccuracy) * attackerAccuracyMod * enemyEvasionMod;

            //Check if hit.
            double rand = Math.random();
            if (rand > result) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }


    /**
     * Return the accuracy modifier for the skill.
     * Ex: Accuracy 95 has a modifier of 0.95
     * @return The accuracy modifier for the skill.
     */
    public double getAccuracyMod(int newAccuracy) {
        return newAccuracy / 100.0;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSubtype() {
        return subtype;
    }

    public void setSubtype(int subtype) {
        this.subtype = subtype;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public void setCategory(SkillCategory category) {
        this.category = category;
    }

    /**
     * Return the maximum PP for the skill.
     * @return The skill's maximum PP.
     */
    public int getMaxPP() {
        return maxPP;
    }

    /**
     * Set the current pp of the skill to the specified amount.
     * @param newPP The new amount of current pp.
     */
    public void setCurrentPP(int newPP) {
        this.currentPP = newPP;
    }


    /**
     * Return the skill's current PP
     * @return The skill's current PP.
     */
    public int getCurrentPP() {
        return currentPP;
    }

    /**
     * Decrease the skill's current PP by 1
     */
    public void decreasePP() {
        currentPP--;
        currentPP = Math.max(currentPP, 0);
    }

    /**
     * Decrease the skill's current PP by an amount.
     * @param amount The amount to decrease the current PP by.
     */
    public void decreasePP(int amount) {
        currentPP -= amount;
        currentPP = Math.max(currentPP, 0);
    }

    /**
     * Increase the skill's current PP by 1.
     */
    public void increasePP() {
        currentPP++;
        currentPP = Math.min(currentPP, maxPP);
    }

    /**
     * Increase the skill's current PP by an amount.
     * @param amount The amount to increase the current PP by.
     */
    public void increasePP(int amount) {
        currentPP += amount;
        currentPP = Math.min(currentPP, maxPP);
    }

    /**
     * Set the current PP to the total amount.
     */
    public void setFullPP() {
        currentPP = maxPP;
    }

    public int getBasePower() {
        return basePower;
    }

    public void setBasePower(int basePower) {
        this.basePower = basePower;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getSpeedPriority() {
        return speedPriority;
    }

    public void setSpeedPriority(int speedPriority) {
        this.speedPriority = speedPriority;
    }

    public SkillTarget getTarget() {
        return target;
    }

    public void setTarget(SkillTarget target) {
        this.target = target;
    }

    public PokemonType getMoveType() {
        return moveType;
    }

    public void setMoveType(PokemonType moveType) {
        this.moveType = moveType;
    }

    public boolean isMakesPhysicalContact() {
        return makesPhysicalContact;
    }

    public void setMakesPhysicalContact(boolean makesPhysicalContact) {
        this.makesPhysicalContact = makesPhysicalContact;
    }

    public boolean isSoundMove() {
        return isSoundMove;
    }

    public void setSoundMove(boolean soundMove) {
        isSoundMove = soundMove;
    }

    public boolean isPunchMove() {
        return isPunchMove;
    }

    public void setPunchMove(boolean punchMove) {
        isPunchMove = punchMove;
    }

    public boolean isSnatchable() {
        return isSnatchable;
    }

    public void setSnatchable(boolean snatchable) {
        isSnatchable = snatchable;
    }

    public boolean isDefrostsPokemon() {
        return defrostsPokemon;
    }

    public void setDefrostsPokemon(boolean defrostsPokemon) {
        this.defrostsPokemon = defrostsPokemon;
    }

    public boolean isReflected() {
        return isReflected;
    }

    public void setReflected(boolean reflected) {
        isReflected = reflected;
    }

    public boolean isBlockable() {
        return isBlockable;
    }

    public void setBlockable(boolean blockable) {
        isBlockable = blockable;
    }

    public boolean isBiteMove() {
        return isBiteMove;
    }

    public void setBiteMove(boolean biteMove) {
        isBiteMove = biteMove;
    }

    public boolean isOppositeGenderMove() {
        return isOppositeGenderMove;
    }

    public void setOppositeGenderMove(boolean oppositeGenderMove) {
        isOppositeGenderMove = oppositeGenderMove;
    }

    public boolean isIgnoreStateChanges() {
        return ignoreStateChanges;
    }

    public void setIgnoreStateChanges(boolean ignoreStateChanges) {
        this.ignoreStateChanges = ignoreStateChanges;
    }

    public boolean isHitFlyingPokemon() {
        return hitFlyingPokemon;
    }

    public void setHitFlyingPokemon(boolean hitFlyingPokemon) {
        this.hitFlyingPokemon = hitFlyingPokemon;
    }

    public boolean isHitUnderwaterPokemon() {
        return hitUnderwaterPokemon;
    }

    public void setHitUnderwaterPokemon(boolean hitUnderwaterPokemon) {
        this.hitUnderwaterPokemon = hitUnderwaterPokemon;
    }

    public boolean isHitUndergroundPokemon() {
        return hitUndergroundPokemon;
    }

    public void setHitUndergroundPokemon(boolean hitUndergroundPokemon) {
        this.hitUndergroundPokemon = hitUndergroundPokemon;
    }

    public boolean isMultiHitMove() {
        return isMultiHitMove;
    }

    public void setMultiHitMove(boolean multiHitMove) {
        isMultiHitMove = multiHitMove;
    }

    public MultiHitType getMultiHitType() {
        return multiHitType;
    }

    public void setMultiHitType(MultiHitType multiHitType) {
        this.multiHitType = multiHitType;
    }

    public void setRequiresFirstMove(boolean requiresFirstMove) {
        this.requiresFirstMove = requiresFirstMove;
    }

    public boolean requiresFirstMove() {
        return requiresFirstMove;
    }

    public boolean isSingleRecoveryMove() {
        return isSingleRecoveryMove;
    }

    public void setIsSingleRecoveryMove(boolean isSingleRecoveryMove) {
        this.isSingleRecoveryMove = isSingleRecoveryMove;
    }

    public boolean isPowderMove() {
        return isPowderMove;
    }

    public void setIsPowderMove(boolean isPowderMove) {
        this.isPowderMove = isPowderMove;
    }

    public boolean isSpiderWebMove() {
        return isSpiderWebMove;
    }

    public boolean requiresSleep() {
        return requiresSleep;
    }

    public boolean requiresNoNightmares() {
        return requiresNoNightmares;
    }

    public boolean isStickyWeb() {
        return isStickyWebMove;
    }

    public boolean requiresEnemyDamageAttack() {
        return requiresEnemyDamageAttack;
    }

    public boolean damagesEnemy() {
        return damagesEnemy;
    }

    public boolean requiresNonGrassDefender() {
        return requiresNonGrassDefender;
    }

    public boolean isLeechSeedMove() {
        return isLeechSeedMove;
    }

    public boolean isContinuingThroughNoEffect() { return continuesThroughNoEffect; }

    public boolean hasRecoil() {
        return false;
    }

    public boolean hasDrain() {
        return false;
    }

    public boolean isTargetingEnemy() {
        if (target == SkillTarget.ENEMY) {
            return true;
        }
        return false;
    }

    public void setTotalStrikes(BattleStatePokemon attackingPokemon, BattleStatePokemon defendingPokemon, Field field,
                                SubField attackerField, SubField defenderField, boolean isFirstMove) {
        if (multiHitType == MultiHitType.STANDARD_FIVE) {
            double rand = Math.random();
            if (rand <= .375) {
                strikesLeft = 2;
            } else if (rand <= .75) {
                strikesLeft = 3;
            } else if (rand <= .875) {
                strikesLeft = 4;
            } else {
                strikesLeft = 5;
            }
        } else if (multiHitType == TWO_TIMES) {
            strikesLeft = 2;
        } else if (multiHitType == TRIPLE_KICK) {
            strikesLeft = 1;
            if (this.willHitEnemy(attackingPokemon.getPokemon(), defendingPokemon.getPokemon(), field, attackerField,
                    defenderField, isFirstMove)) {
                strikesLeft++;
                if (this.willHitEnemy(attackingPokemon.getPokemon(), defendingPokemon.getPokemon(), field, attackerField,
                        defenderField, isFirstMove)) {
                    strikesLeft++;
                }
            }
        }
    }

    public void resetStrikes() {
        strikesLeft = -1;
    }

    public int getStrikesLeft() {
        return strikesLeft;
    }

    public boolean isChargingSkill() {
        //TODO: Create boolean variables for the charging moves (fly, dig, dive, sky drop etc) and then check if any are set to true.
        return false;
    }

    public boolean isSolarBeam() {
        //TODO: Create boolean variable for solar beam and check if true.
        return false;
    }
}
