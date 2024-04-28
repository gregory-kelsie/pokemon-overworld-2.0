package com.anime.arena.skill;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.field.WeatherType;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;
import com.anime.arena.pokemon.StatusCondition;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

import static com.anime.arena.skill.RecoilDrainType.*;

public class DamageSkill extends EffectSkill {
    private boolean bindsEnemy;
    private int criticalRate; //Initial Crit Stage
    private int effectRate; //Effect Rate for Secondary Effect Damaging Moves
    private RecoilDrainType recoilType;
    protected boolean reverseCategory; //For Sacred Sword (calculates user's special attack and hits defense instead of sp def)
    protected boolean usesEnemyAttack; //For moves like Foul play.
    protected double extraMod; //For skills that double damage for certain conditions ex: Brine
    protected boolean facadeEffect;

    public DamageSkill(int id, String name, String description, SkillCategory category, int pp, int currentPP, int accuracy, PokemonType moveType,
                       SkillTarget target, int subtype, int speedPriority, int basePower, int criticalRate) {
        super(id, name, description, category, pp, currentPP, accuracy, moveType, target, subtype, speedPriority, basePower);
        this.criticalRate = criticalRate;
        initMisc();
    }

    public DamageSkill(int id, String name, String description, SkillCategory category, int pp, int currentPP, int accuracy, PokemonType moveType,
                       SkillTarget target, int subtype, int speedPriority, int basePower, int criticalRate, int effectRate) {
        super(id, name, description, category, pp, currentPP, accuracy, moveType, target, subtype, speedPriority, basePower);
        this.criticalRate = criticalRate;
        this.effectRate = effectRate;
        initMisc();
    }

