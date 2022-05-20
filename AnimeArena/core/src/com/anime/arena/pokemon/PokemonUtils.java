package com.anime.arena.pokemon;

import com.anime.arena.skill.DamageSkill;
import com.anime.arena.skill.Skill;
import com.anime.arena.skill.SkillCategory;
import com.anime.arena.skill.SkillTarget;

import java.util.List;

public class PokemonUtils {

    /** Constants */

    //Mins and Maxes
    public static final int MAX_LEVEL = 100;
    public static final int MIN_LEVEL = 1;
    public static final int MAX_IV = 31;
    public static final int MIN_IV = 1;
    public static final int MAX_EV = 255;
    public static final int MIN_EV = 0;
    public static final int TOTAL_EV = 510;
    public static final int NO_EXP = 0;
    public static final int INITIAL_STAT_STAGE = 0;

    public static final int HEALTH = 0;
    public static final int ATTACK = 1;
    public static final int SPECIAL_ATTACK = 3;
    public static final int DEFENSE = 2;
    public static final int SPECIAL_DEFENSE = 4;
    public static final int SPEED = 5;

    //Damage Multiplier Constants
    public static final double NORMAL_MULTIPLIER = 1;
    public static final double REDUCED_MULTIPLIER = 0.9;
    public static final double INCREASED_MULTIPLIER = 1.1;

    public static String getDexNumberString(int num) {
        if (num < 10) {
            return "00" + num;
        } else if (num < 100) {
            return "0" + num;
        }
        return Integer.toString(num);
    }

    public static String getCry(BasePokemon pokemon) {
        String cry = "";
        int dexNumber = pokemon.getDexNumber();
        if (dexNumber < 10) {
            cry = "00" + dexNumber + ".wav";
        } else if (dexNumber < 100) {
            cry = "0" + dexNumber + ".wav";
        } else {
            cry = dexNumber + ".wav";
        }
        return cry;
    }

    public static List<String> getAbilityInformation(BasePokemonFactory basePokemonFactory, int abilityID) {
        return basePokemonFactory.getAbilityDatabase().get(abilityID);
    }

    public static void evolvePokemon(Pokemon pokemon, BasePokemon newPokemon) {
        pokemon.setBasePokemon(newPokemon);
    }

    public static BasePokemon getEvolvedPokemon(Pokemon pokemon, int evolutionMethod, BasePokemonFactory factory) {
        List<Evolution> evolutionMethods = pokemon.getConstantVariables().getEvolutionMethods(evolutionMethod);
        for (Evolution method : evolutionMethods) {
            BasePokemon evolvedPokemon = method.getEvolution(pokemon, evolutionMethod, factory);
            if (evolvedPokemon != null) {
                return evolvedPokemon;
            }
        }
        return null;
    }

    public static void setCurrentHealthAfterRevive(Pokemon pokemon) {
        pokemon.setCurrentHealth(pokemon.getHealthStat() / 2);
    }

    public static void healPokemon(Pokemon pokemon, int amount) {
        if (amount >= 0) {
            int newHealth = pokemon.getCurrentHealth() + amount;
            int maxHealth = pokemon.getHealthStat();
            if (newHealth > maxHealth) {
                pokemon.setCurrentHealth(maxHealth);
            } else {
                pokemon.setCurrentHealth(pokemon.getCurrentHealth() + amount);
            }
        }
    }

    public static void healParty(List<Pokemon> party) {
        for (Pokemon p : party) {
            p.setCurrentHealth(p.getHealthStat());
            recoverStatus(p);
        }
    }

    public static void maxHealPokemon(Pokemon pokemon) {
        pokemon.setCurrentHealth(pokemon.getHealthStat());
    }

    public static boolean isFullHealth(Pokemon pokemon) {
        if (pokemon.getCurrentHealth() == pokemon.getHealthStat()) {
            return true;
        }
        return false;
    }

    public static boolean isStatusFree(Pokemon pokemon) {
        if (pokemon.getUniqueVariables().getStatus() == StatusCondition.STATUS_FREE) {
            return true;
        }
        return false;
    }

    public static void recoverStatus(Pokemon pokemon) {
        pokemon.getUniqueVariables().setStatus(StatusCondition.STATUS_FREE);
    }

    public static char createGender(double genderProbability) {
        double rand = Math.random();
        if (genderProbability < 0) {
            return 'U'; //No Gender
        } else {
            if (rand <= genderProbability) {
                return 'M'; //Male
            } else {
                return 'F'; //Female
            }
        }
    }

    public static void initBlankPokemonData(Pokemon p, int level) {
        p.getUniqueVariables().initBlankData(level);
        initRandomAbility(p);
        p.getUniqueVariables().setGender(createGender(0.5));
        //TODO: Init Moves
        p.setCurrentHealth(p.getHealthStat());
    }

    public static Pokemon createPokemon(int dexNum, int level, BasePokemonFactory factory) {
        BasePokemon pansage = factory.createBasePokemon(dexNum);
        UniquePokemon unique = new UniquePokemon();
        Pokemon p = new Pokemon(pansage, unique);
        PokemonUtils.initBlankPokemonData(p, level);
        return p;
    }


    /**
     * Return a random IV value, 1-31
     * @return A random IV value
     */
    public static int createRandomIV() {
        return (int)(Math.round(Math.random() * 30)) + 1;
    }

    private static void initRandomAbility(Pokemon pokemon) {
        if (pokemon.getConstantVariables().getSecondAbility() != 230) {
            double rand = Math.random();
            if (rand < .5) {
                pokemon.getUniqueVariables().setAbilityPosition(0);
                pokemon.getUniqueVariables().setAbility(pokemon.getConstantVariables().getFirstAbility());
            } else {
                pokemon.getUniqueVariables().setAbilityPosition(1);
                pokemon.getUniqueVariables().setAbility(pokemon.getConstantVariables().getSecondAbility());
            }
        } else {
            pokemon.getUniqueVariables().setAbilityPosition(0);
            pokemon.getUniqueVariables().setAbility(pokemon.getConstantVariables().getFirstAbility());
        }
    }


    /**
     * Set a random nature for the Pokemon.
     */
    public static Nature getRandomNature() {
        Nature[] natureList = Nature.values();
        int value = (int)Math.round(Math.random() * (natureList.length - 1));
        return natureList[value];
    }


    public static Skill createScratchMove() {
        String name = "Scratch";
        String description = "Hard, pointed, sharp claws rake the target to inflict damage.";
        int basePower = 40;
        int pp = 35;
        SkillCategory cat = SkillCategory.PHYSICAL;
        PokemonType type = PokemonType.NORMAL;
        int accuracy = 100;
        SkillTarget target = SkillTarget.ENEMY;
        DamageSkill damageSkill = new DamageSkill(name, description, cat, pp, pp,accuracy, type, target, 0, 1, basePower, 1);
        damageSkill.setMakesPhysicalContact(true);
        return damageSkill;
    }
}
