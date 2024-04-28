package com.anime.arena.battle.endturnlogic;

import com.anime.arena.battle.BattleStatePokemon;
import com.anime.arena.field.Field;
import com.anime.arena.field.WeatherType;
import com.badlogic.gdx.Gdx;

public class WeatherStep extends AbstractEndTurnStep {
    public WeatherStep(BattleStatePokemon firstAttacker, BattleStatePokemon secondAttacker, Field field) {
        super(firstAttacker, secondAttacker, field);
        this.hasImpactOnPokemon = false;
    }

    @Override
    protected void executeStep(BattleStatePokemon battleStatePokemon) {
        if (field.onLastWeatherTurn()) {
            resultsText = getWeatherResultText(true);
            field.clearWeather();
        } else {
            resultsText = getWeatherResultText(false);
            field.adjustWeather();
        }

        if (!resultsText.equals("")) {
            currentState = StepState.DISPLAYING_RESULTS;
        } else {
            currentState = StepState.COMPLETED;
        }
        battleLog("END WEATHER CHECK");
    }

    private String getWeatherResultText(boolean cleared) {
        if (field.getWeatherType() == WeatherType.HAIL) {
            if (cleared) {
                battleLog("WEATHER - THE HAIL STOPPED");
                return "The hail stopped.";
            } else {
                battleLog("WEATHER - THE HAIL CONTINUES TO FALL (" + field.getWeatherLength() + ")");
                return "The hail continues to fall!";
            }
        } else if (field.getWeatherType() == WeatherType.SAND) {
            if (cleared) {
                battleLog("WEATHER - THE SANDSTORM SUBSIDED");
                return "The sandstorm subsided.";
            } else {
                battleLog("WEATHER - THE SANDSTORM RAGES (" + field.getWeatherLength() + ")");
                return "The sandstorm rages!";
            }
        } else if (field.getWeatherType() == WeatherType.RAIN ||
                field.getWeatherType() == WeatherType.HEAVY_RAIN) {
            if (cleared) {
                battleLog("WEATHER - THE RAIN STOPPED");
                return "The rain stopped!";
            } else {
                battleLog("WEATHER - THE RAIN CONTINUES TO FALL (" + field.getWeatherLength() + ")");
                return "The rain continues to fall!";
            }
        } else if (field.getWeatherType() == WeatherType.SUN ||
                field.getWeatherType() == WeatherType.HARSH_SUNSHINE) {
            if (cleared) {
                battleLog("WEATHER - THE SUNLIGHT FADED");
                return "The sunlight faded.";
            } else {
                battleLog("WEATHER - THE SUNLIGHT IS STRONG (" + field.getWeatherLength() + ")");
                return "The sunlight is strong.";
            }
        } else if (field.getWeatherType() == WeatherType.STRONG_WINDS) {
            if (cleared) {
                battleLog("WEATHER - THE MYSTERIOUS AIR CURRENT HAS DISSIPATED");
                return "The mysterious air current has dissipated.";
            } else {
                battleLog("WEATHER - THE MYSTERIOUS AIR CURRENT BLOWS ON REGARDLESS (" + field.getWeatherLength() + ")");
                return "The mysterious air current blows on regardless.";
            }
        } else {
            battleLog("WEATHER - NO WEATHER EFFECT IN PROGRESS");
            return "No weather effect in progress.";
        }
    }


    private void battleLog(String str) {
        Gdx.app.log("EndTurnState::WeatherStep", str);
    }
}