    /**
     * Use the damage skill on the enemy pokemon.
     * @param skillUser The Pokemon using the skill
     * @param enemyPokemon The enemy receiving the skill
     * @param skillUserPartyPosition
     * @param enemyPokemonPartyPosition
     * @param field The field for the battle.
     * @param userField The field for the battle.
     * @param enemyField The field for the battle.
     * @param isFirstAttack Whether or not the skill was used first in the clash
     * @param targetSkill
     * @param skillUserParty
     * @param enemyPokemonParty
     * @return The skill results.
     * */
    @Override
    public List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon,
                            int skillUserPartyPosition, int enemyPokemonPartyPosition, Field field, SubField userField,
                            SubField enemyField, boolean isFirstAttack,
                            Skill targetSkill, List<BattlePokemon> skillUserParty, List<BattlePokemon> enemyPokemonParty) {
        List<String> results = new ArrayList<String>();
        refreshMoveCounters(skillUser);
        boolean heldOnWithSturdy = false;
        //TODO: Set battle type ex: Aerialate switch normal moves to flying
        boolean hasCrit = calcCrit(skillUser, enemyPokemon, field);

        //Add Effectiveness results
        if (moveIsSuperEffective(enemyPokemon)
                //|| (id == SkillFactory.FREEZE_DRY && (enemyPokemon.getFirstType() == PokemonType.WATER || enemyPokemon.getSecondType() == PokemonType.WATER))
        ) {
            results.add("It was super effective!");
        } else if (moveIsNotVeryEffective(enemyPokemon)) {
            results.add("It was not very effective...");
        }

        //Add critical hit text
        if (hasCrit) {
            results.add("Critical Hit!");
        }

        //Calculate the damage results
        int damage = getDamage(skillUser, enemyPokemon, field, userField, enemyField, hasCrit);

        //Prevent Overkill.
        if (damage > enemyPokemon.getCurrentHealth()) {
            if (enemyPokemon.hasFullHealth() && enemyPokemon.getAbility() == AbilityId.STURDY) {
                damage = enemyPokemon.getCurrentHealth() - 1;
                heldOnWithSturdy = true;
            } else {
                damage = enemyPokemon.getCurrentHealth();
            }
        }

        //Let the Pokemon know how much damage they've taken in case
        //they can return 1.5x damage with a Counter/Metal Burst etc.
        if (isFirstAttack) {
            enemyPokemon.setTurnDamageTaken(damage, category);
        }
        enemyPokemon.subtractHealth(damage);
        damageTally += damage; //Keep record of damage for multi-hit-moves
        //results.add("Dealt " + damage + " damage.");

        //Increase attack if the enemy previously used rage.
        if (enemyPokemon.usedRage()) {
            //Attempt to increase the attack stage.
            if (enemyPokemon.getAttackStage() < 6) {
                enemyPokemon.increaseAttackStage(1);
                results.add(enemyPokemon.getName() + "'s attack rose!");
            }
        }

        if (subtype == 1) { //Secondary Effect Move
            results.addAll(useEffects(skillUser, enemyPokemon, field, userField, enemyField, isFirstAttack));
        }

        if (heldOnWithSturdy) {
            results.add(enemyPokemon.getName() + " held on with Sturdy!");
        }
        //Subtract recoil damage.
        if (recoilType == ONE_THIRD && skillUser.getAbility() != AbilityId.ROCK_HEAD) {
            skillUser.subtractHealth((int) Math.ceil(damage / 3.0));
            skillUser.takeDamageThisTurn();
        } else if (recoilType == ONE_HALF && skillUser.getAbility() != AbilityId.ROCK_HEAD) {
            skillUser.subtractHealth((int) Math.ceil(damage / 2.0));
            skillUser.takeDamageThisTurn();
        } else if (recoilType == ONE_FOURTH && skillUser.getAbility() != AbilityId.ROCK_HEAD) {
            skillUser.subtractHealth((int) Math.ceil(damage / 4.0));
            skillUser.takeDamageThisTurn();
        } else if (recoilType == GAIN_HALF) {
            skillUser.addHealth((int) Math.ceil(damage / 2.0));
        } else if (recoilType == GAIN_THREE_QUARTERS) {
            skillUser.addHealth((int) Math.ceil(damage * 0.75));
        }
        return results;
    }

    public void setEffectRate(int effectRate) {
        this.effectRate = effectRate;
    }

    private void initMisc() {
        this.recoilType = RecoilDrainType.NO_RECOIL;
        this.reverseCategory = false;
        this.usesEnemyAttack = false;
        this.extraMod = 1;
        this.facadeEffect = false;
        this.ignoreTargetStatChanges = false;
        this.damagesEnemy = true;
    }

    /**
     * Return whether or not the skill has an effect on the
     * enemy Pokemon.
     * @param enemy The enemy pokemon the skill is being tested on.
     * @return Whether or not the skill has an effect on the
     * enemy Pokemon.
     */
    protected boolean effectsEnemy(BattlePokemon enemy) {
        //TODO: Implement Odor Sleuth and the ability that hits ghost pokemon here.
        if (enemy.getResistances().get(moveType) != 0) {
            return true;
        }
        return false;
    }

    /**
     * Determine if the move is super effective on the enemy pokemon
     * @param enemyPokemon The pokemon we are comparing the move to.
     * @return Whether or not the move is super effective on the enemy
     * Pokemon.
     */
    private boolean moveIsSuperEffective(BattlePokemon enemyPokemon) {
        if (enemyPokemon.getResistances().get(moveType) > 1) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Determine if the move is not very effective on the enemy pokemon
     * @param enemyPokemon The pokemon we are comparing the move to.
     * @return Whether or not the move is not very effective on the enemy
     * Pokemon.
     */
    private boolean moveIsNotVeryEffective(BattlePokemon enemyPokemon) {
        if (enemyPokemon.getResistances().get(moveType) < 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return whether or not the move crit.
     * @param user The skill's user
     * @param enemy The enemy pokemon
     * @param field The field for the battle
     * @return Whether or not the move crit.
     */
    private boolean calcCrit(BattlePokemon user, BattlePokemon enemy, Field field) {
        if (enemy.getAbility() == AbilityId.SHELL_ARMOR ||
                enemy.getAbility() == AbilityId.BATTLE_ARMOR) {
            return false;
        }
        if (criticalRate == -1 || user.hasCrit()) { //-1 Means always crits.
            return true;
        }
        int critStage = criticalRate;
        if (user.isFocused()) {
            critStage += 2;
        }
        if (user.getAbility() == AbilityId.SUPER_LUCK) {
            critStage *= 2;
        }
        //TODO: Add hold item bonus, add lucky chant prevention.
        double rand = Math.random();

        switch(critStage) {
            case 1:
                if (rand <= 0.0625) {
                    return true;
                }
                return false;
            case 2:
                if (rand <= 0.125) {
                    return true;
                }
                return false;
            case 3:
                if (rand <= 0.5) {
                    return true;
                }
                return false;
            case 4:
                return true;

        }
        return false;



    }

    /**
     * Calculate the modifier in the damage calculation formula
     * http://bulbapedia.bulbagarden.net/wiki/Damage
     * @param user The skill user
     * @param enemy The skill receiver
     * @param field The battle field.
     * @param hasCrit Whether or not the skill crit.
     * @return The modifier value in the damage calculation formula.
     */
    private double getModifier(BattlePokemon user, BattlePokemon enemy, Field field, boolean hasCrit) {
        double crit = this.getCritMultiplier(user, hasCrit);
        double resistMod = getResistModifier(user, enemy);
        double stabMod = getStabModifier(user);
        double abilityMod = getAbilityMod(user, field);
        double defenseAbilityMod = getDefenseAbilityMod(enemy);
        double weatherMod = getWeatherMod(field);
        double randVal = Math.random() * (0.15) + 0.85;
        double burnMod = getBurnMod(user);
        Gdx.app.log("DamageMod", user.getPokemon().getConstantVariables().getName() + "\ncrit: " + crit + "\n" +
                "resistMod: " + resistMod + "\n" +
                "stabMod: " + stabMod + "\n" +
                "abilityMod: " + abilityMod + "\n" +
                "defenseAbilityMod: " + defenseAbilityMod + "\n" +
                "weatherMod: " + weatherMod + "\n" +
                "randVal: " + randVal + "\n" +
                "burnMod: " + burnMod + "\n" +
                "extraMod: " + extraMod);
        return  (stabMod * resistMod * crit * abilityMod * defenseAbilityMod * weatherMod * randVal * burnMod * extraMod);
    }

    /**
     * 	Return the attacker's STAB modifier
     *	@param user - The Attacker
     */
    public double getStabModifier(BattlePokemon user) {
        //Check if the user's type is the same as the move type.
        if (user.getFirstType() == moveType || user.getSecondType() == moveType) {
            if (user.getAbility() == AbilityId.ADAPTABILITY) {
                //Adaptability makes the STAB bonus 2 instead of 1
                return 2;
            }
            return 1.5;
        } else {
            return 1;
        }
    }

    /**
     * 	Return the effectiveness modifier (0.25, 0.5, 1, 2, 4)
     *	@param user - The Attacker
     *	@param enemy - The pokemon that gets attacked
     */
    protected double getResistModifier(BattlePokemon user, BattlePokemon enemy) {
        double resistMod = enemy.getResistances().get(moveType);
        boolean superEffective = false;
        boolean notVeryEffective = false;
        if (resistMod > 1) {
            superEffective = true;
        } else if (resistMod < 1) {
            notVeryEffective = true;
        }
        if (enemy.getAbility() == AbilityId.FILTER ||
                enemy.getAbility() == AbilityId.SOLID_ROCK) {
            //Filter and Solid Rock reduce super effective moves by 1/4
            if (superEffective) {
                resistMod *= 0.75;
            }
        }
        if (user.getAbility() == AbilityId.TINTED_LENS) {
            //Power of not very effective moves is doubled.
            if (notVeryEffective) {
                resistMod *= 2;
            }
        }
        return resistMod;
    }


    /**
     * 	Return the crit multiplier when determining the damage dealt.
     *	@param user - The Attacker
     *	@param hasCrit - Whether or not the attacker got a crit
     */
    private double getCritMultiplier(BattlePokemon user, boolean hasCrit) {
        if (hasCrit) {
            if (user.getAbility() == AbilityId.SNIPER) {
                return 2.25;
            }  else {
                return 1.5;
            }
        } else {
            return 1;
        }
    }


    /**
     * Return the burn damage reduction on the skill.
     * Only reduces damage if the attack is a physical attack or the user
     * doesn't have guts or the skill isn't facade.
     * @param user The Attacker
     * @return The burn modifier.
     */
    private double getBurnMod (BattlePokemon user) {
        if (user.isBurned()) {
            if (user.getAbility() == AbilityId.GUTS ||
                    facadeEffect || category != SkillCategory.PHYSICAL) {
                return 1;
            }
            return 0.5;
        } else {
            return 1;
        }
    }
    /**
     * 	Return the attacker's ability multiplier when determining the damage dealt.
     *	@param user - The Attacker
     */
    private double getAbilityMod(BattlePokemon user, Field field) {
        //Health below 1/3 ability mods
        if (user.getPokemon().getCurrentHealth() <= user.getPokemon().getHealthStat() * 0.33) {
            if (moveType == PokemonType.FIRE) {
                if (user.getAbility() == AbilityId.BLAZE) {
                    return 1.5;
                }
            } else if (moveType == PokemonType.WATER) {
                if (user.getAbility() == AbilityId.TORRENT) {
                    return 1.5;
                }
            }	else if (moveType == PokemonType.GRASS) {
                if (user.getAbility() == AbilityId.OVERGROW) {
                    return 1.5;
                }
            }	else if (moveType == PokemonType.BUG) {
                if (user.getAbility() == AbilityId.SWARM) {
                    return 1.5;
                }
            }
        }
        if (user.getAbility() == AbilityId.SAND_FORCE) {
            if (moveType == PokemonType.GROUND ||
                    moveType == PokemonType.STEEL ||
                    moveType == PokemonType.ROCK) {
                return 1.3;
            }
        }

        if (user.getAbility() == AbilityId.SOLAR_POWER) {
            if (field.getWeatherType() == WeatherType.HARSH_SUNSHINE ||
                    field.getWeatherType() == WeatherType.SUN) {
                if (category == SkillCategory.SPECIAL) {
                    return 1.5;
                }
            }
        }

        //Status ability mods
        if (user.getAbility() == AbilityId.GUTS &&
                (user.isBurned() || user.isParalyzed() || user.isPoisoned())) {
            return 1.5;
        }

        //Regular ability mods.
        if (user.getAbility() == AbilityId.HUSTLE) {
            return 1.5;
        }

        //No ability mods
        return 1;
    }

    /**
     * 	Return the defender's ability multiplier when determining the damage taken.
     *	@param enemy - The pokemon that's getting attacked
     */
    private double getDefenseAbilityMod(BattlePokemon enemy) {
        //Ability mods when getting hit by fire
        if (moveType == PokemonType.FIRE) {
            if (enemy.getAbility() == AbilityId.THICK_FAT ||
                    enemy.getAbility() == AbilityId.HEATPROOF) {
                return 0.5;
            } else if (enemy.getAbility() == AbilityId.DRY_SKIN) {
                return 1.25;
            }
        }
        //Ability mods when getting hit by ice
        else if (moveType == PokemonType.ICE) {
            if (enemy.getAbility() == AbilityId.THICK_FAT) {
                return 0.5;
            }
        }

        //No defense ability mods
        return 1;
    }


    /**
     * Return the weather modifier
     * @param field The current battle field.
     * @return Return the weather modifier
     */
    private double getWeatherMod(Field field) {
        if (field.getWeatherType() == WeatherType.SUN) {
            //Fire increase and Water decrease in damage during sunlight.
            if (moveType == PokemonType.FIRE) {
                return 1.5;
            } else if (moveType == PokemonType.WATER) {
                return 0.5;
            }
        } else if (field.getWeatherType() == WeatherType.RAIN) {
            //Fire decrease and water increase in rain
            if (moveType == PokemonType.FIRE) {
                return 0.5;
            } else if (moveType == PokemonType.WATER) {
                return 1.5;
            }
        }
        //1 is the default mod
        return 1;
    }

    /**
     * Calculate the Physical attack stat of the Pokemon after taking into account
     * stages and crit.
     * @param user The Pokemon's attack stat being calculated.
     * @param hasCrit Whether or not the attack crits.
     * @return The working attack stat of the attacker.
     */
    protected double getPhysicalAttack(BattlePokemon user, boolean hasCrit) {
        double atkStat = user.getPokemon().getAttackStat();

        //Double attack if the Pokemon has the ability huge power or pure power
        if (user.getAbility() == AbilityId.PURE_POWER ||
                user.getAbility() == AbilityId.HUGE_POWER) {
            atkStat *= 2;
        }
        //Attack stage is above the normal
        if (user.getAttackStage() >= 0) {
            atkStat = atkStat * (1 + (0.5 * user.getAttackStage()));
        }//Attack stage is below the normal
        else {
            //Ignore negative attack stage
            if (!hasCrit) {
                if (user.getAttackStage() == -1) {
                    atkStat *= 0.66;
                } else if (user.getAttackStage() == -2) {
                    atkStat *= 0.5;
                } else if (user.getAttackStage() == -3) {
                    atkStat *= 0.4;
                } else if (user.getAttackStage() == -4) {
                    atkStat *= 0.33;
                } else if (user.getAttackStage() == -5) {
                    atkStat *= 0.29;
                } else {
                    atkStat *= 0.25;
                }
            }
        }
        return atkStat;
    }

    /**
     * Return the amount of damage the skill will do to the enemy on the current
     * field.
     * @param user The skill user
     * @param enemy The skill receiver
     * @param field The current battle field
     * @param hasCrit Whether or not the skill will crit.
     * @return
     */
    protected int getDamage(BattlePokemon user, BattlePokemon enemy, Field field, SubField userField,
                            SubField targetField, boolean hasCrit) {
        double atkStat;
        double defStat;

        //CALCULATE ATTACK STAT
        if (this.category == SkillCategory.PHYSICAL) {
            if (usesEnemyAttack) {
                atkStat = getPhysicalAttack(enemy, hasCrit);
            } else {
                atkStat = getPhysicalAttack(user, hasCrit);
            }
        } else {
            atkStat = user.getPokemon().getSpecialAttackStat();
            //Attack stage is above the normal
            if (user.getSpecialAttackStage() >= 0) {
                atkStat = atkStat * (1 + (0.5 * user.getSpecialAttackStage()));
            }
            //Attack stage is below the normal
            else {
                //Ignore negative attack stage
                if (!hasCrit) {
                    if (user.getSpecialAttackStage() == -1) {
                        atkStat *= 0.66;
                    } else if (user.getSpecialAttackStage() == -2) {
                        atkStat *= 0.5;
                    } else if (user.getSpecialAttackStage() == -3) {
                        atkStat *= 0.4;
                    } else if (user.getSpecialAttackStage() == -4) {
                        atkStat *= 0.33;
                    } else if (user.getSpecialAttackStage() == -5) {
                        atkStat *= 0.29;
                    } else {
                        atkStat *= 0.25;
                    }
                }
            }
        }

        //CALCULATE DEFENSE STAT
        if ((category == SkillCategory.PHYSICAL && !reverseCategory) ||
                (category == SkillCategory.SPECIAL && reverseCategory)) {
            defStat = enemy.getPokemon().getDefenseStat();
            if (targetField != null && targetField.hasReflect()) {
                defStat *= 2;
            }
            if (!ignoreTargetStatChanges) {
                //Defense stage is above normal
                if (enemy.getDefenseStage() >= 0) {
                    //Ignore defense bonus on crit
                    if (!hasCrit) {
                        defStat = defStat * (1 + (0.5 * enemy.getDefenseStage()));
                    }
                }
                //Defense stage is below normal
                else {
                    if (enemy.getDefenseStage() == -1) {
                        defStat *= 0.66;
                    } else if (enemy.getDefenseStage() == -2) {
                        defStat *= 0.5;
                    } else if (enemy.getDefenseStage() == -3) {
                        defStat *= 0.4;
                    } else if (enemy.getDefenseStage() == -4) {
                        defStat *= 0.33;
                    } else if (enemy.getDefenseStage() == -5) {
                        defStat *= 0.29;
                    } else {
                        defStat *= 0.25;
                    }
                }
            }
        }
        else {
            defStat = enemy.getPokemon().getSpecialDefenseStat();
            if (targetField != null && targetField.hasLightScreen()) {
                defStat *= 2;
            }
            if (field.getWeatherType() == WeatherType.SAND &&
                    (enemy.getFirstType() == PokemonType.ROCK ||
                            enemy.getSecondType() == PokemonType.ROCK)) {
                defStat *= 1.5;
            }
            if (!ignoreTargetStatChanges) {
                if (enemy.getSpecialDefenseStage() >= 0) {
                    //Ignore defense bonus on crit
                    if (!hasCrit) {
                        defStat = defStat * (1 + (0.5 * enemy.getSpecialDefenseStage()));
                    }
                }
                //Defense stage is below normal
                else {
                    if (enemy.getSpecialDefenseStage() == -1) {
                        defStat *= 0.66;
                    } else if (enemy.getSpecialDefenseStage() == -2) {
                        defStat *= 0.5;
                    } else if (enemy.getSpecialDefenseStage() == -3) {
                        defStat *= 0.4;
                    } else if (enemy.getSpecialDefenseStage() == -4) {
                        defStat *= 0.33;
                    } else if (enemy.getSpecialDefenseStage() == -5) {
                        defStat *= 0.29;
                    } else {
                        defStat *= 0.25;
                    }
                }
            }
        }

        //Sport modifiers
        double basePowerModifier = 1;
        if (moveType == PokemonType.FIRE && field.hasWaterSport()) {
            basePowerModifier = .43;
        }
        Gdx.app.log("DamageMod", "atkStat: " + atkStat + ", defStat: " + defStat);
        //Sub values into the formulas from bulbapedia.
        double ls = (((((2 * user.getPokemon().getUniqueVariables().getLevel()) / 5.0) + 2) * (atkStat / defStat) * basePower * basePowerModifier) / 50.0) + 2;
        double mod = getModifier(user, enemy, field, hasCrit);
        double dmg = ls * mod;
        return (int)Math.ceil(dmg);
    }

    public boolean isBindsEnemy() {
        return bindsEnemy;
    }

    public void setBindsEnemy(boolean bindsEnemy) {
        this.bindsEnemy = bindsEnemy;
    }

    public int getCriticalRate() {
        return criticalRate;
    }

    public void setCriticalRate(int criticalRate) {
        this.criticalRate = criticalRate;
    }

    public RecoilDrainType getRecoilType() {
        return recoilType;
    }


    public boolean hasRecoil() {
        if (recoilType == ONE_FOURTH || recoilType == ONE_HALF || recoilType == ONE_FOURTH) {
            return true;
        }
        return false;
    }

    public boolean hasDrain() {
        if (recoilType == GAIN_HALF || recoilType == GAIN_THREE_QUARTERS) {
            return true;
        }
        return false;
    }

    public void setRecoilType(RecoilDrainType recoilType) {
        this.recoilType = recoilType;
    }

    public boolean isReverseCategory() {
        return reverseCategory;
    }

    public void setReverseCategory(boolean reverseCategory) {
        this.reverseCategory = reverseCategory;
    }

    public boolean isUsesEnemyAttack() {
        return usesEnemyAttack;
    }

    public void setUsesEnemyAttack(boolean usesEnemyAttack) {
        this.usesEnemyAttack = usesEnemyAttack;
    }
}
