package com.anime.arena.pokemon;

import static com.anime.arena.pokemon.PokemonUtils.*;

public class Pokemon {
    private BasePokemon constantVariables;
    private UniquePokemon uniqueVariables;
    public Pokemon(BasePokemon constantVariables, UniquePokemon uniqueVariables) {
        this.constantVariables = constantVariables;
        this.uniqueVariables = uniqueVariables;
    }

    public void setBasePokemon(BasePokemon newPokemon) {
        this.constantVariables = newPokemon;
    }

    public BasePokemon getConstantVariables() {
        return constantVariables;
    }

    public UniquePokemon getUniqueVariables() {
        return uniqueVariables;
    }

    public boolean isMaxLevel() {
        return uniqueVariables.getLevel() == 100;
    }

    public int getCurrentHealth() {
        return uniqueVariables.getCurrentHealth();
    }

    public boolean isFainted() {
        if (uniqueVariables.getCurrentHealth() == 0) {
            return true;
        }
        return false;
    }

    public void setCurrentHealth(int newHealth) {
        if (newHealth >= 0 && newHealth <= getHealthStat()) {
            uniqueVariables.setCurrentHealth(newHealth);
        }
    }

    /**
     * Return the exp required to reach the next level.
     * @return The exp required to reach the next level.
     */
    public int getNextLevelExp() {
        if (getUniqueVariables().getLevel() == 100) {
            return 0;
        } else {
            return getTotalExp(getUniqueVariables().getLevel() + 1) - getTotalExp(getUniqueVariables().getLevel());
        }
    }

    public int getTotalExp() {
        return getTotalExp(getUniqueVariables().getLevel()) + (int) Math.round(getUniqueVariables().getCurrentExp());
    }

    public void levelUp() {

    }

    /**
     * Return the total amount of exp at the specified level
     * @param level The level in which we are looking at for total exp
     * @return The total exp at level level.
     */
    public int getTotalExp(int level) {
        //Determine the exp growth rate and then get the total exp.
        if (constantVariables.getExpType() == ExpType.FLUCTUATING) {
            return getFluctuatingTotalExp(level);
        }
        else if (constantVariables.getExpType() == ExpType.MEDIUM_FAST) {
            return getMediumFastTotalExp(level);
        }
        else if (constantVariables.getExpType() == ExpType.MEDIUM_SLOW) {
            return getMediumSlowTotalExp(level);
        }
        else if (constantVariables.getExpType() == ExpType.ERRATIC) {
            return getErraticTotalExp(level);
        }
        else if (constantVariables.getExpType() == ExpType.SLOW) {
            return getSlowTotalExp(level);
        }
        else {
            //The last exp growth rate is ExpType.FAST
            return getFastTotalExp(level);
        }
    }

    /**
     * Return the total amount of exp at the specified level
     * for a fluctuating exp growth rate.
     * @param level The level in which we are looking at for total exp
     * @return The total exp at level level.
     */
    private int getFluctuatingTotalExp(int level) {
        if (level <= 15)
        {
            return (int)Math.round(Math.pow(level, 3) * ((Math.floor((level + 1) / 3) + 24) / 50.0));
        }
        else if(level > 15 && level <= 36)
        {
            return (int)Math.round(Math.pow(level, 3) * ((level + 14) / 50.0));
        }
        else
        {
            return (int)Math.round(Math.pow(level, 3) * ((Math.floor(level / 2) + 32) / 50.0));
        }
    }

    /**
     * Return the total amount of exp at the specified level
     * for a medium fast exp growth rate.
     * @param level The level in which we are looking at for total exp
     * @return The total exp at level level.
     */
    private int getMediumFastTotalExp(int level) {
        return (int)Math.round(Math.pow(level, 3));
    }

    /**
     * Return the total amount of exp at the specified level
     * for a medium slow exp growth rate.
     * @param level The level in which we are looking at for total exp
     * @return The total exp at level level.
     */
    private int getMediumSlowTotalExp(int level) {
        if (level == 1) {
            return 0;
        }
        else {
            return (int)Math.round((6/5.0 * Math.pow(level, 3)) - (15 * Math.pow(level, 2)) + (100 * level) - 140);
        }
    }

