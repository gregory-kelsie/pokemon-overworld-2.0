package com.anime.arena.pokemon;

public class ExpUtils {
    /**
     * Return the total amount of exp at the specified level
     * for a fluctuating exp growth rate.
     * @param level The level in which we are looking at for total exp
     * @return The total exp at level level.
     */
    public static int getFluctuatingTotalExp(int level) {
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
    public static int getMediumFastTotalExp(int level) {
        return (int)Math.round(Math.pow(level, 3));
    }

    /**
     * Return the total amount of exp at the specified level
     * for a medium slow exp growth rate.
     * @param level The level in which we are looking at for total exp
     * @return The total exp at level level.
     */
    public static int getMediumSlowTotalExp(int level) {
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
    public static int getSlowTotalExp(int level) {
        return (int)Math.round(5 * Math.pow(level, 3) / 4.0);
    }

    /**
     * Return the total amount of exp at the specified level
     * for a fast exp growth rate.
     * @param level The level in which we are looking at for total exp
     * @return The total exp at level level.
     */
    public static int getFastTotalExp(int level) {
        return (int)Math.round(4 * Math.pow(level, 3) / 5.0);
    }

    /**
     * Return the total amount of exp at the specified level
     * for an erratic exp growth rate.
     * @param level The level in which we are looking at for total exp
     * @return The total exp at level level.
     */
    public static int getErraticTotalExp(int level) {
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
}
