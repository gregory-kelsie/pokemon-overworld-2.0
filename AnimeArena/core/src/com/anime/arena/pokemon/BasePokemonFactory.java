package com.anime.arena.pokemon;

import com.anime.arena.field.WeatherType;
import com.anime.arena.skill.*;
import com.anime.arena.skill.effect.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.compression.lzma.Base;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasePokemonFactory {
    private String basePokemonResponse;
    private Map<Integer, JsonObject> database;
    private Map<Integer, List<JsonObject>> pokemonFormDatabase;
    private Map<Integer, List<JsonObject>> evolutionDatabase;
    private Map<Integer, List<String>> abilityDatabase;
    private Map<Integer, JsonObject> moveDatabase;

    private static final boolean useDatabase = true;

    public BasePokemonFactory() {
        this.basePokemonResponse = "";
        this.database = new HashMap<Integer, JsonObject>();
    }

    public BasePokemonFactory(String basePokemonResponse, Map<Integer, JsonObject> database) {
        this.basePokemonResponse = basePokemonResponse;
        this.database = database;
    }

    public void initDatabase(String basePokemonResponse, Map<Integer, JsonObject> database) {
        this.basePokemonResponse = basePokemonResponse;
        this.database = database;
    }

    public List<JsonObject> getForms(int dexNumber) {
        List<JsonObject> forms = new ArrayList<JsonObject>();
        for (Integer key : database.keySet()) {
            JsonObject pokemon = database.get(key);
            if (pokemon.get("dex").getAsInt() == dexNumber) {
                forms.add(pokemon);
            }
        }
        return forms;
    }

    public boolean hasDexNumber(int dexNumber) {
        return database.containsKey(dexNumber);
    }

    public Map<Integer, JsonObject> getDatabase() {
        return database;
    }

    public void setEvolutionDatabase(Map<Integer, List<JsonObject>> evolutionDatabase) {
        this.evolutionDatabase = evolutionDatabase;
    }

    public void setMoveDatabase(Map<Integer, JsonObject> moveDatabase) {
        this.moveDatabase = moveDatabase;
    }

    public void setAbilityDatabase(Map<Integer, List<String>> abilityDatabase) {
        this.abilityDatabase = abilityDatabase;
    }

    public Map<Integer, List<String>> getAbilityDatabase() {
        return abilityDatabase;
    }

    public Skill getMove(int moveID) {
        JsonObject jsonMove = moveDatabase.get(moveID);
        int moveSubtype = jsonMove.get("move_subtype").getAsInt();
        switch (moveSubtype) {
            case 0: //Damaging Skill
                return createDamagingMove(jsonMove, moveSubtype);
            case 1: //Damaging Skill with Secondary Effect
                int effectRate = jsonMove.get("effect_rate").getAsInt();
                return createDamagingMove(jsonMove, moveSubtype);
            case 2:
                return createDamagingMove(jsonMove, moveSubtype);
            case 5:
                return createEffectMove(jsonMove);
            default:
                return null;
        }
    }

    private DamageSkill createDamagingMove(JsonObject jsonMove, int moveSubtype) {
        int id = jsonMove.get("move_id").getAsInt();
        String name = jsonMove.get("name").getAsString();
        String description = jsonMove.get("description").getAsString();
        SkillCategory category = SkillCategory.fromInt(jsonMove.get("category").getAsInt());
        int pp = jsonMove.get("pp").getAsInt();
        int basePower = jsonMove.get("base_power").getAsInt();
        int accuracy = jsonMove.get("accuracy").getAsInt();
        SkillTarget target = SkillTarget.ENEMY;
        PokemonType moveType = PokemonType.fromInt(jsonMove.get("type").getAsInt());
        int speedPriority = jsonMove.get("speed_priority").getAsInt();
        int criticalRate = jsonMove.get("critical_rate").getAsInt();
        int recoil = jsonMove.get("recoil").getAsInt();
        int drain = jsonMove.get("drain").getAsInt();
        DamageSkill damageSkill = new DamageSkill(id, name, description, category, pp, pp, accuracy, moveType, target, moveSubtype,
                speedPriority, basePower, criticalRate);
        if (recoil != 0) {
            if (recoil == 1) {
                damageSkill.setRecoilType(RecoilDrainType.ONE_FOURTH);
            } else if (recoil == 2) {
                damageSkill.setRecoilType(RecoilDrainType.ONE_THIRD);
            } else if (recoil == 3) {
                damageSkill.setRecoilType(RecoilDrainType.ONE_HALF);
            }
        } else if (drain != 0) {
            if (drain == 1) {
                damageSkill.setRecoilType(RecoilDrainType.GAIN_HALF);
            } else if (drain == 2) {
                damageSkill.setRecoilType(RecoilDrainType.GAIN_THREE_QUARTERS);
            }
        }
        if (moveSubtype == 1) {
            JsonArray secondaryEffectsJsonArray = jsonMove.get("secondary_effects").getAsJsonArray();
            createAndAddEffects(damageSkill, secondaryEffectsJsonArray);
        }
        return damageSkill;
    }

    private EffectSkill createEffectMove(JsonObject jsonMove) {
        int id = jsonMove.get("move_id").getAsInt();
        String name = jsonMove.get("name").getAsString();
        String description = jsonMove.get("description").getAsString();
        SkillCategory category = SkillCategory.fromInt(jsonMove.get("category").getAsInt());
        int pp = jsonMove.get("pp").getAsInt();
        int accuracy = jsonMove.get("accuracy").getAsInt();
        SkillTarget target = SkillTarget.fromInt(jsonMove.get("target").getAsInt());
        PokemonType moveType = PokemonType.fromInt(jsonMove.get("type").getAsInt());
        int speedPriority = jsonMove.get("speed_priority").getAsInt();
        EffectSkill effectSkill = new EffectSkill(id, name, description, category, pp, pp, accuracy, moveType, target, 0, speedPriority);
        JsonArray effectsJsonArray = jsonMove.get("effects").getAsJsonArray();
        createAndAddEffects(effectSkill, effectsJsonArray);
        return effectSkill;
    }

    private void createAndAddEffects(EffectSkill effectSkill, JsonArray effectsArray) {
        for (JsonElement effectJsonElement : effectsArray) {
            JsonObject effectObject = effectJsonElement.getAsJsonObject();
            EffectType effectType = EffectType.fromInt(effectObject.get("effect_type").getAsInt());
            switch(effectType) {
                case STAGE:
                    effectSkill.addEffect(createStageEffect(effectObject));
                    break;
                case STATUS:
                    effectSkill.addEffect(createStatusEffect(effectObject));
                    break;
                case WEATHER:
                    effectSkill.addEffect(createWeatherEffect(effectObject));
                    break;
                case BIND_TYPE:
                    effectSkill.addEffect(createBindEffect(effectObject));
                    break;
                default:
                    effectSkill.addEffect(createMiscEffect(effectObject, effectType));
                    break;

            }
        }
    }

    private Effect createWeatherEffect(JsonObject effectObject) {
        WeatherType weatherType = WeatherType.fromInt(effectObject.get("weather_type").getAsInt());
        switch (weatherType) {
            case RAIN:
                return new RainEffect();
            case SUN:
                return new SunEffect();
            case HAIL:
                return new HailEffect();
            case SAND:
                return new SandstormEffect();
            default:
                return null;
        }
    }

    private Effect createBindEffect(JsonObject effectObject) {
        BindType bindType = BindType.fromInt(effectObject.get("bind_type").getAsInt());
        switch (bindType) {
            case BIND_MOVE_TYPE:
                return new BindEffect();
            case CLAMP:
                return new ClampEffect();
            case WHIRLPOOL:
                return new BindEffect();
            case FIRE_SPIN:
                return new BindEffect();
            case MAGMA_STORM:
                return new BindEffect();
            case INFESTATION:
                return new BindEffect();
            case WRAP:
                return new BindEffect();
            case SAND_TOMB:
                return new BindEffect();
            default:
                return null;
        }
    }

    private Effect createMiscEffect(JsonObject effectObject, EffectType effectType) {
        switch (effectType) {
            case SPLASH:
                return new SplashEffect();
            default:
                return null;
        }
    }

    private Effect createStatusEffect(JsonObject effectObject) {
        StatusCondition statusType = StatusCondition.fromInt(effectObject.get("status_effect").getAsInt());
        SkillTarget target = SkillTarget.fromInt(effectObject.get("target").getAsInt());
        switch (statusType) {
            case BURN:
                return new BurnEffect(target);
            case PARALYSIS:
                return new ParalysisEffect(target);
            case POISON:
                return new PoisonEffect(target);
            case SLEEP:
                return new SleepEffect(target);
            case CONFUSION:
                return new ConfusionEffect(target);
            default:
                return null;
        }
    }

    private Effect createStageEffect(JsonObject effectObject) {
        StageType stageType = StageType.fromInt(effectObject.get("stage_type").getAsInt());
        SkillTarget target = SkillTarget.fromInt(effectObject.get("target").getAsInt());
        int stageAmount = effectObject.get("stage_amount").getAsInt();
        StatDirection direction;
        if (stageAmount < 0) {
            direction = StatDirection.DECREASE;
        } else {
            direction = StatDirection.INCREASE;
        }
        stageAmount = Math.abs(stageAmount);
        switch (stageType) {
            case ATTACK:
                return new AttackEffect(target, direction, stageAmount);
            case DEFENSE:
                return new DefenseEffect(target, direction, stageAmount);
            case SPECIAL_ATTACK:
                return new SpecialAttackEffect(target, direction, stageAmount);
            case SPECIAL_DEFENSE:
                return new SpecialDefenseEffect(target, direction, stageAmount);
            case SPEED:
                return new SpeedEffect(target, direction, stageAmount);
            case ACCURACY:
                return new AccuracyEffect(target, direction, stageAmount);
            case EVASION:
                return new EvasionEffect(target, direction, stageAmount);
        }
        return null;
    }


    private List<Evolution> getEvolutions(int pid) {
        if (evolutionDatabase != null && evolutionDatabase.containsKey(pid)) {
            List<JsonObject> evolutionJson = evolutionDatabase.get(pid);
            List<Evolution> evolutions = new ArrayList<Evolution>();
            for (JsonObject obj : evolutionJson) {
                String gender;
                if (!obj.get("gender").isJsonNull()) {
                    gender = obj.get("gender").getAsString();
                } else {
                    gender = null;
                }
                Evolution evolution = new Evolution(obj.get("level").getAsInt(), obj.get("evolved-form").getAsInt(),
                        obj.get("method").getAsInt(), obj.get("attack").getAsBoolean(), obj.get("defense").getAsBoolean(), obj.get("equal").getAsBoolean(),
                        obj.get("second-evolution").getAsInt(), gender, obj.get("nature-type").getAsInt());
                evolutions.add(evolution);
            }
            return evolutions;
        }
        return new ArrayList<Evolution>();
    }

    public BasePokemon createBasePokemon(JsonObject pokemon) {
        if (pokemon != null) {
            BasePokemon base = new BasePokemon();
            base.setPID(pokemon.get("pid").getAsInt());
            base.setName(pokemon.get("name").getAsString());
            base.setDexNumber(pokemon.get("dex").getAsInt());
            base.setClassification(pokemon.get("classification").getAsString());
            base.setDescription(pokemon.get("description").getAsString());
            base.setImage(pokemon.get("img").getAsString());
            base.setCaptureRate(pokemon.get("capture_rate").getAsDouble());
            base.setHeight(pokemon.get("height").getAsDouble());
            base.setWeight(pokemon.get("weight").getAsDouble());
            base.setBaseExp(pokemon.get("base_exp").getAsInt());
            base.setFirstAbility(pokemon.get("first_ability").getAsInt());
            base.setSecondAbility(pokemon.get("second_ability").getAsInt());
            base.setHiddenAbility(pokemon.get("hidden_ability").getAsInt());
            base.setFirstType(pokemon.get("type_one").getAsInt());
            base.setSecondType(pokemon.get("type_two").getAsInt());
            base.setBaseStats(new int[] {pokemon.get("base_hp").getAsInt(), pokemon.get("base_atk").getAsInt(),
                    pokemon.get("base_def").getAsInt(),pokemon.get("base_spatk").getAsInt(),pokemon.get("base_spdef").getAsInt(),
                    pokemon.get("base_spd").getAsInt()});
            base.setEvYield(new int[] {pokemon.get("yield_hp").getAsInt(), pokemon.get("yield_atk").getAsInt(),
                    pokemon.get("yield_def").getAsInt(),pokemon.get("yield_spatk").getAsInt(),pokemon.get("yield_spdef").getAsInt(),
                    pokemon.get("yield_spd").getAsInt()});
            base.setEvolutionList(getEvolutions(base.getPID()));
            if (pokemon.has("level_up_moves")) {
                JsonArray levelUpMovesJsonArray = pokemon.get("level_up_moves").getAsJsonArray();
                for (JsonElement levelUpMoveJsonElement : levelUpMovesJsonArray) {
                    JsonObject levelUpMoveObject = levelUpMoveJsonElement.getAsJsonObject();
                    int level = levelUpMoveObject.get("level").getAsInt();
                    int moveID = levelUpMoveObject.get("move_id").getAsInt();
                    base.addLevelUpMove(level, moveID);
                }
            }
            return base;
        }
        return null;
    }

    public BasePokemon createBasePokemonDB(int pokemonID) {
        return createBasePokemon(database.get(pokemonID));
    }

    public BasePokemon createBasePokemonString(int pokemonID) {
        JsonArray pokemonArray  = new JsonParser().parse(basePokemonResponse).getAsJsonArray();
        int i = 0;
        boolean foundPokemon = false;
        JsonObject pokemon = null;
        for (JsonElement obj : pokemonArray) {
            JsonObject jo = obj.getAsJsonObject();
            if (jo.get("pid").getAsInt() == pokemonID) {
                pokemon = jo;
                break;
            }
        }
        return createBasePokemon(pokemon);
    }

    public BasePokemon createBasePokemon(int dexNumber) {
        if (useDatabase) {
            return createBasePokemonDB(dexNumber);
        } else {
            return createBasePokemonString(dexNumber);
        }
    }

    public BasePokemon createBasePokemonFromTestFile(String filename) {
        FileHandle file = Gdx.files.internal("test/pokemon/" + filename + ".json");
        String text = file.readString();
        JsonObject jsonPokemon = new JsonParser().parse(text).getAsJsonObject();
        return createBasePokemon(jsonPokemon);
    }
}
