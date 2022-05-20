package com.anime.arena.pokemon;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.anime.arena.pokemon.PokemonUtils.*;

public class BasePokemon {
    private String name;
    private int pid;
    private int dexNumber;
    private String classification;
    private String description;
    private double captureRate;
    private double height;
    private double weight;
    private int baseExp;

    private int firstAbility;
    private int secondAbility;
    private int hiddenAbility;

    private PokemonType firstType;
    private PokemonType secondType;

    private ExpType expType;

    private String image;

    private int[] baseStats;
    private int[] evYield;

    private List<Evolution> evolutionList;


    public BasePokemon() {

    }



    public void setEvolutionList(List<Evolution> evolutionList) {
        this.evolutionList = evolutionList;
    }

    public void logEvolutionMethods() {
        if (evolutionList.size() == 0) {
            Gdx.app.log("Evolutions for " + name, "None");
        } else {
            for (Evolution e : evolutionList) {
                Gdx.app.log("Evolution for " + name, "Level: " + e.getLevel() + ", Method: " + e.getEvolutionMethod() + ", Pokemon ID: " + e.getEvolutionPokemon());
            }
        }
    }

    public List<Evolution> getEvolutionMethods(int evolutionMethod) {
        List<Evolution> filteredEvolutionList = new ArrayList<Evolution>();
        for (Evolution m : evolutionList) {
            if (m.getEvolutionMethod() == evolutionMethod) {
                filteredEvolutionList.add(m);
            }
        }
        return filteredEvolutionList;
    }

    public int getPID() { return pid; }

    public void setPID(int pid) { this.pid = pid; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDexNumber() {
        return dexNumber;
    }

    public void setDexNumber(int dexNumber) {
        this.dexNumber = dexNumber;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCaptureRate() {
        return captureRate;
    }

    public void setCaptureRate(double captureRate) {
        this.captureRate = captureRate;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getBaseExp() {
        return baseExp;
    }

    public void setBaseExp(int baseExp) {
        this.baseExp = baseExp;
    }

    public int getFirstAbility() {
        return firstAbility;
    }

    public void setFirstAbility(int firstAbility) {
        this.firstAbility = firstAbility;
    }

    public int getSecondAbility() {
        return secondAbility;
    }

    public void setSecondAbility(int secondAbility) {
        this.secondAbility = secondAbility;
    }

    public int getHiddenAbility() {
        return hiddenAbility;
    }

    public void setHiddenAbility(int hiddenAbility) {
        this.hiddenAbility = hiddenAbility;
    }

    public PokemonType getFirstType() {
        return firstType;
    }

    public void setFirstType(int firstType) {
        this.firstType = PokemonType.fromInt(firstType);
    }

    public PokemonType getSecondType() {
        return secondType;
    }

    public void setSecondType(int secondType) {
        this.secondType = PokemonType.fromInt(secondType);
    }

    public ExpType getExpType() {
        return expType;
    }

    public void setExpType(ExpType expType) {
        this.expType = expType;
    }

    public String getImage() {
        return image;
    }

    public String getFormattedImage() {
        return image.substring(0, image.length() - 4);
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int[] getBaseStats() {
        return baseStats;
    }

    public void setBaseStats(int[] baseStats) {
        this.baseStats = baseStats;
    }

    public int[] getEvYield() {
        return evYield;
    }

    public void setEvYield(int[] evYield) {
        this.evYield = evYield;
    }

    /**
     * Return the Pokemon's base heatlh stat.
     * @return Base Health
     */
    public int getBaseStatHealth() {
        return baseStats[HEALTH];
    }

    /**
     * Return the Pokemon's base attack stat.
     * @return Base Attack
     */
    public int getBaseStatAttack() {
        return baseStats[ATTACK];
    }

    /**
     * Return the Pokemon's base special attack
     * @return Base Special Attack
     */
    public int getBaseStatSpeicialAttack() {
        return baseStats[SPECIAL_ATTACK];
    }

    /**
     * Return the Pokemon's base defense.
     * @return Base Defense
     */
    public int getBaseStatDefense() {
        return baseStats[DEFENSE];
    }

    /**
     * Return the Pokemon's base special defense.
     * @return Base Special Defense
     */
    public int getBaseStatSpecialDefense() {
        return baseStats[SPECIAL_DEFENSE];
    }

    /**
     * Return the Pokemon's base speed.
     * @return Base Speed
     */
    public int getBaseStatSpeed() {
        return baseStats[SPEED];
    }
}
