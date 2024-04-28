package com.anime.arena.pokemon;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.field.WeatherType;
import com.anime.arena.skill.Skill;
import com.anime.arena.skill.SkillCategory;
import com.anime.arena.tools.ScriptParameters;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.HashMap;

import static com.anime.arena.pokemon.PokemonUtils.INITIAL_STAT_STAGE;

public class BattlePokemon {

    private Pokemon pokemon;

    protected HashMap<PokemonType, Double> resistances;
    protected PokemonType battleTypeOne;
    protected PokemonType battleTypeTwo;
    protected AbilityId ability;

    //Animation Variables
    private double animationHealth;
    private double displayedExp;
    private Sprite sprite;
    private boolean visible;
    private boolean justLeveledUp;

    //Stage Variables
    protected int attackStage;
    protected int defenseStage;
    protected int specialDefenseStage;
    protected int specialAttackStage;
    protected int speedStage;
    protected int accuracyStage;
    protected int evasionStage;

    protected StatusCondition preStatus;
    protected boolean flinched;
    protected boolean focused;
    protected boolean wrapped;
    protected int wrapTurns;
    protected boolean fainted;

    //Battle Variables
    private boolean isTaunted;
    private int tauntTime;

    private boolean isEffectedByEncore;
    private int encoreTime;
    private int encoreSlot;
    private final int TOTAL_ENCORE_TIME = 3;

    private int disabledSlot;
    private int disabledTime;
    private final int TOTAL_DISABLE_TIME = 4;

    private boolean isMagnetRisen;
    private int magnetRisenTime;
    private final int TOTAL_MAGNET_RISE_TIME = 5;

    private boolean isLiftedByTelekinesis;
    private int telekinesisTime;
    private final int TOTAL_TELEKINESIS_TIME = 3;

    private boolean isHealBlocked;
    private int healBlockTime;
    private final int TOTAL_HEAL_BLOCK_TIME = 5;

    private boolean isEffectedByEmbargo;
    private int embargoTime;
    private final int TOTAL_EMBARGO_TIME = 5;

    private boolean justReceivedYawn;
    private boolean fallAsleepDueToYawnThisTurn;

    private boolean heardPerishSong;
    private int perishSongTime;
    private final int TOTAL_PERISH_SONG_TIME = 4;

    private boolean witnessedFutureSight;
    private int futureSightTime;
    private Pokemon futureSightUser;
    private final int TOTAL_FUTURE_SIGHT_TIME = 2;

    private boolean witnessedDoomDesire;
    private int doomDesireTime;
    private final int TOTAL_DOOM_DESIRE_TIME = 2;

    //The amount of damage the Pokemon took this turn. This is used for counter, mirror coat and metal burst.
    private int turnDamageTaken;
    private SkillCategory damageTakenCategory;
    private boolean tookDamageThisTurn; //For Assurance


    private boolean envelopedInAquaRing;
    private boolean isIngrained;
    private boolean isCursed;

    private boolean binded;
    private int bindedTurns;
    private boolean clamped;
    private int clampTurns;
    private boolean inWhirlpool;
    private int whirlpoolTurns;
    private boolean fireSpin;
    private int fireSpinTurns;
    private boolean magmaStorm;
    private int magmaStormTurns;
    private boolean infested;
    private int infestationTurns;
    private boolean sandTomb;
    private int sandTombTurns;

    private boolean isLeechSeeded;
    private boolean hasNightmares;
    private boolean receivingWish;
    private int wishTurns;
    private String wishUser;
    private final int TOTAL_WISH_TURNS = 1;

    private int confusionTime;
    private final int NOT_CONFUSED = 0;
    private boolean confused;

    private int sleepTime;

    private int rolloutTurns = 0;

    private Skill outrageSkill;
    private int outrageTurns;

    private int furyCutterStacks;
    private int echoedVoiceStacks;
    private int chargeTurns; //For move Charge

    private boolean powdered;

    private Skill nextTurnSkill;
    private boolean flying; //Fly
    private boolean underground; //Dig
    private boolean underwater; //Dive
    private boolean recharging;
    private boolean skyDrop;
    private boolean spiderWebbed;
    private boolean firstTurn;
    private int laserFocusTurns;
    private int lockOnTurns;
    private boolean usedRage;
    private int stockpileStacks;
    private boolean uproaring;

    private StanceForme stanceForme;
    
    public BattlePokemon(Pokemon p, TextureAtlas atlas, boolean isPlayerPokemon) {
       this.pokemon = p;
       this.ability = AbilityId.fromInt(p.getUniqueVariables().getAbility());
       if (ability == AbilityId.STANCE_CHANGE) {
           stanceForme = StanceForme.SHIELD;
       } else {
           stanceForme = StanceForme.NONE;
       }

       this.animationHealth = p.getCurrentHealth();
        sprite = new Sprite(atlas.findRegion(p.getConstantVariables().getFormattedImage()));
        if (isPlayerPokemon) {
            sprite.setSize(396, 396);
            sprite.setPosition(122, 1231);
        } else {
            sprite.setSize(396, 396);
            sprite.setPosition(122,1231);
        }
        this.visible = true;
        this.justLeveledUp = false;
        this.displayedExp = p.getUniqueVariables().getCurrentExp();
        init();
    }

    private void initBattleTypes() {
        this.battleTypeOne = pokemon.getConstantVariables().getFirstType();
        this.battleTypeTwo = pokemon.getConstantVariables().getSecondType();
    }

    private void init() {
        initBattleTypes();
        initializeResistances();
    }

    public void draw(Batch batch) {
        if (visible) {
            sprite.draw(batch);
        }
    }

    public void setVisibility(boolean visible) {
        this.visible = visible;
    }

    public void levelUp() {
        if (!pokemon.isMaxLevel()) {
            pokemon.getUniqueVariables().levelUp();
            justLeveledUp = true;
        }
    }

    public boolean isMaxHealth() {
        return getCurrentHealth() == pokemon.getHealthStat();
    }

    public StanceForme getStanceForme() {
        return stanceForme;
    }

    /**
     * Return whether or not the animation bar's displayed health
     * matches the actual current health of the Pokemon.
     * @return Whether or not the animation health matches the
     * current health.
     */
    public boolean matchingAnimationHealth() {
        return animationHealth == pokemon.getCurrentHealth() ? true : false;
    }

    /**
     * Return the animation health bar's health value.
     * @return The animation health bar's health value.
     */
    public double getAnimationHealth() {
        return animationHealth;
    }

    /**
     * Return the sprite for this BattlePokemon
     * @return
     */
    public Sprite getSprite() { return sprite; }

    /**
     * Subtract an amount of animation health
     * @param amt The amount of animation health to subtract.
     */
    public void subtractAnimationHealth(double amt) {
        animationHealth -= amt;
        animationHealth = Math.max(pokemon.getCurrentHealth(), animationHealth);
    }

    /**
     * Add an amount of animation health.
     * @param amt The amount of animation health to add.
     */
    public void addAnimationHealth(double amt) {
        animationHealth += amt;
        animationHealth = Math.min(pokemon.getCurrentHealth(), animationHealth);
    }

    /**
     * Add or subtract the animation health in order to make it
     * equivalent to the current health.
     * @param rate The amount to add or subtract from the animation health
     */
    public void adjustAnimationHealth(int rate) {
        if (animationHealth > pokemon.getCurrentHealth()) {
            subtractAnimationHealth(rate);
        } else if (animationHealth < pokemon.getCurrentHealth()) {
            addAnimationHealth(rate);
        }
    }

    public int getCurrentHealth() {
        return pokemon.getCurrentHealth();
    }

    public void subtractHealth(int amount) {
        pokemon.setCurrentHealth(Math.max(0, pokemon.getCurrentHealth() - amount));
    }

    public boolean hasFainted() {
        return pokemon.isFainted();
    }

    public void addHealth(int amount) {
        PokemonUtils.healPokemon(pokemon, amount);
    }

    public boolean hasFullHealth() {
        return pokemon.getCurrentHealth() == pokemon.getHealthStat();
    }

