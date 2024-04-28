package com.anime.arena.pokemon;

import com.anime.arena.skill.Skill;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

import static com.anime.arena.pokemon.PokemonUtils.*;

public class UniquePokemon {
    private char gender;
    private int ability;
    private int abilityPosition;
    private Nature nature;
    private int level;
    private int[] ivs;
    private int[] evs;

    private int currentHealth;
    private double currentExp;

    private StatusCondition status;

    private List<Skill> moves;


    public UniquePokemon() {
        moves = new ArrayList<Skill>();
    }

    public UniquePokemon(int level, char gender, Nature nature, int abilityPosition, int[] ivs, int[] evs) {
        this.level = level;
        this.gender = gender;
        this.nature = nature;
        this.abilityPosition = abilityPosition;
        this.ivs = ivs;
        this.evs = evs;
    }


    public void initBlankData(int level) {
        this.level = level;
        initBlankEVs();
        initRandomIVs();
        setRandomNature();
        currentExp = 0;
        status = StatusCondition.STATUS_FREE;

    }

    public List<Skill> getMoves() {
        return moves;
    }

    public boolean hasMove(int moveID) {
        for (Skill currentSkill : moves) {
            if (currentSkill.getId() == moveID) {
                return true;
            }
        }
        return false;
    }


    /**
     * Initialize an empty EV set. (All = 0)
     */
    private void initBlankEVs() {
        evs = new int[6];
        evs[HEALTH] = 0;
        evs[ATTACK] = 0;
        evs[SPECIAL_ATTACK] = 0;
        evs[SPECIAL_DEFENSE] = 0;
        evs[DEFENSE] = 0;
        evs[SPECIAL_DEFENSE] = 0;
        evs[SPEED] = 0;
    }

    private void initRandomIVs() {
        ivs = new int[6];
        ivs[HEALTH] = PokemonUtils.createRandomIV();
        ivs[ATTACK] = PokemonUtils.createRandomIV();
        ivs[SPECIAL_ATTACK] = PokemonUtils.createRandomIV();
        ivs[DEFENSE] = PokemonUtils.createRandomIV();
        ivs[SPECIAL_DEFENSE] = PokemonUtils.createRandomIV();
        ivs[SPEED] = PokemonUtils.createRandomIV();
    }


    public int getAbilityPosition() {
        return abilityPosition;
    }

    public void setAbilityPosition(int abilityPosition) {
        this.abilityPosition = abilityPosition;
    }

    /**
     * Set a random nature for the Pokemon.
     */
    private void setRandomNature() {
        Nature[] natures = Nature.values();
        int value = (int)Math.round(Math.random() * (natures.length - 1));
        nature = natures[value];
    }



    /**
     * Initialize the Nature multipliers
     */
    public double getAttackNatureMultiplier() {
        if (nature == Nature.LONELY || nature == Nature.ADAMANT || nature == Nature.NAUGHTY || nature == Nature.BRAVE) {
            return INCREASED_MULTIPLIER;
        } else if (nature == Nature.BOLD || nature == Nature.MODEST || nature == Nature.CALM || nature == Nature.TIMID) {
            return REDUCED_MULTIPLIER;
        } else {
            return NORMAL_MULTIPLIER;
        }
    }

    public double getDefenseNatureMultiplier() {
        if (nature == Nature.BOLD || nature == Nature.IMPISH || nature == Nature.LAX || nature == Nature.RELAXED) {
            return INCREASED_MULTIPLIER;
        } else if (nature == Nature.LONELY || nature == Nature.MILD || nature == Nature.GENTLE || nature == Nature.HASTY) {
            return REDUCED_MULTIPLIER;
        } else {
            return NORMAL_MULTIPLIER;
        }
    }

    public double getSpAttackNatureMultiplier() {
        if (nature == Nature.MODEST || nature == Nature.MILD || nature == Nature.RASH || nature == Nature.QUIET) {
            return INCREASED_MULTIPLIER;
        } else if (nature == Nature.ADAMANT || nature == Nature.IMPISH || nature == Nature.CAREFUL || nature == Nature.JOLLY) {
            return REDUCED_MULTIPLIER;
        } else {
            return NORMAL_MULTIPLIER;
        }
    }

    public double getSpDefenseNatureMultiplier() {
        if (nature == Nature.CALM || nature == Nature.CAREFUL || nature == Nature.GENTLE || nature == Nature.SASSY) {
            return INCREASED_MULTIPLIER;
        } else if (nature == Nature.NAUGHTY || nature == Nature.LAX || nature == Nature.RASH || nature == Nature.NAIVE) {
            return REDUCED_MULTIPLIER;
        } else {
            return NORMAL_MULTIPLIER;
        }
    }

