package com.anime.arena.pokemon;

public class Evolution {
    private int level;
    private int evolutionPokemon;
    //If the Pokemon has a chance to evolved into one of two things, it will have a second evolution. Second evolution is the pokemon id for the second evolution.
    //The only Pokemon that evolves this way is Wurmple. Either into a Cascoon or a Silcoon
    private int secondEvolution;
    private int evolutionMethod;

    private boolean attackLessDefense;
    private boolean defenseLessAttack;
    private boolean equalAttackDefense;
    private String gender;
    //If the Pokemon evolves into something based on it's nature types (Attack Nature or Sp Attack Nature)
    //The only Pokemon that evolves this way is Toxel.
    private int natureTypes;

    //TODO: Add other evolution criteria here - Skill a pokemon needs when leveling up, stat being higher than another like tyrogue etc

    public Evolution(int level, int evolutionPokemon, int evolutionMethod, boolean attackLessDefense, boolean defenseLessAttack,
                     boolean equalAttackDefense, int secondEvolution, String gender, int natureTypes) {
        this.level = level;
        this.evolutionMethod = evolutionMethod;
        this.evolutionPokemon = evolutionPokemon;
        this.attackLessDefense = attackLessDefense;
        this.defenseLessAttack = defenseLessAttack;
        this.equalAttackDefense = equalAttackDefense;
        this.secondEvolution = secondEvolution;
        this.gender = gender;
        this.natureTypes = natureTypes;
    }

    private boolean matchesGender(Pokemon p) {
        if (gender != null && gender.length() == 1) {
            return p.getUniqueVariables().getGender() == gender.charAt(0);
        }
        return true;
    }

    public int getLevel() {
        return level;
    }

    public int getEvolutionPokemon() {
        return evolutionPokemon;
    }



    public BasePokemon getEvolution(Pokemon basePokemon, int evolutionMethod, BasePokemonFactory factory) {
        if (this.evolutionMethod == evolutionMethod && matchesGender(basePokemon) && basePokemon.getUniqueVariables().getLevel() >= level) {
            if (evolvesWhenAttackGreaterThanDefense(basePokemon) || evolvesWhenAttackLessThanDefense(basePokemon)
                    || evolvesWhenAttackEqualsDefense(basePokemon) || noStatRequirement()) {
                if (natureTypes == 0 || (natureTypes == 1 && hasAttackNature(basePokemon.getUniqueVariables())) ||
                        (natureTypes == 2 && hasSpAttackNature(basePokemon.getUniqueVariables()))) {
                    if (secondEvolution != 0) {
                        if (Math.random() < 0.5) {
                            return factory.createBasePokemon(secondEvolution);
                        }
                    }
                }
                return factory.createBasePokemon(evolutionPokemon);
            }
        }
        return null;
    }

    private boolean hasAttackNature(UniquePokemon p) {
        return true;
    }
    private boolean hasSpAttackNature(UniquePokemon p) {
        return false;
    }

    private boolean evolvesWhenAttackLessThanDefense(Pokemon basePokemon) {
       return attackLessDefense && basePokemon.getAttackStat() < basePokemon.getDefenseStat();
    }

    private boolean evolvesWhenAttackGreaterThanDefense(Pokemon basePokemon) {
        return defenseLessAttack && basePokemon.getAttackStat() > basePokemon.getDefenseStat();
    }

    private boolean evolvesWhenAttackEqualsDefense(Pokemon basePokemon) {
        return equalAttackDefense && basePokemon.getAttackStat() == basePokemon.getDefenseStat();
    }

    private boolean noStatRequirement() {
        if (!attackLessDefense && !defenseLessAttack && !equalAttackDefense) {
            return true;
        }
        return false;
    }

    public int getEvolutionMethod() {
        return evolutionMethod;
    }
}
