package com.anime.arena.pokemon;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.skill.*;
import com.anime.arena.skill.effect.*;

import java.util.List;
import java.util.Map;

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

    public static boolean isSendOutAbility(AbilityId ability) {
        AbilityId[] sendOutAbilities = new AbilityId[] {
                AbilityId.INTIMIDATE,
                AbilityId.TRACE,
                AbilityId.DRIZZLE,
                AbilityId.SAND_STREAM,
                AbilityId.PRESSURE,
                AbilityId.DROUGHT,
                AbilityId.FOREWARN,
                AbilityId.SNOW_WARNING,
                AbilityId.FRISK,
                AbilityId.ELECTRIC_SURGE,
                AbilityId.PSYCHIC_SURGE,
                AbilityId.GRASSY_SURGE,
                AbilityId.MISTY_SURGE
        };
        for (AbilityId id : sendOutAbilities) {
            if (ability == id) {
                return true;
            }
        }
        return false;
    }

    public void useSendOutAbility(AbilityId ability, BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField) {
        if (ability == AbilityId.INTIMIDATE) {
            Effect intimidateEffect = new AttackEffect(SkillTarget.ENEMY, StatDirection.DECREASE, 1);
            intimidateEffect.use(skillUser, enemyPokemon, field, userField, enemyField, false);
        }
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

    public static void initBlankPokemonData(Pokemon p, int level, BasePokemonFactory factory) {
        p.getUniqueVariables().initBlankData(level);
        initRandomAbility(p);
        p.getUniqueVariables().setGender(createGender(0.5));
        //add moves
        List<Integer> defaultMoves = p.getConstantVariables().getDefaultMoves();
        int numberOfDefaultMoves = defaultMoves.size();
        Map<Integer, List<Integer>> levelUpSkills =p.getConstantVariables().getLevelUpMoves();
        if (defaultMoves.size() > 0) {
            int currentSkill = 0;
            List<Skill> moves = p.getUniqueVariables().getMoves();
            for (int i = 0; i <= p.getUniqueVariables().getLevel(); i++) {
                //Check if the Pokemon learns a move at the level i
                if (levelUpSkills.containsKey(i)) {
                    for (int j = 0; j < levelUpSkills.get(i).size(); j++) {
                        if (!p.getUniqueVariables().hasMove(levelUpSkills.get(i).get(j))) {
                            if (moves.size() > currentSkill) {
                                moves.set(currentSkill, factory.getMove(levelUpSkills.get(i).get(j)));
                            } else {
                                moves.add(factory.getMove(levelUpSkills.get(i).get(j)));
                            }
                            currentSkill++;
                        }
                        if (currentSkill == 4) {
                            //Start adding moves from the oldest to the newest since the
                            //move list is full.
                            currentSkill = 0;
                        }
                    }
                }
            }
//            for (int i = 0; i < Math.min(numberOfDefaultMoves, 4); i++) {
//                p.getUniqueVariables().getMoves().add(factory.getMove(defaultMoves.get(i)));
//            }
        } else {
            //Create default hail when there are no moves
            p.getUniqueVariables().getMoves().add(PokemonUtils.createHail());
        }
        p.setCurrentHealth(p.getHealthStat());
    }

    public static Pokemon createPokemon(int dexNum, int level, BasePokemonFactory factory) {
        BasePokemon pansage = factory.createBasePokemon(dexNum);
        UniquePokemon unique = new UniquePokemon();
        Pokemon p = new Pokemon(pansage, unique);
        PokemonUtils.initBlankPokemonData(p, level, factory);
        return p;
    }

    public static Pokemon createTestPokemon(String filename, BasePokemonFactory factory) {
        return createTestPokemon(filename, factory, 5);
    }

    public static Pokemon createTestPokemon(String filename, BasePokemonFactory factory, int level) {
        BasePokemon pansage = factory.createBasePokemonFromTestFile(filename);
        UniquePokemon unique = new UniquePokemon();
        Pokemon p = new Pokemon(pansage, unique);
        PokemonUtils.initBlankPokemonData(p, level, factory);
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

    public static void setFirstAbility(Pokemon pokemon) {
        pokemon.getUniqueVariables().setAbilityPosition(0);
        pokemon.getUniqueVariables().setAbility(pokemon.getConstantVariables().getFirstAbility());
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
        DamageSkill damageSkill = new DamageSkill(0, name, description, cat, pp, pp,accuracy, type, target, 0, 0, basePower, 1);
        damageSkill.setMakesPhysicalContact(true);
        return damageSkill;
    }

    public static Skill createGustMove() {
        String name = "Gust";
        String description = "A gust of wind is whipped up by wings and launched at the target to inflict damage.";
        int basePower = 40;
        int pp = 35;
        SkillCategory cat = SkillCategory.SPECIAL;
        PokemonType type = PokemonType.FLYING;
        int accuracy = 100;
        SkillTarget target = SkillTarget.ENEMY;
        DamageSkill damageSkill = new DamageSkill(0, name, description, cat, pp, pp,accuracy, type, target, 0, 0, basePower, 1);
        return damageSkill;
    }

    public static Skill createBulkUpMove() {
        String name = "Bulk Up";
        String description = "The user tenses its muscles to bulk up its body, raising both its Attack and Defense stats.";
        int pp = 20;
        SkillCategory cat = SkillCategory.MISC;
        PokemonType type = PokemonType.FIGHTING;
        int accuracy = -1;
        SkillTarget target = SkillTarget.SELF;
        EffectSkill effectSkill = new EffectSkill(0, name, description, cat, pp, pp,accuracy, type, target, 0, 0);
        effectSkill.addEffect(new AttackEffect(SkillTarget.SELF, StatDirection.INCREASE, 1));
        effectSkill.addEffect(new DefenseEffect(SkillTarget.SELF, StatDirection.INCREASE, 1));
        return effectSkill;
    }


    public static Skill createCalmMindMove() {
        String name = "Calm Mind";
        String description = "The user quietly focuses its mind and calms its spirit to raise its Sp. Atk and Sp. Def stats.";
        int pp = 20;
        SkillCategory cat = SkillCategory.MISC;
        PokemonType type = PokemonType.PSYCHIC;
        int accuracy = -1;
        SkillTarget target = SkillTarget.SELF;
        EffectSkill effectSkill = new EffectSkill(0, name, description, cat, pp, pp,accuracy, type, target, 0, 0);
        effectSkill.addEffect(new SpecialAttackEffect(SkillTarget.SELF, StatDirection.INCREASE, 1));
        effectSkill.addEffect(new SpecialDefenseEffect(SkillTarget.SELF, StatDirection.INCREASE, 1));
        return effectSkill;
    }

    public static Skill createAgilityMove() {
        String name = "Agility";
        String description = "The user relaxes and lightens its body to move faster. This sharply raises the Speed stat.";
        int pp = 30;
        SkillCategory cat = SkillCategory.MISC;
        PokemonType type = PokemonType.PSYCHIC;
        int accuracy = -1;
        SkillTarget target = SkillTarget.SELF;
        EffectSkill effectSkill = new EffectSkill(0, name, description, cat, pp, pp,accuracy, type, target, 0, 0);
        effectSkill.addEffect(new SpeedEffect(SkillTarget.SELF, StatDirection.INCREASE, 2));
        return effectSkill;
    }

    public static Skill createWillOWispMove() {
        String name = "Will-O-Wisp";
        String description = "The user relaxes and lightens its body to move faster. This sharply raises the Speed stat.";
        int pp = 15;
        SkillCategory cat = SkillCategory.MISC;
        PokemonType type = PokemonType.FIRE;
        int accuracy = 100;
        SkillTarget target = SkillTarget.ENEMY;
        EffectSkill effectSkill = new EffectSkill(0, name, description, cat, pp, pp,accuracy, type, target, 0, 0);
        effectSkill.addEffect(new BurnEffect(target));
        return effectSkill;
    }

    public static Skill createPoisonpowder() {
        String name = "Poison Powder";
        String description = "The user scatters a cloud of poisonous dust that poisons the target.";
        int pp = 35;
        SkillCategory cat = SkillCategory.MISC;
        PokemonType type = PokemonType.POISON;
        int accuracy = 100;
        SkillTarget target = SkillTarget.ENEMY;
        EffectSkill effectSkill = new EffectSkill(0, name, description, cat, pp, pp, accuracy, type, target, 0, 0);
        effectSkill.addEffect(new PoisonEffect(target));
        return effectSkill;
    }

    public static Skill createThunderWave() {
        String name = "Thunder Wave";
        String description = "The user launches a weak jolt of electricity that paralyzes the target.";
        int pp = 20;
        SkillCategory cat = SkillCategory.MISC;
        PokemonType type = PokemonType.ELECTRIC;
        int accuracy = 100;
        SkillTarget target = SkillTarget.ENEMY;
        EffectSkill effectSkill = new EffectSkill(0, name, description, cat, pp, pp, accuracy, type, target, 0, 0);
        effectSkill.addEffect(new ParalysisEffect(target));
        return effectSkill;
    }

    public static Skill createDarkVoid() {
        String name = "Dark Void";
        String description = "Opposing Pokémon are dragged into a world of total darkness that makes them sleep.";
        int pp = 10;
        SkillCategory cat = SkillCategory.MISC;
        PokemonType type = PokemonType.DARK;
        int accuracy = 100;
        SkillTarget target = SkillTarget.ENEMY;
        EffectSkill effectSkill = new EffectSkill(0, name, description, cat, pp, pp, accuracy, type, target, 0, 0);
        effectSkill.addEffect(new SleepEffect(target));
        return effectSkill;
    }

    public static Skill createRainDance() {
        String name = "Rain Dance";
        String description = "The user summons a heavy rain that falls for five turns, powering up Water-type moves. It lowers the power of Fire-type moves.";
        int pp = 5;
        SkillCategory cat = SkillCategory.MISC;
        PokemonType type = PokemonType.WATER;
        int accuracy = -1;
        SkillTarget target = SkillTarget.FIELD;
        EffectSkill effectSkill = new EffectSkill(0, name, description, cat, pp, pp, accuracy, type, target, 0, 0);
        effectSkill.addEffect(new RainEffect());
        return effectSkill;
    }

    public static Skill createSunnyDay() {
        String name = "Sunny Day";
        String description = "The user intensifies the sun for five turns, powering up Fire-type moves. It lowers the power of Water-type moves.";
        int pp = 5;
        SkillCategory cat = SkillCategory.MISC;
        PokemonType type = PokemonType.FIRE;
        int accuracy = -1;
        SkillTarget target = SkillTarget.FIELD;
        EffectSkill effectSkill = new EffectSkill(0, name, description, cat, pp, pp, accuracy, type, target, 0, 0);
        effectSkill.addEffect(new SunEffect());
        return effectSkill;
    }

    public static Skill createSandstorm() {
        String name = "Sandstorm";
        String description = "A five-turn sandstorm is summoned to hurt all combatants except Rock, Ground, and Steel types. It raises the Sp. Def stat of Rock types.";
        int pp = 5;
        SkillCategory cat = SkillCategory.MISC;
        PokemonType type = PokemonType.GROUND;
        int accuracy = -1;
        SkillTarget target = SkillTarget.FIELD;
        EffectSkill effectSkill = new EffectSkill(0, name, description, cat, pp, pp, accuracy, type, target, 0, 0);
        effectSkill.addEffect(new SandstormEffect());
        return effectSkill;
    }

    public static Skill createHail() {
        String name = "Hail";
        String description = "The user summons a hailstorm lasting five turns. It damages all Pokémon except Ice types.";
        int pp = 5;
        SkillCategory cat = SkillCategory.MISC;
        PokemonType type = PokemonType.ICE;
        int accuracy = -1;
        SkillTarget target = SkillTarget.FIELD;
        EffectSkill effectSkill = new EffectSkill(0, name, description, cat, pp, pp, accuracy, type, target, 0, 0);
        effectSkill.addEffect(new HailEffect());
        return effectSkill;
    }

    public static Skill createGrowlMove() {
        String name = "Growl";
        String description = "The user growls in an endearing way, making opposing Pokémon less wary. This lowers their Attack stats.";
        int pp = 35;
        SkillCategory cat = SkillCategory.MISC;
        PokemonType type = PokemonType.NORMAL;
        int accuracy = -1;
        SkillTarget target = SkillTarget.ENEMY;
        EffectSkill effectSkill = new EffectSkill(0, name, description, cat, pp, pp,accuracy, type, target, 0, 0);
        effectSkill.addEffect(new AttackEffect(SkillTarget.ENEMY, StatDirection.DECREASE, 1));
        return effectSkill;
    }

    public static Skill createSandAttackMove() {
        String name = "Sand Attack";
        String description = "Sand is hurled in the foe's face, reducing its accuracy.";
        int pp = 15;
        SkillCategory cat = SkillCategory.MISC;
        PokemonType type = PokemonType.GROUND;
        int accuracy = -1;
        SkillTarget target = SkillTarget.ENEMY;
        EffectSkill effectSkill = new EffectSkill(0, name, description, cat, pp, pp,accuracy, type, target, 0, 0);
        effectSkill.addEffect(new AccuracyEffect(SkillTarget.ENEMY, StatDirection.DECREASE, 1));
        return effectSkill;
    }

    public static Skill createLeerMove() {
        String name = "Leer";
        String description = "The user gives opposing Pokémon an intimidating leer that lowers the Defense stat.";
        int pp = 30;
        SkillCategory cat = SkillCategory.MISC;
        PokemonType type = PokemonType.NORMAL;
        int accuracy = -1;
        SkillTarget target = SkillTarget.ENEMY;
        EffectSkill effectSkill = new EffectSkill(0, name, description, cat, pp, pp,accuracy, type, target, 0, 0);
        effectSkill.addEffect(new DefenseEffect(SkillTarget.ENEMY, StatDirection.DECREASE, 1));
        return effectSkill;
    }

    public static Skill createTackleMove() {
        String name = "Tackle";
        String description = "A physical attack in which the user charges and slams into the target with its whole body.";
        SkillCategory category = SkillCategory.PHYSICAL;
        int tacklePP = 35;
        int accuracy = 100;
        PokemonType moveType = PokemonType.NORMAL;
        SkillTarget target = SkillTarget.ENEMY;
        int subtype = 0;
        int speedPriority = 0;
        int basePower = 40;
        int criticalRate = 1;
        return new DamageSkill(0, name, description, category, tacklePP, tacklePP, accuracy, moveType, target, subtype,
                speedPriority, basePower, criticalRate);
    }
}
