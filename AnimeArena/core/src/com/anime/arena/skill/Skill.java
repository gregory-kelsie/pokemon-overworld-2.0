package com.anime.arena.skill;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.field.WeatherType;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;

import java.util.List;

public class Skill {
    protected int id;
    protected String name;
    protected String description;
    protected int subtype;
    protected SkillCategory category;
    protected int maxPP;
    protected int currentPP;
    protected int basePower;
    protected int accuracy;
    protected int speedPriority;
    protected SkillTarget target;
    protected PokemonType moveType;

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

    protected boolean ignoreTargetStatChanges;
    protected int damageTally;

    public Skill(String name, String description, SkillCategory category, int pp, int currentPP, int accuracy, PokemonType moveType,
                 SkillTarget target, int subtype, int speedPriority, int basePower) {
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
        this.basePower = basePower;
        initMisc();
    }

    private void initMisc() {
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
    public void use(BattlePokemon skillUser, BattlePokemon enemyPokemon,
                    int skillUserPartyPosition, int enemyPokemonPartyPosition, Field field, SubField userField,
                    SubField enemyField, boolean isFirstAttack,
                    Skill targetSkill, List<BattlePokemon> skillUserParty, List<BattlePokemon> enemyPokemonParty) {
        skillUser.removeRage();
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
            double enemyEvasionMod = enemyPokemon.getEvasionModifier(evasionStage);
            double result = getAccuracyMod() * attackerAccuracyMod * enemyEvasionMod;

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
    public double getAccuracyMod() {
        return accuracy / 100.0;
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
}
