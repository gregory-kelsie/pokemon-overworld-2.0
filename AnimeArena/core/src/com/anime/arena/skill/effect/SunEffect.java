package com.anime.arena.skill.effect;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.field.WeatherType;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.skill.MoveEffectSubtype;
import com.anime.arena.skill.SkillTarget;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class SunEffect extends Effect {

    public SunEffect() {
        super(MoveEffectSubtype.WEATHER, SkillTarget.FIELD);
    }

    @Override
    public List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        List<String> results = new ArrayList<String>();
        if (field.getWeatherType() == WeatherType.HEAVY_RAIN ||
                field.getWeatherType() == WeatherType.HARSH_SUNSHINE ||
                field.getWeatherType() == WeatherType.SUN) {
            Gdx.app.log("SunEffect", "Sunlight failed due to heavy rain, harsh sunshine or there is already sunlight");
            return results;
        }
        if (field.getWeatherType() == WeatherType.SAND) {
            results.add("The sandstorm subsided.");
        } else if (field.getWeatherType() == WeatherType.RAIN) {
            results.add("The rain stopped.");
        } else if (field.getWeatherType() == WeatherType.HAIL) {
            results.add("The hail stopped.");
        }
        field.setWeather(WeatherType.SUN);
        results.add("The sunlight turned harsh!");
        return results;
    }



    public boolean willFailAsSingleEffect(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField,
                                          SubField enemyField, boolean isFirstAttack) {
        if (field.getWeatherType() == WeatherType.HEAVY_RAIN ||
                field.getWeatherType() == WeatherType.HARSH_SUNSHINE ||
                field.getWeatherType() == WeatherType.SUN) {
            return true;
        } else if (enemyPokemon.getAbility() == AbilityId.CLOUD_NINE) {
            return true;
        }
        return false;
    }
}