    public double getSpeedNatureMultiplier() {
        if (nature == Nature.TIMID || nature == Nature.HASTY || nature == Nature.JOLLY || nature == Nature.NAIVE) {
            return INCREASED_MULTIPLIER;
        } else if (nature == Nature.BRAVE || nature == Nature.RELAXED || nature == Nature.QUIET || nature == Nature.SASSY) {
            return REDUCED_MULTIPLIER;
        } else {
            return NORMAL_MULTIPLIER;
        }
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public int getAbility() {
        return ability;
    }

    public void setAbility(int ability) {
        this.ability = ability;
    }

    public Nature getNature() {
        return nature;
    }

    public void setNature(Nature nature) {
        this.nature = nature;
    }

    public int getLevel() {
        return level;
    }



    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Level up the Pokemon. Can't exceed the max level of 100.
     */
    public void levelUp() {
        level = Math.min(100, level + 1);
        currentExp = 0;
    }


    public int[] getIvs() {
        return ivs;
    }

    public void setIvs(int[] ivs) {
        this.ivs = ivs;
    }

    public int[] getEvs() {
        return evs;
    }

    public void setEvs(int[] evs) {
        this.evs = evs;
    }

    /**
     * Add exp to the Pokemon
     * @param amt The amount of exp to be added.
     */
    public void addExp(double amt) {
        currentExp += amt;
    }

    /**
     * Set the amount of exp to amt.
     * @param amt The amount of exp getting set to the currentExp
     */
    public void setExp(int amt) {
        currentExp = amt;
    }

    /**
     * Return whether or not the Pokemon has the total amount of
     * possible EVs (510)
     * @return Whether or not the Pokemon has the total amount of
     * possible EVs.
     */
    public boolean hasMaxEvs() {
        int sum = getTotalEvs();
        if (sum == TOTAL_EV) {
            return true;
        }
        return false;
    }

    /**
     * Return the total amount of EVs the Pokemon has.
     * @return The total amount of EVs the Pokemon has.
     */
    public int getTotalEvs() {
        return evs[0] + evs[1] + evs[2] +
                evs[3] + evs[4] + evs[5];
    }

    /**
     * Increase the evs of the ev type by an amount
     * @param amount The amount of evs to add to the ev type
     * @param evType The ev type changing
     * @return true or false based on whether the ev type's number of evs changed after this function was called
     */
    public boolean addEV(int amount, int evType) {
        if (!hasMaxEvs()) {
            if ((amount > 0 && evs[evType] < MAX_EV) || (amount < 0 && evs[evType] > 0)) {
                evs[evType] = Math.max(0, (Math.min(MAX_EV, evs[evType] + amount)));
                return true;
            }
        }
        return false;
    }

    /**
     * Add evs to the Pokemon based on the enemyEvYield
     * @param enemyEvYield The enemy's evYield that gets added
     *                     to the Pokemons ev pool.
     */
    public void addEvs(int[] enemyEvYield) {
        int availableEvs = TOTAL_EV - getTotalEvs();
        for (int i = 0; i < evs.length; i++) {
            if (!hasMaxEvs() && availableEvs > 0) {
                //Make sure the ev count is under the maximum evs
                if (evs[i] < MAX_EV) {
                    //Check if the yield is more than what's available to the total evs
                    if (enemyEvYield[i] > availableEvs) {
                        //Check if the yield is more than what's available to the current ev
                        if (evs[i] + enemyEvYield[i] > MAX_EV) {
                            //Determine the number of evs to reach the maximum
                            int toMax = MAX_EV - evs[i];
                            if (toMax < availableEvs) {
                                //Cap off the ev to the max
                                availableEvs -= toMax;
                                evs[i] += toMax;
                            } else {
                                //Just add the available evs left to the stat.
                                availableEvs = 0;
                                evs[i] = Math.min(MAX_EV, evs[i] + availableEvs);
                            }
                        } else {
                            //The stat won't get maxed so just add the remaining evs to the stat
                            evs[i] = Math.min(MAX_EV, evs[i] + availableEvs);
                            availableEvs = 0;
                        }

                    } else {
                        //Add ev yield to the pokemon.
                        if (evs[i] + enemyEvYield[i] > MAX_EV) {
                            availableEvs -= (MAX_EV - evs[i]);
                            evs[i] = MAX_EV;
                        } else {
                            evs[i] += enemyEvYield[i];
                            availableEvs -= enemyEvYield[i];
                        }

                    }
                }
            }
        }
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public double getCurrentExp() {
        return currentExp;
    }

    public void setCurrentExp(double currentExp) {
        this.currentExp = currentExp;
    }

    public StatusCondition getStatus() {
        return status;
    }

    public void setStatus(StatusCondition status) {
        this.status = status;
    }


    //TODO: Skills

}