    public String getName() {
        return pokemon.getConstantVariables().getName();
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public AbilityId getAbility() { return ability; }

    public PokemonType getFirstType() { return battleTypeOne; }

    public PokemonType getSecondType() { return battleTypeTwo; }

    public void resetMoves() {
        pokemon.getUniqueVariables().getMoves().clear();
    }

    public Skill getFirstMove() {
        return pokemon.getUniqueVariables().getMoves().size() > 0 ? pokemon.getUniqueVariables().getMoves().get(0) : null;
    }

    public Skill getSecondMove() {
        return pokemon.getUniqueVariables().getMoves().size() > 1 ? pokemon.getUniqueVariables().getMoves().get(1) : null;
    }

    public Skill getThirdMove() {
        return pokemon.getUniqueVariables().getMoves().size() > 2 ? pokemon.getUniqueVariables().getMoves().get(2) : null;
    }

    public Skill getFourthMove() {
        return pokemon.getUniqueVariables().getMoves().size() > 3 ? pokemon.getUniqueVariables().getMoves().get(3) : null;
    }

    /**
     * Set all of the battle stages to default.
     * - Attack, Sp. Atk, ... Speed.
     */
    public void resetStages() {
        attackStage = INITIAL_STAT_STAGE;
        specialAttackStage = INITIAL_STAT_STAGE;
        defenseStage = INITIAL_STAT_STAGE;
        specialDefenseStage = INITIAL_STAT_STAGE;
        speedStage = INITIAL_STAT_STAGE;
        accuracyStage = INITIAL_STAT_STAGE;
        evasionStage = INITIAL_STAT_STAGE;
    }

    /**
     * Decrease the attack stage by amount stages.
     * The stage can't go below -6
     * @param amount The amount of stages to decrease
     */
    public void decreaseAttackStage(int amount) {
        attackStage -= amount;
        attackStage = Math.max(attackStage, -6);
    }

    /**
     * Decrease the defense stage by amount stages.
     * The stage can't go below -6
     * @param amount The amount of stages to decrease
     */
    public void decreaseDefenseStage(int amount) {
        defenseStage -= amount;
        defenseStage = Math.max(defenseStage, -6);
    }

    /**
     * Decrease the special attack stage by amount stages.
     * The stage can't go below -6
     * @param amount The amount of stages to decrease
     */
    public void decreaseSpAttackStage(int amount) {
        specialAttackStage -= amount;
        specialAttackStage = Math.max(specialAttackStage, -6);
    }

    /**
     * Decrease the special defense stage by amount stages.
     * The stage can't go below -6
     * @param amount The amount of stages to decrease
     */
    public void decreaseSpDefenseStage(int amount) {
        specialDefenseStage -= amount;
        specialDefenseStage = Math.max(specialDefenseStage, -6);
    }

    /**
     * Decrease the speed stage by amount stages.
     * The stage can't go below -6
     * @param amount The amount of stages to decrease
     */
    public void decreaseSpeedStage(int amount) {
        speedStage -= amount;
        speedStage = Math.max(speedStage, -6);
    }

    /**
     * Decrease the accuracy stage by amount stages.
     * The stage can't go below -6
     * @param amount The amount of stages to decrease
     */
    public void decreaseAccuracyStage(int amount) {
        accuracyStage -= amount;
        accuracyStage = Math.max(accuracyStage, -6);
    }

    /**
     * Decrease the evasion stage by amount stages.
     * The stage can't go below -6
     * @param amount The amount of stages to decrease
     */
    public void decreaseEvasionStage(int amount) {
        evasionStage -= amount;
        evasionStage = Math.max(evasionStage, -6);
    }

    /**
     * Increase the attack stage by amount stages.
     * The stage can't go above 6
     * @param amount The amount of stages to increase.
     */
    public void increaseAttackStage(int amount) {
        attackStage += amount;
        attackStage = Math.min(attackStage, 6);
    }

    /**
     * Increase the defense stage by amount stages.
     * The stage can't go above 6
     * @param amount The amount of stages to increase.
     */
    public void increaseDefenseStage(int amount) {
        defenseStage += amount;
        defenseStage = Math.min(defenseStage, 6);
    }

    /**
     * Increase the special attack stage by amount stages.
     * The stage can't go above 6
     * @param amount The amount of stages to increase.
     */
    public void increaseSpAttackStage(int amount) {
        specialAttackStage += amount;
        specialAttackStage = Math.min(specialAttackStage, 6);
    }

    /**
     * Increase the special defense stage by amount stages.
     * The stage can't go above 6
     * @param amount The amount of stages to increase.
     */
    public void increaseSpDefenseStage(int amount) {
        specialDefenseStage += amount;
        specialDefenseStage = Math.min(specialDefenseStage, 6);
    }

    /**
     * Increase the speed stage by amount stages.
     * The stage can't go above 6
     * @param amount The amount of stages to increase.
     */
    public void increaseSpeedStage(int amount) {
        speedStage += amount;
        speedStage = Math.min(speedStage, 6);
    }

    /**
     * Increase the accuracy stage by amount stages.
     * The stage can't go above 6
     * @param amount The amount of stages to increase.
     */
    public void increaseAccuracyStage(int amount) {
        accuracyStage += amount;
        accuracyStage = Math.min(accuracyStage, 6);
    }

    /**
     * Increase the evasion stage by amount stages.
     * The stage can't go above 6
     * @param amount The amount of stages to increase.
     */
    public void increaseEvasionStage(int amount) {
        evasionStage += amount;
        evasionStage = Math.min(evasionStage, 6);
    }

    /**
     * Set the attack stage by amount stages.
     * The stage can't go above 6
     * @param amount The new stage amount
     */
    public void setAttackStage(int amount) {
        attackStage = amount;
        if (amount < 0) {
            attackStage = Math.max(-6, attackStage);
        } else {
            attackStage = Math.min(6, attackStage);
        }
    }

    /**
     * Set the defense stage by amount stages.
     * The stage can't go above 6 or below -6
     * @param amount The new stage amount
     */
    public void setDefenseStage(int amount) {
        defenseStage = amount;
        if (amount < 0) {
            defenseStage = Math.max(-6, defenseStage);
        } else {
            defenseStage = Math.min(6, defenseStage);
        }
    }

    /**
     * Set the special attack stage by amount stages.
     * The stage can't go above 6 or below -6
     * @param amount The new stage amount
     */
    public void setSpAttackStage(int amount) {
        specialAttackStage = amount;
        if (amount < 0) {
            specialAttackStage = Math.max(-6, specialAttackStage);
        } else {
            specialAttackStage = Math.min(6, specialAttackStage);
        }
    }

    /**
     * Set the special defense stage by amount stages.
     * The stage can't go above 6 or below -6
     * @param amount The new stage amount
     */
    public void setSpDefenseStage(int amount) {
        specialDefenseStage = amount;
        if (amount < 0) {
            specialDefenseStage = Math.max(-6, specialDefenseStage);
        } else {
            specialDefenseStage = Math.min(6, specialDefenseStage);
        }
    }

    /**
     * Set the speed stage by amount stages.
     * The stage can't go above 6 or below -6
     * @param amount The new stage amount
     */
    public void setSpeedStage(int amount) {
        speedStage = amount;
        if (amount < 0) {
            speedStage = Math.max(-6, speedStage);
        } else {
            speedStage = Math.min(6, speedStage);
        }
    }

    /**
     * Set the accuracy stage by amount stages.
     * The stage can't go above 6 or below -6
     * @param amount The new stage amount
     */
    public void setAccuracyStage(int amount) {
        accuracyStage = amount;
        if (amount < 0) {
            accuracyStage = Math.max(-6, accuracyStage);
        } else {
            accuracyStage = Math.min(6, accuracyStage);
        }
    }

    /**
     * Set the evasion stage by amount stages.
     * The stage can't go above 6 or below -6
     * @param amount The new stage amount
     */
    public void setEvasionStage(int amount) {
        evasionStage = amount;
        if (amount < 0) {
            evasionStage = Math.max(-6, evasionStage);
        } else {
            evasionStage = Math.min(6, evasionStage);
        }
    }

    /**
     * Return the Pokemon's attack stage.
     * @return Attack Stage
     */
    public int getAttackStage() {
        return attackStage;
    }

    /**
     * Return the Pokemon's defense stage.
     * @return Defense Stage
     */
    public int getDefenseStage() {
        return defenseStage;
    }

    /**
     * Return the Pokemon's special attack stage.
     * @return Special Attack Stage
     */
    public int getSpecialAttackStage() {
        return specialAttackStage;
    }

    /**
     * Return the Pokemon's special defense stage.
     * @return Special Defense Stage
     */
    public int getSpecialDefenseStage() {
        return specialDefenseStage;
    }

    /**
     * Return the Pokemon's speed stage.
     * @return Speed Stage
     */
    public int getSpeedStage() {
        return speedStage;
    }

    /**
     * Return the Pokemon's accuracy stage.
     * @return Accuracy Stage
     */
    public int getAccuracyStage() { return accuracyStage; }

    /**
     * Return the Pokemon's evasion stage.
     * @return Evasion Stage.
     */
    public int getEvasionStage() { return evasionStage; }

    /**
     * Convert the accuracy stage into the modifier and return it.
     * @return The accuracy modifier.
     */
    public double getAccuracyModifier(int accuracyStage) {
        if (accuracyStage >= 0) {
            return 1 + (1 / 3.0) * accuracyStage;
        } else {
            if (accuracyStage == -1) {
                return .75;
            } else if (accuracyStage == -2) {
                return .6;
            } else if (accuracyStage == -3) {
                return .5;
            } else if (accuracyStage == -4) {
                return .428;
            } else if (accuracyStage == -5) {
                return .375;
            } else if (accuracyStage == -6) {
                return .33;
            }
        }
        return 1; //default
    }

    /**
     * Convert the evasion stage into the modifier and return it.
     * @return The evasion modifier.
     */
    public double getEvasionModifier(int evasionStage) {
        if (evasionStage < 0) {
            return Math.abs(evasionStage) * (1 / 3.0) + 1;
        } else {
            if (evasionStage == 1) {
                return .75;
            } else if (evasionStage == 2) {
                return .6;
            } else if (evasionStage == 3) {
                return .5;
            } else if (evasionStage == 4) {
                return .428;
            } else if (evasionStage == 5) {
                return .375;
            } else if (evasionStage == 6) {
                return .33;
            }
        }
        return 1; //default
    }

    /**
     * Initialize the Pokemon's resistances.
     */
    private void initializeResistances() {
        resistances = new HashMap<PokemonType, Double>();
        resistances.put(PokemonType.NORMAL, 1.0);
        resistances.put(PokemonType.FIGHTING, 1.0);
        resistances.put(PokemonType.FLYING, 1.0);
        resistances.put(PokemonType.POISON, 1.0);
        resistances.put(PokemonType.GROUND, 1.0);
        resistances.put(PokemonType.ROCK, 1.0);
        resistances.put(PokemonType.BUG, 1.0);
        resistances.put(PokemonType.GHOST, 1.0);
        resistances.put(PokemonType.STEEL, 1.0);
        resistances.put(PokemonType.FAIRY, 1.0);
        resistances.put(PokemonType.FIRE, 1.0);
        resistances.put(PokemonType.WATER, 1.0);
        resistances.put(PokemonType.GRASS, 1.0);
        resistances.put(PokemonType.ELECTRIC, 1.0);
        resistances.put(PokemonType.PSYCHIC, 1.0);
        resistances.put(PokemonType.ICE, 1.0);
        resistances.put(PokemonType.DRAGON, 1.0);
        resistances.put(PokemonType.DARK, 1.0);
        initializeResistances(battleTypeOne);
        if (battleTypeTwo != PokemonType.NONE) {
            initializeResistances(battleTypeTwo);
        }
    }

    /**
     * Return whether or not the Pokemon's defense can be lowered
     * due to their ability.
     * @return Whether or not the Pokemon's defense can be lowered
     * due to their ability.
     */
    public boolean isProtectedByDefenseLoweringEffects() {
        if (ability == AbilityId.BIG_PECKS) {
            return true;
        }
        return false;
    }

    /**
     * Return the total speed this Pokemon has at the time of an
     * attack.
     * @param field The total field at the time of the attack.
     * @param pokemonField The Pokemon's side of the field at the time
     *                     of the attack.
     * @return The total speed this Pokemon has when it attacks.
     */
    public double getTotalSpeed(Field field, SubField pokemonField) {
        double userSpeed = pokemon.getSpeedStat();
        int stage = getSpeedStage();
        if (stage > 0) {
            userSpeed = userSpeed * ((2.0 + stage) / 2.0);
        } else if (stage < 0) {
            userSpeed = userSpeed * (2.0 / (Math.abs(stage) + 2.0));
        }
        if (isParalyzed()) {
            userSpeed *= 0.5;
        }
        if (ability == AbilityId.SAND_RUSH &&
                field.getWeatherType() == WeatherType.SAND) {
            userSpeed *= 2;
        } else if (ability == AbilityId.SWIFT_SWIM &&
                (field.getWeatherType() == WeatherType.RAIN ||
                        field.getWeatherType() == WeatherType.HEAVY_RAIN)) {
            userSpeed *= 2;
        } else if (ability == AbilityId.CHLOROPHYLL &&
                (field.getWeatherType() == WeatherType.SUN ||
                        field.getWeatherType() == WeatherType.HARSH_SUNSHINE)) {
            userSpeed *= 2;
        }
        if (pokemonField.hasTailwind()) {
            userSpeed *= 2;
        }
        return userSpeed;
    }

    public void mitigateAttack(Skill usedSkill) {
        if (usedSkill.getMoveType() == PokemonType.ELECTRIC) {
            if (ability == AbilityId.LIGHTNINGROD) {
                getLightningRodAbsorbEffect();
            } else if (ability == AbilityId.MOTOR_DRIVE) {
                getMotorDriveAbsorbEffect();
            } else if (ability == AbilityId.VOLT_ABSORB) {
                getVoltAbsorbAbsorbEffect();
            }
        } else if (usedSkill.getMoveType() == PokemonType.WATER) {
            if (ability == AbilityId.DRY_SKIN) {
                getDrySkinAbsorbEffect();
            } else if (ability == AbilityId.WATER_ABSORB) {
                getWaterAbsorbAbsorbEffect();
            }
        }
    }

    /**
     * Return the AbsorbResults when Lightning Rod absorbs an
     * attack successfully while gaining Lightning Rod's absorbtion
     * effects.
     * @return Volt Absorbs successful AbsorbResults.
     */
    private void getLightningRodAbsorbEffect() {
        String name = pokemon.getConstantVariables().getName();
        Gdx.app.log("Battle", name + " takes no damage due to their ability Lightning Rod.");
        if (specialAttackStage < 6) {
            Gdx.app.log("Battle", name + "'s Special Attack rose!");
            increaseSpAttackStage(1);
        } else {
            Gdx.app.log("Battle", name + "'s Special Attack cannot go higher.");
        }
    }

    /**
     * Return the AbsorbResults when Motor Drive absorbs an
     * attack successfully while gaining Motor Drive's absorbtion
     * effects.
     * @return Motor Drive's successful AbsorbResults.
     */
    private void getMotorDriveAbsorbEffect() {
        String name = pokemon.getConstantVariables().getName();
        Gdx.app.log("Battle",name + " takes no damage due to their ability Motor Drive.");
        if (speedStage < 6) {
            Gdx.app.log("Battle",name + "'s Speed rose!");
            increaseSpeedStage(1);
        } else {
            Gdx.app.log("Battle",name + "'s Speed cannot go higher.");
        }
    }

    /**
     * Return the AbsorbResults when Dry Skin absorbs an
     * attack successfully while gaining Dry Skin's absorbtion
     * effects.
     * @return Dry Skin's successful AbsorbResults.
     */
    private void getDrySkinAbsorbEffect() {
        String name = pokemon.getConstantVariables().getName();
        if (!PokemonUtils.isFullHealth(pokemon)) {
            Gdx.app.log("Battle",name + " recovered health due to their ability Dry Skin.");
            PokemonUtils.healPokemon(pokemon, (int)Math.round(pokemon.getHealthStat() / 4.0));
        } else {
            Gdx.app.log("Battle","Absorbed attack using Dry Skin");
        }
    }

    /**
     * Return the AbsorbResults when Water Absorb absorbs an
     * attack successfully while gaining Water Absorb's absorbtion
     * effects.
     * @return Water Absorbs successful AbsorbResults.
     */
    private void getWaterAbsorbAbsorbEffect() {
        String name = pokemon.getConstantVariables().getName();
        if (!PokemonUtils.isFullHealth(pokemon)) {
            Gdx.app.log("Battle",name + " recovered health due to their ability Water Absorb.");
            PokemonUtils.healPokemon(pokemon, (int)Math.round(pokemon.getHealthStat() / 4.0));
        } else {
            Gdx.app.log("Battle","Absorbed attack using Water Absorb.");
        }
    }

    /**
     * Return the AbsorbResults when Volt Absorb absorbs an
     * attack successfully while gaining Volt Absorb's absorbtion
     * effects.
     * @return Volt Absorbs successful AbsorbResults.
     */
    private void getVoltAbsorbAbsorbEffect() {
        String name = pokemon.getConstantVariables().getName();
        if (!PokemonUtils.isFullHealth(pokemon)) {
            Gdx.app.log("Battle",name + " recovered health due to their ability Volt Absorb.");
            PokemonUtils.healPokemon(pokemon, (int)Math.round(pokemon.getHealthStat() / 4.0));
        } else {
            Gdx.app.log("Battle","Absorbed attack using Volt Absorb.");
        }
    }

    public boolean isPoisonable() {
        if (!isStatused() && pokemon.getCurrentHealth() != 0 &&
                ability != AbilityId.SHIELD_DUST &&
                !(battleTypeOne == PokemonType.POISON || battleTypeTwo == PokemonType.POISON) &&
                !(battleTypeOne == PokemonType.STEEL || battleTypeTwo == PokemonType.STEEL)) {
            return true;
        }
        return false;
    }

    public boolean isParalyzable() {
        if (!isStatused() && pokemon.getCurrentHealth() != 0 &&
                ability != AbilityId.SHIELD_DUST &&
                ability != AbilityId.LIMBER &&
                !(battleTypeOne == PokemonType.ELECTRIC || battleTypeTwo == PokemonType.ELECTRIC)) {
            return true;
        }
        return false;
    }

    public boolean isSleepable() {
        if (!isStatused() && ability != AbilityId.INSOMNIA && !uproaring) {
            return true;
        }
        return false;
    }
    public boolean isFreezable() {
        if (!isStatused() && pokemon.getCurrentHealth() != 0 && ability != AbilityId.SHIELD_DUST &&
                !(battleTypeOne == PokemonType.ICE || battleTypeTwo == PokemonType.ICE)) {
            return true;
        }
        return false;
    }

    public boolean isBurnable() {
        if (!isStatused() && pokemon.getCurrentHealth() != 0 && ability != AbilityId.SHIELD_DUST &&
                !(battleTypeOne == PokemonType.FIRE || battleTypeTwo == PokemonType.FIRE)) {
            return true;
        }
        return false;
    }

    public boolean isConfusable() {
        if (!confused && ability != AbilityId.OWN_TEMPO) {
            return true;
        }
        return false;
    }

    public boolean isFlinchable() {
        if (ability != AbilityId.INNER_FOCUS && ability != AbilityId.SHIELD_DUST) {
            return true;
        }
        return false;
    }


    /**
     * Initialize the Pokemon's resistances for the specified
     * type.
     * This method gets called twice. Once for typeOne,
     * second for typeTwo.
     * @param type The type of the Pokemon, either one or two.
     */
    private void initializeResistances(PokemonType type) {
        if (type == PokemonType.NORMAL) {
            resistances.put(PokemonType.FIGHTING,
                    resistances.get(PokemonType.FIGHTING) *  2);
            resistances.put(PokemonType.GHOST,
                    resistances.get(PokemonType.GHOST) *  0);
        } else if (type == PokemonType.FIGHTING) {
            resistances.put(PokemonType.FLYING,
                    resistances.get(PokemonType.FLYING) * 2);
            resistances.put(PokemonType.ROCK,
                    resistances.get(PokemonType.ROCK) * 0.5);
            resistances.put(PokemonType.BUG,
                    resistances.get(PokemonType.BUG) * 0.5);
            resistances.put(PokemonType.PSYCHIC,
                    resistances.get(PokemonType.PSYCHIC) * 2);
            resistances.put(PokemonType.DARK,
                    resistances.get(PokemonType.DARK) * 0.5);
            resistances.put(PokemonType.FAIRY, resistances.get(PokemonType.FAIRY) * 2);
        } else if (type == PokemonType.FLYING) {
            resistances.put(PokemonType.FIGHTING,
                    resistances.get(PokemonType.FIGHTING)  * 0.5);
            resistances.put(PokemonType.GROUND, 0.0);
            resistances.put(PokemonType.ROCK,
                    resistances.get(PokemonType.ROCK) * 2);
            resistances.put(PokemonType.BUG,
                    resistances.get(PokemonType.BUG) * 0.5);
            resistances.put(PokemonType.GRASS,
                    resistances.get(PokemonType.GRASS) * 0.5);
            resistances.put(PokemonType.ELECTRIC,
                    resistances.get(PokemonType.ELECTRIC) * 2);
            resistances.put(PokemonType.ICE,
                    resistances.get(PokemonType.ICE) * 2);
        } else if (type == PokemonType.POISON) {
            resistances.put(PokemonType.FIGHTING, resistances.get(PokemonType.FIGHTING)  * 0.5);
            resistances.put(PokemonType.POISON, resistances.get(PokemonType.POISON) * 0.5);
            resistances.put(PokemonType.GROUND, resistances.get(PokemonType.GROUND) * 2);
            resistances.put(PokemonType.BUG, resistances.get(PokemonType.BUG) * 0.5);
            resistances.put(PokemonType.GRASS, resistances.get(PokemonType.GRASS) * 0.5);
            resistances.put(PokemonType.PSYCHIC, resistances.get(PokemonType.PSYCHIC) * 2);
            resistances.put(PokemonType.FAIRY, resistances.get(PokemonType.FAIRY) * 0.5);
        } else if (type == PokemonType.GROUND) {
            resistances.put(PokemonType.POISON, resistances.get(PokemonType.POISON) * 0.5);
            resistances.put(PokemonType.ROCK, resistances.get(PokemonType.ROCK) * 0.5);
            resistances.put(PokemonType.WATER, resistances.get(PokemonType.WATER) * 2);
            resistances.put(PokemonType.GRASS, resistances.get(PokemonType.GRASS) * 2);
            resistances.put(PokemonType.ELECTRIC, 0.0);
            resistances.put(PokemonType.ICE, resistances.get(PokemonType.ICE) * 2);
        } else if (type == PokemonType.ROCK) {
            resistances.put(PokemonType.NORMAL, resistances.get(PokemonType.NORMAL) * 0.5);
            resistances.put(PokemonType.FIGHTING, resistances.get(PokemonType.FIGHTING) * 2);
            resistances.put(PokemonType.FLYING, resistances.get(PokemonType.FLYING) * 0.5);
            resistances.put(PokemonType.POISON, resistances.get(PokemonType.POISON) * 0.5);
            resistances.put(PokemonType.GROUND, resistances.get(PokemonType.GROUND) * 2);
            resistances.put(PokemonType.STEEL, resistances.get(PokemonType.STEEL) * 2);
            resistances.put(PokemonType.FIRE, resistances.get(PokemonType.FIRE) * 0.5);
            resistances.put(PokemonType.WATER, resistances.get(PokemonType.WATER) * 2);
            resistances.put(PokemonType.GRASS, resistances.get(PokemonType.GRASS) * 2);
        } else if (type == PokemonType.BUG) {
            resistances.put(PokemonType.FIGHTING, resistances.get(PokemonType.FIGHTING) * 0.5);
            resistances.put(PokemonType.FLYING, resistances.get(PokemonType.FLYING) * 2);
            resistances.put(PokemonType.GROUND, resistances.get(PokemonType.GROUND) * 0.5);
            resistances.put(PokemonType.ROCK, resistances.get(PokemonType.ROCK) * 2);
            resistances.put(PokemonType.FIRE, resistances.get(PokemonType.FIRE) * 2);
            resistances.put(PokemonType.GRASS, resistances.get(PokemonType.GRASS) * 0.5);
        } else if (type == PokemonType.GHOST) {
            resistances.put(PokemonType.NORMAL, 0.0);
            resistances.put(PokemonType.FIGHTING, 0.0);
            resistances.put(PokemonType.POISON, resistances.get(PokemonType.POISON) * 0.5);
            resistances.put(PokemonType.BUG, resistances.get(PokemonType.BUG) * 0.5);
            resistances.put(PokemonType.GHOST, resistances.get(PokemonType.GHOST) * 2);
            resistances.put(PokemonType.DARK, resistances.get(PokemonType.DARK) * 2);
        } else if (type == PokemonType.STEEL) {
            resistances.put(PokemonType.NORMAL, resistances.get(PokemonType.NORMAL) * 0.5);
            resistances.put(PokemonType.FIGHTING, resistances.get(PokemonType.FIGHTING) * 2);
            resistances.put(PokemonType.FLYING, resistances.get(PokemonType.FLYING) * 0.5);
            resistances.put(PokemonType.POISON, 0.0);
            resistances.put(PokemonType.GROUND, resistances.get(PokemonType.GROUND) * 2);
            resistances.put(PokemonType.ROCK, resistances.get(PokemonType.ROCK) * 0.5);
            resistances.put(PokemonType.BUG, resistances.get(PokemonType.BUG) * 0.5);
            resistances.put(PokemonType.STEEL, resistances.get(PokemonType.STEEL) * 0.5);
            resistances.put(PokemonType.FIRE, resistances.get(PokemonType.FIRE) * 2);
            resistances.put(PokemonType.GRASS, resistances.get(PokemonType.GRASS) * 0.5);
            resistances.put(PokemonType.PSYCHIC, resistances.get(PokemonType.PSYCHIC) * 0.5);
            resistances.put(PokemonType.ICE, resistances.get(PokemonType.ICE) * 0.5);
            resistances.put(PokemonType.DRAGON, resistances.get(PokemonType.DRAGON) * 0.5);
            resistances.put(PokemonType.FAIRY, resistances.get(PokemonType.FAIRY) * 0.5);
        } else if (type == PokemonType.FIRE) {
            resistances.put(PokemonType.GROUND, resistances.get(PokemonType.GROUND) * 2);
            resistances.put(PokemonType.ROCK, resistances.get(PokemonType.ROCK) * 2);
            resistances.put(PokemonType.BUG, resistances.get(PokemonType.BUG) * 0.5);
            resistances.put(PokemonType.STEEL, resistances.get(PokemonType.STEEL) * 0.5);
            resistances.put(PokemonType.FIRE, resistances.get(PokemonType.FIRE) * 0.5);
            resistances.put(PokemonType.WATER, resistances.get(PokemonType.WATER) * 2);
            resistances.put(PokemonType.GRASS, resistances.get(PokemonType.GRASS) * 0.5);
            resistances.put(PokemonType.ICE, resistances.get(PokemonType.ICE) * 0.5);
            resistances.put(PokemonType.FAIRY, resistances.get(PokemonType.FAIRY) * 0.5);
        } else if (type == PokemonType.WATER) {
            resistances.put(PokemonType.STEEL, resistances.get(PokemonType.STEEL) * 0.5);
            resistances.put(PokemonType.FIRE, resistances.get(PokemonType.FIRE) * 0.5);
            resistances.put(PokemonType.WATER, resistances.get(PokemonType.WATER) * 0.5);
            resistances.put(PokemonType.GRASS, resistances.get(PokemonType.GRASS) * 2);
            resistances.put(PokemonType.ELECTRIC, resistances.get(PokemonType.ELECTRIC) * 2);
            resistances.put(PokemonType.ICE, resistances.get(PokemonType.ICE) * 0.5);
        } else if (type == PokemonType.GRASS) {
            resistances.put(PokemonType.FLYING, resistances.get(PokemonType.FLYING) * 2);
            resistances.put(PokemonType.POISON, resistances.get(PokemonType.POISON) * 2);
            resistances.put(PokemonType.GROUND, resistances.get(PokemonType.GROUND) * 0.5);
            resistances.put(PokemonType.BUG, resistances.get(PokemonType.BUG) * 2);
            resistances.put(PokemonType.FIRE, resistances.get(PokemonType.FIRE) * 2);
            resistances.put(PokemonType.WATER, resistances.get(PokemonType.WATER) * 0.5);
            resistances.put(PokemonType.GRASS, resistances.get(PokemonType.GRASS) * 0.5);
            resistances.put(PokemonType.ELECTRIC, resistances.get(PokemonType.ELECTRIC) * 0.5);
            resistances.put(PokemonType.ICE, resistances.get(PokemonType.ICE) * 2);
        } else if (type == PokemonType.ELECTRIC) {
            resistances.put(PokemonType.FLYING, resistances.get(PokemonType.FLYING) * 0.5);
            resistances.put(PokemonType.GROUND, resistances.get(PokemonType.GROUND) * 2);
            resistances.put(PokemonType.STEEL, resistances.get(PokemonType.STEEL) * 0.5);
            resistances.put(PokemonType.ELECTRIC, resistances.get(PokemonType.ELECTRIC) * 0.5);
        } else if (type == PokemonType.PSYCHIC) {
            resistances.put(PokemonType.FIGHTING, resistances.get(PokemonType.FIGHTING) * 0.5);
            resistances.put(PokemonType.BUG, resistances.get(PokemonType.BUG) * 2);
            resistances.put(PokemonType.GHOST, resistances.get(PokemonType.GHOST) * 2);
            resistances.put(PokemonType.PSYCHIC, resistances.get(PokemonType.PSYCHIC) * 0.5);
            resistances.put(PokemonType.DARK, resistances.get(PokemonType.DARK) * 2);
        } else if (type == PokemonType.ICE) {
            resistances.put(PokemonType.FIGHTING, resistances.get(PokemonType.FIGHTING) * 2);
            resistances.put(PokemonType.ROCK, resistances.get(PokemonType.ROCK) * 2);
            resistances.put(PokemonType.STEEL, resistances.get(PokemonType.STEEL) * 2);
            resistances.put(PokemonType.FIRE, resistances.get(PokemonType.FIRE) * 2);
            resistances.put(PokemonType.ICE, resistances.get(PokemonType.ICE) * 0.5);
        } else if (type == PokemonType.DRAGON) {
            resistances.put(PokemonType.FIRE, resistances.get(PokemonType.FIRE) * 0.5);
            resistances.put(PokemonType.WATER, resistances.get(PokemonType.WATER) * 0.5);
            resistances.put(PokemonType.GRASS, resistances.get(PokemonType.GRASS) * 0.5);
            resistances.put(PokemonType.ELECTRIC, resistances.get(PokemonType.ELECTRIC) * 0.5);
            resistances.put(PokemonType.ICE, resistances.get(PokemonType.ICE) * 2);
            resistances.put(PokemonType.DRAGON, resistances.get(PokemonType.DRAGON) * 2);
            resistances.put(PokemonType.FAIRY, resistances.get(PokemonType.FAIRY) * 2);
        } else if (type == PokemonType.DARK) {
            resistances.put(PokemonType.FIGHTING, resistances.get(PokemonType.FIGHTING) * 2);
            resistances.put(PokemonType.BUG, resistances.get(PokemonType.BUG) * 2);
            resistances.put(PokemonType.GHOST, resistances.get(PokemonType.GHOST) * 0.5);
            resistances.put(PokemonType.FAIRY, resistances.get(PokemonType.FAIRY) * 2);
            resistances.put(PokemonType.PSYCHIC,  0.0);
            resistances.put(PokemonType.DARK, resistances.get(PokemonType.DARK) * 0.5);
        } else if (type == PokemonType.FAIRY) {
            resistances.put(PokemonType.FIGHTING, resistances.get(PokemonType.FIGHTING) * 0.5);
            resistances.put(PokemonType.POISON, resistances.get(PokemonType.POISON) * 2);
            resistances.put(PokemonType.BUG, resistances.get(PokemonType.BUG) * 0.5);
            resistances.put(PokemonType.DRAGON, 0.0);
            resistances.put(PokemonType.STEEL,  resistances.get(PokemonType.STEEL) * 2);
            resistances.put(PokemonType.DARK, resistances.get(PokemonType.DARK) * 0.5);
        }
    }

    /**
     * Return the amount of exp this Pokemon gives based on the number
     * of participants in the battle.
     * @param numberOfBattleParticipants The number of participants in the
     *                                   battle.
     * @param a Whether or not the battle was a trainer battle or not. (1 for wild 1.5 for trainer)
     * @return The amount of exp the Pokemon will give.
     */
    public long calculateExp(int numberOfBattleParticipants, double a) {
        int b = pokemon.getConstantVariables().getBaseExp();
        double e = 1; //1.5 if holding lucky egg
        int l = pokemon.getUniqueVariables().getLevel();
        int t = 1; //1.5 if the pokemon was traded.
        int s = 1; //The number of pokemon who participated in the party

        long exp = Math.round((a * b * e * l * t) / (7 * s));
        if (ScriptParameters.DEBUG_EXP) {
            exp = exp * ScriptParameters.EXP_RATE;
        }
        Gdx.app.log("Experience: ", "" + exp);
        return exp;
    }

    /**
     * Add exp to the Pokemon
     * @param amt The amount of exp to be added.
     */
    public void addExp(double amt) {
        pokemon.getUniqueVariables().addExp(amt);
    }

    /**
     * Set the amount of exp to amt.
     * @param amt The amount of exp getting set to the currentExp
     */
    public void setExp(int amt) {
        pokemon.getUniqueVariables().setExp(amt);
        displayedExp = amt;
    }

    public double getCurrentExp() {
        return pokemon.getUniqueVariables().getCurrentExp();
    }

    /**
     * Return the Pokemon's resistance hash map.
     * @return A hash map of the Pokemon's resistances. The
     * attack type is the key, and the resist modifier for the value.
     */
    public HashMap<PokemonType, Double> getResistances() {
        return resistances;
    }
    
    public int getFuryCutterStacks() {
        return furyCutterStacks;
    }

    public int getEchoedVoiceStacks() { return echoedVoiceStacks; }

    /**
     * Return whether or not the Pokemon is powered up by the move Charge.
     * @return Whether or not the Pokemon is powered up by the move Charge.
     */
    public boolean isCharged() {
        if (chargeTurns > 0) {
            return true;
        }
        return false;
    }

    /**
     * Start uproar, prevents sleep for all Pokemon on the field.
     */
    public void uproar() {
        uproaring = true;
    }

    /**
     * Stop the uproar.
     */
    public void stopUproar() {
        uproaring = false;
    }

    /**
     * Return whether or not the Pokemon is Uproaring.
     * @return Whether or not the Pokemon is Uproaring.
     */
    public boolean isUproaring() {
        return uproaring;
    }
    /**
     * Return whether or not the Pokemon just used the move Rage.
     * @return Whether or not the Pokemon just used the move Rage.
     */
    public boolean usedRage() {
        return usedRage;
    }


    /**
     * Set that the Pokemon just used the move Rage.
     */
    public void useRage() {
        usedRage = true;
    }

    /**
     * Remove the Rage effect from the move Rage from the Pokemon
     */
    public void removeRage() {
        usedRage = false;
    }

    /**
     * Adjust the Pokemon's Charge from the move charge if they have the Charge.
     */
    public void adjustCharge() {
        if (chargeTurns > 0) {
            chargeTurns--;
        }
    }

    /**
     * Apply Charge effect to the Pokemon.
     */
    public void applyCharge() {
        chargeTurns = 2; //Two turns, one for charge, one for the next move.
    }

    /**
     * Return whether or not this will be/is the first turn the Pokemon is on the Field.
     * @return Whether or not this will be/is the first turn the Pokemon is on the Field.
     */
    public boolean isFirstTurn() {
        return firstTurn;
    }

    /**
     * Return whether or not the Pokemon has a crit available for their next attack.
     * @return Whether or not the Pokemon will crit their next attack.
     */
    public boolean hasCrit() {
        if (laserFocusTurns > 0) {
            return true;
        }
        return false;
    }

    /**
     * Set the amount of turns laser focus is active. 2 because laser focus uses the first turn
     * and the second turn is the crit turn.
     */
    public void useLaserFocus() {
        laserFocusTurns = 2;
    }

    /**
     * Adjust the Laser Focus effect duration.
     */
    public void adjustLaserFocus() {
        if (laserFocusTurns > 0) {
            laserFocusTurns--;
        }
    }

    public boolean isLockedOn() {
        if (lockOnTurns > 0) {
            return true;
        }
        return false;
    }

    public void lockOn() {
        lockOnTurns = 2;
    }

    public void removeLockOn() {
        lockOnTurns = 0;
    }

    public void adjustLockOn() {
        if (lockOnTurns > 0) {
            lockOnTurns--;
        }
    }

    /**
     * Set whether or not this is this first turn the Pokemon is on the Field.
     * @param firstTurn Whether or not this is the first turn the Pokemon is on the Field.
     */
    public void setFirstTurn(boolean firstTurn) {
        this.firstTurn = firstTurn;
    }

    public void setFuryCutterStacks(int stacks) {
        furyCutterStacks = Math.min(3, stacks);
    }

    public void setEchoedVoiceStacks(int stacks) {
        echoedVoiceStacks = Math.min(4, stacks);
    }

    public void receiveTransferrableBattleVariables(BattlePokemon transferPokemon) {
        if (transferPokemon.isReceivingWish()) {
            wishTurns = transferPokemon.getWishTurns();
            wishUser = transferPokemon.getWishUser();
            receivingWish = true;
        }
        if (transferPokemon.witnessedFutureSight()) {
            futureSightTime = transferPokemon.getFutureSightTime();
            witnessedFutureSight = true;
            futureSightUser = transferPokemon.getFutureSightUser();
        } else if (transferPokemon.witnessedDoomDesire()) {
            doomDesireTime = transferPokemon.getDoomDesireTime();
            witnessedDoomDesire = true;
            futureSightUser = transferPokemon.getFutureSightUser();
        }

    }



    public void setTurnDamageTaken(int damageAmount, SkillCategory damageCategory) {
        this.turnDamageTaken = damageAmount;
        this.damageTakenCategory = damageCategory;
        tookDamageThisTurn = true;
    }

    public boolean tookPhysicalDamageThisTurn() {
        if (turnDamageTaken > 0 && damageTakenCategory == SkillCategory.PHYSICAL) {
            return true;
        }
        return false;
    }

    public boolean tookSpecialDamageThisTurn() {
        if (turnDamageTaken > 0 && damageTakenCategory == SkillCategory.SPECIAL) {
            return true;
        }
        return false;
    }

    public boolean tookEnemyDamageThisTurn() {
        if (turnDamageTaken > 0) {
            return true;
        }
        return false;
    }

    public boolean tookDamageThisTurn() {
        return tookDamageThisTurn;
    }

    public void takeDamageThisTurn() {
        tookDamageThisTurn = true;
    }

    public int getDamageTakenThisTurn() {
        return turnDamageTaken;
    }

    /**
     * Apply the move Powder to the Pokemon.
     */
    public void applyPowder() {
        powdered = true;
    }

    /**
     * Return whether or not this Pokemon is affected by the move
     * Powder.
     * @return
     */
    public boolean isPowdered() {
        return powdered;
    }

    /**
     * Remove the Powder effect from the Pokemon.
     */
    public void removePowder() {
        powdered = false;
    }

    /**
     * Remove all of the Stockpile stacks.
     */
    public void removeStockpileStacks() {
        stockpileStacks = 0;
    }

    /**
     * Add a Stockpile stack. Maxes out at 3.
     */
    public void stockpile() {
        stockpileStacks++;
        stockpileStacks = Math.min(stockpileStacks, 3);
    }

    /**
     * Return whether or not the Pokemon has any Stockpile stacks.
     * @return Whether or not the Pokemon has any Stockpile stacks.
     */
    public boolean hasStockpileStacks() {
        if (stockpileStacks > 0) {
            return true;
        }
        return false;
    }

    /**
     * Return the number of stockpile stacks the Pokemon has.
     * @return
     */
    public int getStockpileStacks() {
        return stockpileStacks;
    }

    public void resetDamageTakenThisTurn() {
        turnDamageTaken = 0;
        damageTakenCategory = SkillCategory.MISC;
    }

    /**
     * Set the Pokemon to be sky dropped for the next turn.
     */
    public void setSkyDrop() {
        skyDrop = true;
    }

    /**
     * Remove the Pokemon from being sky dropped the next turn.
     */
    public void removeSkyDrop() {
        skyDrop = false;
    }

    /**
     * Return whether or not the Pokemon is in the air after being
     * hit by Sky Drop.
     * @return Whether or not the Pokemon is in the air after being
     * hit by Sky Drop.
     */
    public boolean isSkyDropped() {
        return skyDrop;
    }


    /**
     * Return the number of rollout turns left.
     * 0 if there Pokemon hasn't started rolling out yet.
     * @return The number of rollout turns left.
     */
    public int getRolloutTurns() {
        return rolloutTurns;
    }

    /**
     * Cancel the Pokemon from Rolling out if it is
     * using rollout.
     */
    public void cancelRollout() {
        if (rolloutTurns > 0) {
            nextTurnSkill = null;
            rolloutTurns = 0;
        }
    }

    /**
     * Add a turn to the rollout chain.
     */
    public void addRolloutTurn() {
        rolloutTurns++;
    }

    /**
     * Cancel the effects that get cancelled when the
     * Pokemon misses their target. Ex: Rollout.
     */
    public void cancelMissSkills(BattlePokemon target) {
        cancelRollout();
        furyCutterStacks = 0;
        nextTurnSkill = null;
        flying = false;
        underground = false;
        underwater = false;
        target.removeSkyDrop();
    }

    /**
     * Cancel the effects that get cancelled when the
     * Pokemon flinches. Ex: Rollout
     */
    public void cancelFlinchSkills() {
        cancelRollout();
    }

    /**
     * Start using rollout.
     * @param rollout The rollout skill that will be used on the next
     *                turn.
     */
    public void startRollout(Skill rollout) {
        rolloutTurns = 1;
        nextTurnSkill = rollout;
    }
    /**
     * Return whether or not the Pokemon is Binded by
     * the move Bind.
     * @return
     */
    public boolean isBinded() {
        return binded;
    }


    /**
     * Return whether or not the Pokemon is in a Whirlpool.
     * @return Whether or not the Pokemon is in the Whirlpool
     * vortex.
     */
    public boolean inWhirlpool() { return inWhirlpool; }

    /**
     * Return whether or not the Pokemon is in a Fire Spin.
     * @return Whether or not the Pokemon is trapped in Fire
     * Spin.
     */
    public boolean inFireSpin() { return fireSpin; }

    /**
     * Return whether or not the Pokemon is in a Magma Storm.
     * @return Whether or not the Pokemon is trapped in Magma
     * Storm.
     */
    public boolean inMagmaStorm() { return magmaStorm; }


    /**
     * Return whether or not the Pokemon is infested by Infestation
     * @return Whether or not the Pokemon is infested by
     * the move Infestation.
     */
    public boolean isInfested() { return infested; }


    /**
     * Return whether or not the Pokemon is trapped in Sand Tomb.
     * @return Whether or not the Pokemon is trapped in Sand Tomb.
     */
    public boolean inSandTomb() { return sandTomb; }


    /**
     * Return whether or not the Pokemon is Clamped by the move Clamp.
     * @return Whether or not the Pokemon is Clamped.
     */
    public boolean isClamped() {
        return clamped;
    }

    /**
     * Return whether or not the Pokemon is wrapped.
     * @return Whether or not the Pokemon is wrapped.
     */
    public boolean isWrapped() { return wrapped; }

    /**
     * Bind the Pokemon with the skill effect from the move
     * Bind.
     */
    public void bind() {
        binded = true;
        if (Math.random() < .5) {
            bindedTurns = 4;
        } else {
            bindedTurns = 5;
        }
    }

    public void whirlpool() {
        inWhirlpool = true;
        if (Math.random() < .5) {
            whirlpoolTurns = 4;
        } else {
            whirlpoolTurns = 5;
        }
    }

    /**
     * Clamp the Pokemon with the skill effect from the move
     * Clamp.
     */
    public void clamp() {
        clamped = true;
        double rand = Math.random();
        if (rand < .375) {
            clampTurns = 2;
        } else if (rand <= .75) {
            clampTurns = 3;
        } else if (rand <= .875) {
            clampTurns = 4;
        } else {
            clampTurns = 5;
        }
    }

    /**
     * Trap the Pokemon in Fire Spin.
     */
    public void trapInFireSpin() {
        fireSpin = true;
        if (Math.random() < .5) {
            fireSpinTurns = 4;
        } else {
            fireSpinTurns = 5;
        }
    }

    /**
     * Trap the Pokemon in Magma Storm.
     */
    public void trapInMagmaStorm() {
        magmaStorm = true;
        if (Math.random() < .5) {
            magmaStormTurns = 4;
        } else {
            magmaStormTurns = 5;
        }
    }

    /**
     * Trap the Pokemon in Infestation
     */
    public void trapInInfestation() {
        infested = true;
        if (Math.random() < .5) {
            infestationTurns = 4;
        } else {
            infestationTurns = 5;
        }
    }

    /**
     * Trap the Pokemon in Sand Tomb.
     */
    public void trapInSandTomb() {
        sandTomb = true;
        if (Math.random() < .5) {
            sandTombTurns = 4;
        } else {
            sandTombTurns = 5;
        }
    }

    /**
     * Trap the Pokemon in Wrap.
     */
    public void wrap() {
        wrapped = true;
        if (Math.random() < .5) {
            wrapTurns = 4;
        } else {
            wrapTurns = 5;
        }
    }

    /**
     * Remove the clamp effect from the Pokemon.
     */
    public void removeClamp() {
        clamped = false;
    }

    /**
     * Remove the Bind effect from the Pokemon.
     */
    public void removeBind() {
        binded = false;
    }


    /**
     * Remove Whirlpool effect.
     */
    public void removeWhirlpool() { inWhirlpool = false; }

    /**
     * Remove the Pokemon from Magma Storm.
     */
    public void removeMagmaStorm() {
        magmaStorm = false;
    }

    /**
     * Remove the Pokemon from Fire Spin.
     */
    public void removeFireSpin() {
        fireSpin = false;
    }

    /**
     * Remove the Pokemon from Infestation.
     */
    public void removeInfestation() {
        infested = false;
    }

    /**
     * Remove the Pokemon from Sand Tomb.
     */
    public void removeSandTomb() {
        sandTomb = false;
    }

    /**
     * Remove the Pokemon from Wrap.
     */
    public void removeWrap() { wrapped = false; }

    /**
     * Remove all Bind effects from the Pokemon. (Bind, Wrap, Fire Spin etc)
     */
    public void freeFromBinds() {
        binded = false;
        bindedTurns = 0;
        clamped = false;
        clampTurns = 0;
        inWhirlpool = false;
        whirlpoolTurns = 0;
        fireSpin = false;
        fireSpinTurns = 0;
        magmaStorm = false;
        magmaStormTurns = 0;
        infested = false;
        infestationTurns = 0;
        sandTomb = false;
        sandTombTurns = 0;
        wrapped = false;
        wrapTurns = 0;
    }

    /**
     * Return the number of turns left until Bind expires.
     * @return The number of turns left until Bind expires.
     */
    public int getBindTurns() {
        return bindedTurns;
    }

    /**
     * Return the number of turns left until Clamp expires.
     * @return The number of turns left until Clamp expires.
     */
    public int getClampTurns() { return clampTurns; }

    /**
     * Return the number of turns left until Whirlpool expires.
     * @return The number of turns left until Whirlpool expires.
     */
    public int getWhirlpoolTurns() { return whirlpoolTurns; }

    /**
     * Return the number of turns left until Fire Spin expires.
     * @return The number of turns left until Fire Spin expires.
     */
    public int getFireSpinTurns() { return fireSpinTurns; }

    /**
     * Return the number of turns left until Magma Storm expires.
     * @return The number of turns left until Magma Storm expires.
     */
    public int getMagmaStormTurns() { return magmaStormTurns; }

    /**
     * Return the number of turns left until Infestation expires.
     * @return The number of turns left until Infestation expires.
     */
    public int getInfestationTurns() { return infestationTurns; }

    /**
     * Return the number of turns left until Sand Tomb expires.
     * @return The number of turns left until Sand Tomb expires.
     */
    public int getSandTombTurns() { return sandTombTurns; }

    /**
     * Return the number of turns left until Wrap expires.
     * @return The number of turns left until Wrap expires.
     */
    public int getWrapTurns() { return wrapTurns; }

    /**
     * Adjust the number of Clamp turns left.
     */
    public void adjustClampTurns() {
        clampTurns--;
    }

    /**
     * Adjust the number of Bind turns left.
     */
    public void adjustBindTurns() {
        bindedTurns--;
    }

    /**
     * Adjust the number of Whirlpool turns left.
     */
    public void adjustWhirlpoolTurns() { whirlpoolTurns--; }

    /**
     * Adjust the number of Fire Spin turns left.
     */
    public void adjustFireSpinTurns() { fireSpinTurns--; }

    /**
     * Adjust the number of Magma Storm turns left.
     */
    public void adjustMagmaStormTurns() { magmaStormTurns--; }

    /**
     * Adjust the number of Infestation turns left.
     */
    public void adjustInfestationTurns() { infestationTurns--; }

    /**
     * Adjust the number of Sand Tomb turns left.
     */
    public void adjustSandTombTurns() {
        sandTombTurns--;
    }

    /**
     * Adjust the number of Wrap turns left.
     */
    public void adjustWrapTurns() { wrapTurns--; }

    /**
     * Return whether or not this Pokemon will receive
     * wish.
     * @return Whether or not this Pokemon will receive Wish.
     */
    public boolean isReceivingWish() {
        return receivingWish;
    }

    /**
     * The number of turns until Wish effects the Pokemon.
     * @return The number of turns until Wish effects the
     * Pokemon.
     */
    public int getWishTurns() {
        return wishTurns;
    }

    /**
     * Adjust the number of turns left until Wish is
     * activated.
     */
    public void adjustWishTurns() {
        wishTurns--;
        wishTurns = Math.max(0, wishTurns);
    }

    /**
     * Remove the Wish effect from the Pokemon.
     */
    public void removeWish() {
        receivingWish = false;
        wishTurns = -1;
        wishUser = "";
    }

    /**
     * Return the name of the Pokemon who used Wish.
     * @return The name of the Pokemon who used Wish;
     */
    public String getWishUser() {
        return wishUser;
    }

    /**
     * Use the Wish effect on the Pokemon.
     * - They will receive the heal after a turn.
     */
    public void receiveWish(String wishUser) {
        receivingWish = true;
        wishTurns = TOTAL_WISH_TURNS;
        this.wishUser = wishUser;
    }

    /**
     * Return whether or not the Pokemon has flinched.
     * @return Whether or not the Pokemon flinched.
     */
    public boolean hasFlinched() {
        return flinched;
    }

    /**
     * Make the Pokemon flinch.
     */
    public void flinch() {
        flinched = true;
    }

    /**
     * Remove the flinch status from the Pokemon.
     */
    public void removeFlinch() {
        flinched = false;
    }

    /**
     * Get taunted and set the time the Pokemon is taunted
     * to the maximum taunt time.
     */
    public void receiveTaunt() {
        isTaunted = true;
        tauntTime = 3;
    }

    /**
     * Return whether or not the Pokemon is taunted.
     * @return Whether or not the Pokemon is taunted.
     */
    public boolean isTaunted() {
        return isTaunted;
    }

    /**
     * Receive the Embargo status.
     */
    public void receiveEmbargo() {
        isEffectedByEmbargo = true;
        embargoTime = TOTAL_EMBARGO_TIME;
    }

    /**
     * Return whether or not the Pokemon is effected by
     * the Embargo move.
     * @return Whether or not the Pokemon is effected by
     * the Embargo move.
     */
    public boolean isEffectedByEmbargo() {
        return isEffectedByEmbargo;
    }

    /**
     * Make the skill at the index slot get encored.
     * @param slot The move slot that gets encored.
     */
    public void receiveEncore(int slot) {
        isEffectedByEncore = true;
        encoreSlot = slot;
        encoreTime = TOTAL_ENCORE_TIME;
    }

    /**
     * Return whether or not the Pokemon is encored.
     * @return Whether or not the Pokemon is encored.
     */
    public boolean isEncored() {
        return isEffectedByEncore;
    }

    /**
     * Return which move slot is encored. -1 if the
     * Pokemon is not effected by encore.
     * @return Which move slot is encored. -1 if the
     * Pokemon is not encored.
     */
    public int getEncoredSlot() {
        return encoreSlot;
    }

    /**
     * Use the Heal Block effect on the Pokemon.
     */
    public void receiveHealBlock() {
        isHealBlocked = true;
        healBlockTime = TOTAL_HEAL_BLOCK_TIME;
    }

    /**
     * Return whether or not the Pokemon is heal blocked.
     * @return Whether or not the Pokemon is heal blocked.
     */
    public boolean isHealBlocked() {
        return isHealBlocked;
    }

    /**
     * Lift the Pokemon by the Telekinesis effect.
     */
    public void receiveTelekinesis() {
        isLiftedByTelekinesis = true;
        telekinesisTime = TOTAL_TELEKINESIS_TIME;
    }

    /**
     * Return whether or not the Pokemon is effected by
     * the Telekinesis move.
     * @return Whether or not the Pokemon is effected by
     * the Telekinesis move.
     */
    public boolean isLiftedByTelekinesis() {
        return isLiftedByTelekinesis;
    }

    /**
     * Use the Magnet Rise move effect on the Pokemon.
     */
    public void receiveMagnetRise() {
        isMagnetRisen = true;
        magnetRisenTime = TOTAL_MAGNET_RISE_TIME;
    }

    /**
     * Return whether or not the Pokemon is effected by
     * Magnet Rise.
     * @return Whether or not the Pokemon is effected by
     * Magnet Rise.
     */
    public boolean isMagnetRisen() {
        return isMagnetRisen;
    }

    /**
     * Get the Yawn effect.
     */
    public void receiveYawn() {
        justReceivedYawn = true;
        fallAsleepDueToYawnThisTurn = false;
    }

    /**
     * Return whether or not the Pokemon is effected by Yawn.
     * @return Whether or not the Pokemon is effected by Yawn.
     */
    public boolean isYawned() {
        return (justReceivedYawn || fallAsleepDueToYawnThisTurn);
    }

    /**
     * Return whether or not the Pokemon just received the yawn effect.
     * @return Whether or not the Pokemon just received the yawn effect.
     */
    public boolean justReceivedYawn() {
        return justReceivedYawn;
    }

    /**
     * Change the yawn variables so that when the next turn ends, the Pokemon will fall asleep.
     */
    public void passFirstYawnTurn() {
        justReceivedYawn = false;
        fallAsleepDueToYawnThisTurn = true;
    }

    /**
     * Use Nightmare on the Pokemon.
     */
    public void receiveNightmare() {
        hasNightmares = true;
    }

    /**
     * Remove Nightmares from the Pokemon.
     */
    public void removeNightmare() { hasNightmares = false; }

    /**
     * Return whether or not the Pokemon is effected by
     * Nightmare.
     * @return Whether or not the Pokemon is effected by
     * Nightmare.
     */
    public boolean hasNightmares() {
        return hasNightmares;
    }


    /**
     * Give the Pokemon the Curse effect.
     */
    public void giveCurse() {
        isCursed = true;
    }

    /**
     * Return whether or not the Pokemon is affected by Curse.
     * @return Whether or not the Pokemon is affected by Curse.
     */
    public boolean isCursed() {
        return isCursed;
    }

    /**
     * Remove curse from the Pokemon.
     */
    public void removeCurse() {
        isCursed = false;
    }

    /**
     * Use the Ingrain move on the Pokemon.
     */
    public void receiveIngrain() {
        isIngrained = true;
    }

    /**
     * Return whether or not the Pokemon is ingrained.
     * @return Whether or not the Pokemon is ingrained.
     */
    public boolean isIngrained() {
        return isIngrained;
    }

    /**
     * Apply the Spider Web to the Pokemon.
     */
    public void applySpiderWeb() {
        spiderWebbed = true;
    }

    /**
     * Return whether or not the Pokemon is trapped in
     * Spider Web.
     * @return
     */
    public boolean isSpiderWebbed() {
        return spiderWebbed;
    }

    /**
     * Remove the Spider Web effect from the Pokemon.
     */
    public void removeSpiderWeb() {
        spiderWebbed = false;
    }

    /**
     * Let the Pokemon get leech seeded.
     */
    public void receiveLeechSeed() {
        isLeechSeeded = true;
    }

    /**
     * Return whether or not the Pokemon is effected
     * by Leech Seed.
     * @return Whether or not the Pokemon is effected by
     * Leech Seed.
     */
    public boolean isSeeded() {
        return isLeechSeeded;
    }

    /**
     * Remove Leech Seed from the Pokemon.
     */
    public void removeLeechSeed() {
        isLeechSeeded = false;
    }

    /**
     * Envelop the Pokemon in Aqua Ring.
     */
    public void receiveAquaRing() {
        envelopedInAquaRing = true;
    }

    /**
     * Return whether or not the Pokemon has Aqua Ring
     * activated.
     * @return Whether or not the Pokemon has Aqua Ring
     * activated.
     */
    public boolean isEnvelopedInAquaRing() {
        return envelopedInAquaRing;
    }

    /**
     * Set the Pokemon to receive Future Sight damage after
     * a few turns.
     */
    public void receiveFutureSight(Pokemon skillUser) {
        witnessedFutureSight = true;
        futureSightTime = TOTAL_FUTURE_SIGHT_TIME;
        futureSightUser = skillUser;
    }

    /**
     * Return the Pokemon who casted Future Sight on this one.
     * @return The Pokemon who casted Future Sight on this Pokemon.
     */
    public Pokemon getFutureSightUser() {
        return futureSightUser;
    }

    /**
     * Return the number of turns left until future sight hits.
     * @return The number of turns left until future sight hits.
     */
    public int getFutureSightTime() {
        return futureSightTime;
    }

    /**
     * Adjust the number of turns left until future sight
     * hits.
     */
    public void adjustFutureSightTime() {
        futureSightTime--;
        futureSightTime = Math.max(0, futureSightTime);
    }

    public void removeFutureSight() {
        witnessedFutureSight = false;
        futureSightUser = null;
        futureSightTime = -1;
    }

    /**
     * Return whether or not the Pokemon has been hit by
     * Future Sight and hasn't received the damage yet.
     * @return Whether or not the Pokemon has been hit by
     * Future Sight and hasn't received the damage yet.
     */
    public boolean witnessedFutureSight() {
        return witnessedFutureSight;
    }

    /**
     * Adjust the number of turns left until Doom Desire
     * hits.
     */
    public void adjustDoomDesireTime() {
        doomDesireTime--;
        doomDesireTime = Math.max(0, doomDesireTime);
    }

    /**
     * Return the number of turns left until Doom Desire hits.
     * @return The number of turns left until Doom Desire hits.
     */
    public int getDoomDesireTime() {
        return doomDesireTime;
    }

    /**
     * Set the Pokemon to receive Doom Desire damage after
     * a few turns.
     */
    public void receiveDoomDesire(Pokemon skillUser) {
        witnessedDoomDesire = true;
        doomDesireTime = TOTAL_DOOM_DESIRE_TIME;
        futureSightUser = skillUser;
    }

    public void removeDoomDesire() {
        witnessedDoomDesire = false;
        futureSightUser = null;
        doomDesireTime = -1;
    }

    /**
     * Return whether or not the Pokemon has been hit by
     * Doom Desire and hasn't received the damage yet.
     * @return Whether or not the Pokemon has been hit by
     * Doom Desire  and hasn't received the damage yet.
     */
    public boolean witnessedDoomDesire() {
        return witnessedDoomDesire;
    }

    /**
     * Set the move slot that gets disabled.
     * @param slot The move at index slot that gets disabled.
     */
    public void receiveDisable(int slot) {
        disabledSlot = slot;
        disabledTime = TOTAL_DISABLE_TIME;
    }

    /**
     * Return whether or not the first move is disabled.
     * @return Whether or not the first move is diabled.
     */
    public boolean isFirstMoveDisabled() {
        if (disabledSlot == 0) {
            return true;
        }
        return false;
    }

    /**
     * Return whether or not the second move is disabled.
     * @return Whether or not the second move is diabled.
     */
    public boolean isSecondMoveDisabled() {
        if (disabledSlot == 1) {
            return true;
        }
        return false;
    }

    /**
     * Return whether or not the third move is disabled.
     * @return Whether or not the third move is diabled.
     */
    public boolean isThirdMoveDisabled() {
        if (disabledSlot == 2) {
            return true;
        }
        return false;
    }

    /**
     * Return whether or not the fourth move is disabled.
     * @return Whether or not the fourth move is diabled.
     */
    public boolean isFourthMoveDisabled() {
        if (disabledSlot == 3) {
            return true;
        }
        return false;
    }

    /**
     * Let the Pokemon hear Perish Song.
     */
    public void receivePerishSong() {
        heardPerishSong = true;
        perishSongTime = TOTAL_PERISH_SONG_TIME;
    }

    /**
     * Return whether or not the Pokemon heard Perish Song.
     * @return Whether or not the Pokemon heard Perish Song.
     */
    public boolean heardPerishSong() {
        return heardPerishSong;
    }

    public int getPerishSongTime() {
        return perishSongTime;
    }

    public void adjustPerishSongDuration() {
        perishSongTime--;
    }

    public void removePerishSong() {
        perishSongTime = -1;
        heardPerishSong = false;
    }


    /**
     * Put the Pokemon to sleep and give it a random sleep time.
     */
    public void induceSleep() {
        pokemon.getUniqueVariables().setStatus(StatusCondition.SLEEP);
        double rand = Math.random();
        if (rand <= .33) {
            sleepTime = 2;
        } else if (rand <= .67) {
            sleepTime = 3;
        } else {
            sleepTime = 4;
        }
    }

    public void setPreStatus(StatusCondition status) {
        preStatus = status;
    }

    /**
     * Put the Pokemon to sleep for 2 turns.
     */
    public void induceRestSleep() {
        preStatus = StatusCondition.SLEEP;
        sleepTime = 2; //Sleep for rest is always 2 turns.
    }

    /**
     * Reduce the amount of turns the Pokemon is asleep for by
     * 1 turn.
     */
    public void reduceSleepTime() {
        sleepTime--;
        sleepTime = Math.max(sleepTime, 0);
    }

    /**
     * Return the amount of turns the Pokemon is asleep for.
     * @return The amount of turns the Pokemon is asleep for.
     */
    public int getSleepTime() {
        return sleepTime;
    }

    /**
     * Remove the Sleep status from the Pokemon and reset the sleepTime.
     */
    public void wakeUp() {
        sleepTime = 0;
        pokemon.getUniqueVariables().setStatus(StatusCondition.STATUS_FREE);
        //Reset yawn
        justReceivedYawn = false;
        fallAsleepDueToYawnThisTurn = false;
        hasNightmares = false;
    }

    /**
     * Apply confusion to the Pokemon that will last 1-4 attacking turns.
     */
    public void induceConfusion() {
        double rand = Math.random();
        if (rand <= .25) {
            confusionTime = 1;
        } else if (rand <= .5) {
            confusionTime = 2;
        } else if (rand <= .75) {
            confusionTime = 3;
        } else {
            confusionTime = 4;
        }
        Gdx.app.log("InduceConfusion", "Confusion Time: " + confusionTime);
        confused = true;
    }

    /**
     * Reduce the confusion time by 1 turn.
     */
    public void reduceConfusionTime() {
        confusionTime--;
        confusionTime = Math.max(confusionTime, NOT_CONFUSED);
    }

    /**
     * Return how many turns of confusion are left.
     * @return Number of confusion turns left.
     */
    public int getConfusionTime() {
        return confusionTime;
    }

    /**
     * Remove confusion from the Pokemon.
     */
    public void removeConfusion() {
        confusionTime = 0;
        confused = false;
    }

    /**
     * Return whether or not the Pokemon is confused.
     * @return Whether or not the Pokemon is confused.
     */
    public boolean isConfused() {
        return confused;
    }

    /**
     * Return whether or not the Pokemon is outraging.
     * Ex: When they are locked into Thrash, Petal Dance, Outrage etc.
     * @return Whether or not the Pokemon is outraging.
     */
    public boolean isOutraging() {
        if (outrageTurns == 0) {
            return false;
        }
        return true;
    }

    /**
     * Set the outrage skill for the Pokemon.
     * When the Pokemon first uses Outrage/Thrash this is called.
     * @param outrageSkill The skill they are outraging with.
     * @param outrageTurns The time left for the outrage.
     */
    public void activateOutrage(Skill outrageSkill, int outrageTurns) {
        this.outrageSkill = outrageSkill;
        this.outrageTurns = outrageTurns;
    }

    /**
     * Reduce the amount of time the Pokemon is outraging for
     * by 1 turn.
     */
    public void reduceOutrageTime() {
        outrageTurns--;
    }

    /**
     * Return the amount of turns left for the outrage.
     * @return The amount of turns left for the outrage.
     */
    public int getOutrageTime() {
        return outrageTurns;
    }

    /**
     * Return the outrage skill, otherwise return null.
     * @return The outraging skill.
     */
    public Skill getOutrageSkill() {
        return outrageSkill;
    }

    /**
     * Remove the outrage skill from the Pokemon.
     * (They are no longer stuck in it!)
     */
    public void removeOutrageSkill() {
        outrageSkill = null;
    }

    /**
     * Return the skill that will be used next turn.
     * Ex: When fly is first used the next turn skill is fly.
     * @return The skill that will be used in the next turn.
     */
    public Skill getNextTurnSkill() {
        return nextTurnSkill;
    }

    /**
     * Return whether or not the Pokemon has a skill ready for
     * the next turn.
     * Ex: Fly, Dig, Sky Attack etc.
     * @return Whether or not the Pokemon has a skill ready for
     * the next turn.
     */
    public boolean hasNextTurnSkill() {
        if (nextTurnSkill != null) {
            return true;
        }
        return false;
    }

    /**
     * Remove the next turn skill.
     */
    public void removeNextTurnSkill() {
        nextTurnSkill = null;
    }

    /**
     * Set the next turn skill to the argument.
     * @param nextTurnSkill The new next turn skill.
     */
    public void setNextTurnSkill(Skill nextTurnSkill) {
        this.nextTurnSkill = nextTurnSkill;
    }

    /**
     * Return whether or not the Pokemon is in the air after using
     * Fly.
     * @return Whether or not the Pokemon is in the air flying.
     */
    public boolean isFlying() {
        return flying;
    }

    /**
     * Return whether or not the Pokemon is ungrounded
     * @return Whether or not the Pokemon is ungrounded
     */
    public boolean isUngrounded() {
        return ability == AbilityId.LEVITATE || battleTypeOne == PokemonType.FLYING || battleTypeTwo == PokemonType.FLYING
                || flying || isMagnetRisen || isLiftedByTelekinesis;
    }

    /**
     * Return whether or not the Pokemon is underground after
     * using Dig.
     * @return Whether or not the Pokemon is underground.
     */
    public boolean isUnderground() {
        return underground;
    }

    /**
     * Return whether or not the Pokemon is underwater after using
     * Dive.
     * @return Whether or not the Pokemon is underwater.
     */
    public boolean isUnderwater() {
        return underwater;
    }

    /**
     * Finish using the Dive skill by removing the next turn skill
     * and come up from underwater.
     */
    public void finishDive() {
        setNextTurnSkill(null);
        underwater = false;
    }

    /**
     * Finish using Fly or Bounce by removing the next turn skill and come down.
     */
    public void flyDown() {
        setNextTurnSkill(null);
        flying = false;
    }

    /**
     * Finish using Dig by removing the next turn skill and come up.
     */
    public void finishDig() {
        setNextTurnSkill(null);
        underground = false;
    }

    /**
     * Use fly or bounce
     */
    public void fly() {
        flying = true;
    }

    /**
     * Use dig.
     */
    public void dig() { underground = true; }

    public boolean isSemiInvulnerable() {
        if (underwater || underground || flying) {
            return true;
        }
        return false;
    }

    /**
     * Return whether or not the Pokemon is recharging after using
     * a Hyper Beam or similar move.
     * @return
     */
    public boolean isRecharging() {
        return recharging;
    }

    /**
     * Initiate recharging after using a Hyper Beam.
     */
    public void initiateRecharge() {
        recharging = true;
    }

    /**
     * Recharge the Pokemon from recharging after a Hyper Beam or
     * similar move.
     */
    public void recharge() {
        recharging = false;
    }

    /**
     * Make the Pokemon go underwater using Dive's effect.
     */
    public void dive() {
        underwater = true;
    }


    public boolean isFocused() {
        return focused;
    }

    public void focus() {
        focused = true;
    }

    public boolean isBurned() {
        return pokemon.getUniqueVariables().getStatus() == StatusCondition.BURN;
    }

    public void inflictBurn() {
        pokemon.getUniqueVariables().setStatus(StatusCondition.BURN);
    }

    public void inflictPoison() {
        pokemon.getUniqueVariables().setStatus(StatusCondition.POISON);
    }

    public void inflictParalysis() {
        pokemon.getUniqueVariables().setStatus(StatusCondition.PARALYSIS);
    }

    public void inflictSleep() {
        pokemon.getUniqueVariables().setStatus(StatusCondition.SLEEP);
    }

    public void inflictFreeze() {
        pokemon.getUniqueVariables().setStatus(StatusCondition.FROZEN);
    }

    public boolean isStatused() {
        return pokemon.getUniqueVariables().getStatus() != StatusCondition.STATUS_FREE &&
                pokemon.getUniqueVariables().getStatus() != StatusCondition.RECOVER;
    }

    public boolean isPoisoned() {
        return pokemon.getUniqueVariables().getStatus() == StatusCondition.POISON;
    }

    public boolean isParalyzed() {
        return pokemon.getUniqueVariables().getStatus() == StatusCondition.PARALYSIS;
    }

    public boolean isSleeping() {
        return pokemon.getUniqueVariables().getStatus() == StatusCondition.SLEEP;
    }

    public boolean isFrozen() {
        return pokemon.getUniqueVariables().getStatus() == StatusCondition.FROZEN;
    }
    
}
