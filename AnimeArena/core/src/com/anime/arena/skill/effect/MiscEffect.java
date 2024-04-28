package com.anime.arena.skill.effect;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.field.WeatherType;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.skill.MoveEffectSubtype;
import com.anime.arena.skill.SkillTarget;

import java.util.ArrayList;
import java.util.List;

public class MiscEffect extends Effect {
    private boolean activatingSunlight;
    private boolean activatingRain;
    private boolean activatingSandstorm;
    private boolean activatingStrongSunshine;
    private boolean activatingHeavyRain;
    private boolean activatingStrongWind;

    private boolean activatingElectricTerrain;
    private boolean activatingMistyTerrain;
    private boolean activatingPsychicTerrain;
    private boolean activatingGrassyTerrain;

    private boolean activatingTailwind;
    private boolean activatingIngrain;

    private boolean activatingLeechSeed;

    private MiscEffectType miscEffectType;
    public MiscEffect(MoveEffectSubtype subtype, SkillTarget target, MiscEffectType miscEffectType) {
        super(subtype, target);
        this.miscEffectType = miscEffectType;
    }

    @Override
    public List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        if (miscEffectType == MiscEffectType.RAIN) {
            List<String> results = new ArrayList<String>();
            if (field.getWeatherType() == WeatherType.SAND) {
                results.add("The sandstorm subsided.");
            } else if (field.getWeatherType() == WeatherType.SUN) {
                results.add("The sunlight faded.");
            } else if (field.getWeatherType() == WeatherType.HAIL) {
                results.add("The hail stopped.");
            }
            field.setWeather(WeatherType.RAIN);
            results.add("It started to rain!");
            return results;
        }
        return null;
    }

    private void useRain() {

    }

    public boolean willFailAsSingleEffect(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        if (miscEffectType == MiscEffectType.RAIN) {
            if (field.getWeatherType() == WeatherType.HEAVY_RAIN ||
                    field.getWeatherType() == WeatherType.HARSH_SUNSHINE ||
                    field.getWeatherType() == WeatherType.RAIN) {
                return true;
            } else if (enemyPokemon.getAbility() == AbilityId.CLOUD_NINE) {
                return true;
            }
        }
        return false;
    }
}