    /**
     * Return the total amount of exp at the specified level
     * for a slow exp growth rate.
     * @param level The level in which we are looking at for total exp
     * @return The total exp at level level.
     */
    private int getSlowTotalExp(int level) {
        return (int)Math.round(5 * Math.pow(level, 3) / 4.0);
    }

    /**
     * Return the total amount of exp at the specified level
     * for a fast exp growth rate.
     * @param level The level in which we are looking at for total exp
     * @return The total exp at level level.
     */
    private int getFastTotalExp(int level) {
        return (int)Math.round(4 * Math.pow(level, 3) / 5.0);
    }

    /**
     * Return the total amount of exp at the specified level
     * for an erratic exp growth rate.
     * @param level The level in which we are looking at for total exp
     * @return The total exp at level level.
     */
    private int getErraticTotalExp(int level) {
        if (level <= 50)
        {
            return (int)Math.round((Math.pow(level, 3) * (100 - level)) / 50.0);
        }
        else if (level > 50 && level <= 68)
        {
            return (int)Math.round((Math.pow(level, 3) * (150 - level)) / 100.0);
        }
        else if (level > 68 && level <= 98)
        {
            return (int)Math.round(Math.pow(level, 3) * Math.floor((1911 - 10 * level) / 3) / 500.0);
        }
        else
        {
            return (int)Math.round((Math.pow(level, 3) * (160 - level)) / 100.0);
        }
    }

    /**
     * Return the Pokemon's health stat, total health
     * @return Total Health stat
     */
    public int getHealthStat() {
        return (int)Math.round(((2 * constantVariables.getBaseStatHealth() + uniqueVariables.getIvs()[HEALTH] +
                (uniqueVariables.getEvs()[HEALTH] / 4.0) + 100) * uniqueVariables.getLevel()) / 100.0) + 10;
    }

    /**
     * Return the Pokemon's attack stat
     * @return Attack Stat
     */
    public int getAttackStat() {
        return (int)Math.round(((((2 * constantVariables.getBaseStatAttack() + uniqueVariables.getIvs()[ATTACK] +
                (uniqueVariables.getEvs()[ATTACK] / 4.0)) * uniqueVariables.getLevel())
                / 100.0) + 5) * uniqueVariables.getAttackNatureMultiplier());
    }

    /**
     * Return the Pokemon's special attack stat.
     * @return Special Attack Stat
     */
    public int getSpecialAttackStat() {
        return (int)Math.round(((((2 * constantVariables.getBaseStatSpeicialAttack() + uniqueVariables.getIvs()[SPECIAL_ATTACK] +
                (uniqueVariables.getEvs()[SPECIAL_ATTACK] / 4.0)) * uniqueVariables.getLevel())
                / 100.0) + 5) * uniqueVariables.getSpAttackNatureMultiplier());
    }

    /**
     * Return the Pokemon's defense stat.
     * @return Defense Stat
     */
    public int getDefenseStat() {
        return (int)Math.round(((((2 * constantVariables.getBaseStatDefense() + uniqueVariables.getIvs()[DEFENSE] +
                (uniqueVariables.getEvs()[DEFENSE] / 4.0)) * uniqueVariables.getLevel())
                / 100.0) + 5) * uniqueVariables.getDefenseNatureMultiplier());
    }

    /**
     * Return the Pokemon's special defense stat.
     * @return Special Defense Stat
     */
    public int getSpecialDefenseStat() {
        return (int)Math.round(((((2 * constantVariables.getBaseStatSpecialDefense() + uniqueVariables.getIvs()[SPECIAL_DEFENSE] +
                (uniqueVariables.getEvs()[SPECIAL_DEFENSE] / 4.0)) * uniqueVariables.getLevel())
                / 100.0) + 5) * uniqueVariables.getSpDefenseNatureMultiplier());
    }

    /**
     * Return the Pokemon's speed stat.
     * @return Speed Stat
     */
    public int getSpeedStat() {
        return (int)Math.round(((((2 * constantVariables.getBaseStatSpeed() + uniqueVariables.getIvs()[SPEED] +
                (uniqueVariables.getEvs()[SPEED] / 4.0)) * uniqueVariables.getLevel())
                / 100.0) + 5) * uniqueVariables.getSpeedNatureMultiplier());
    }
}
