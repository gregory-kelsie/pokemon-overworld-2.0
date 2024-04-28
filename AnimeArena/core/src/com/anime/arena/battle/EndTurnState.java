package com.anime.arena.battle;

import com.anime.arena.battle.endturnlogic.EndTurnStep;
import com.anime.arena.battle.ui.BattleTextBox;
import com.anime.arena.battle.ui.BattleTextBoxComponent;
import com.anime.arena.battle.ui.FaintAnimation;
import com.anime.arena.battle.ui.HealthUpdater;
import com.anime.arena.field.Field;
import com.anime.arena.field.WeatherType;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;
import com.anime.arena.pokemon.StatusCondition;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EndTurnState extends BattleState {
    private final int WEATHER = 0;
    private final int DISPLAY_TEXT = 1;
    private final int CHECK_BLACK_OUT = 2;
    private final int USE_WEATHER_DAMAGE = 3;
    private final int USE_HAIL = 4;
    private final int CHECK_FUTURE_SIGHT = 5;
    private final int ADJUST_ENEMY_HEALTH = 6;
    private final int ADJUST_PLAYER_HEALTH = 7;
    private final int FAINT_ENEMY_POKEMON = 8;
    private final int FAINT_PLAYER_POKEMON = 9;
    private final int DISPLAY_ENEMY_FAINT_TEXT = 10;
    private final int DISPLAY_PLAYER_FAINT_TEXT = 11;
    private final int USE_SAND = 12;
    private final int USE_RAIN = 13;
    private final int USE_SUN = 14;
    private final int DOUBLE_KNOCKOUT = 15;
    private final int END_GAME = 16;
    private final int WISH_STATE = 17;
    private final int HEALING_ABILITIES = 18;
    private final int AQUA_RING_STATE = 19;
    private final int INGRAIN_STATE = 20;
    private final int LEECH_SEED = 21;
    private final int RECEIVE_LEECH_SEED_DRAIN = 22;
    private final int NEGATIVE_STATUS = 23;
    private final int NIGHTMARES = 24;
    private final int CURSE = 25;
    private final int BIND_STATE = 26;
    private final int CLAMP_STATE = 27;
    private final int WHIRLPOOL_STATE = 28;
    private final int MIST_STATE = 29;
    private final int TAILWIND = 30;
    private final int FIRE_SPIN = 31;
    private final int INFESTATION = 32;
    private final int SAND_TOMB = 33;
    private final int REFLECT = 34;
    private final int LIGHT_SCREEN = 35;
    private final int TRICK_ROOM = 36;
    private final int PERISH_SONG = 37;
    private final int YAWN = 38;
    private final int WRAP = 39;
    private final int MAGMA_STORM = 40;


    private Field field;
    private BattleStatePokemon firstAttacker;
    private BattleStatePokemon secondAttacker;
    private BattleStatePokemon faintingPokemon;
    private BattleStep stepAfterEnemyFaint;
    private List<BattleStep> stepList;

    private String afterHealthUpdateText; //When you lose health, usually there is text that comes associated with it - Recovered due to dry skin, Recovered due to rain dish etc.

    public EndTurnState(BattleStateManager battleStateManager) {
        this.battleStateManager = battleStateManager;
        this.field = battleStateManager.getField();
        this.firstAttacker = battleStateManager.getFirstAttacker();
        this.secondAttacker = battleStateManager.getSecondAttacker();
        this.afterHealthUpdateText = "";
        stepList = new ArrayList<BattleStep>();
        stepList.add(BattleStep.UPDATE_WEATHER);
        stepList.add(BattleStep.FIRST_POISON);
        stepList.add(BattleStep.SECOND_POISON);
        stepList.add(BattleStep.FIRST_BURN);
        stepList.add(BattleStep.SECOND_BURN);
        stepList.add(BattleStep.DONE_END_TURN);
        setFirstBattleStep();
    }


    private void setFirstBattleStep() {
        currentStep = getNextStep(null);
    }

    private BattleStep getNextStep(BattleStep finishedStep) {
        //UPDATE_WEATHER -> WEATHER_EFFECTS -> (FIRST_POISON -> FIRST_HURT_POISON) -> SECOND_POISON -> FIRST_BURN -> SECOND_BURN -> DONE_END_TURN
        int nextStepNumber = finishedStep != null ? stepList.indexOf(finishedStep) + 1 : 0;
        for (int i = nextStepNumber; i < stepList.size(); i++) {
            if (isBattleStepConditionSatisfied(stepList.get(i))) {
                return stepList.get(i);
            }
        }
        return null;
    }

    public boolean isBattleStepConditionSatisfied(BattleStep step) {
        if (step == BattleStep.UPDATE_WEATHER) {
            return field.getWeatherType() != WeatherType.NORMAL;
        } else if (step == BattleStep.DAMAGING_WEATHER) {
            return field.hasDamagingWeatherConditions();
        } else if (step == BattleStep.FIRST_BURN) {
            return firstAttacker.getPokemon().isBurned();
        } else if (step == BattleStep.SECOND_BURN) {
            return secondAttacker.getPokemon().isBurned();
        } else if (step == BattleStep.FIRST_POISON) {
            return firstAttacker.getPokemon().isPoisoned();
        } else if (step == BattleStep.SECOND_POISON) {
            return secondAttacker.getPokemon().isPoisoned();
        } else if (step == BattleStep.DONE_END_TURN) {
            return true;
        }
        return false;
    }

    public void update(float dt) {
        if (uiComponent != null) {
            uiComponent.update(dt);
        }
        if (finishedUpdatingUI()) {
            if (currentStep == BattleStep.UPDATE_WEATHER) {
                updateWeather();
            } else if (currentStep == BattleStep.FIRST_RAIN) {
                useRain(firstAttacker, true);
            } else if (currentStep == BattleStep.SECOND_RAIN) {
                useRain(secondAttacker, false);
            } else if (currentStep == BattleStep.FIRST_EFFECT_RAIN) {
                currentStep = BattleStep.SECOND_RAIN;
                uiComponent = new BattleTextBoxComponent(this, createMoveMessages(afterHealthUpdateText));
            } else if (currentStep == BattleStep.SECOND_EFFECT_RAIN) {
                currentStep = getNextStep(BattleStep.UPDATE_WEATHER);
                uiComponent = new BattleTextBoxComponent(this, createMoveMessages(afterHealthUpdateText));
            } else if (currentStep == BattleStep.FIRST_SUN) {
                useSunlight(firstAttacker, true);
            } else if (currentStep == BattleStep.SECOND_SUN) {
                if (firstAttacker.getPokemon().hasFainted()) {
                    initializeFainting(true, firstAttacker.isUser(), firstAttacker, null, false);
                } else {
                    useSunlight(secondAttacker, false);
                }
            } else if (currentStep == BattleStep.FIRST_EFFECT_SUN) {
                currentStep = BattleStep.SECOND_SUN;
                uiComponent = new BattleTextBoxComponent(this, createMoveMessages(afterHealthUpdateText));
            } else if (currentStep == BattleStep.SECOND_EFFECT_SUN) {
                if (secondAttacker.getPokemon().hasFainted()) {
                    initializeFainting(false, secondAttacker.isUser(), secondAttacker, BattleStep.UPDATE_WEATHER, true);
                } else {
                    currentStep = getNextStep(BattleStep.UPDATE_WEATHER);
                }
                uiComponent = new BattleTextBoxComponent(this, createMoveMessages(afterHealthUpdateText));
            } else if (currentStep == BattleStep.FIRST_SAND) {
                useSandstorm(firstAttacker, true);
            } else if (currentStep == BattleStep.SECOND_SAND) {
                if (firstAttacker.getPokemon().hasFainted()) {
                    initializeFainting(true, firstAttacker.isUser(), firstAttacker, null, false);
                } else {
                    useSandstorm(secondAttacker, false);
                }
            } else if (currentStep == BattleStep.FIRST_EFFECT_SAND) {
                currentStep = BattleStep.SECOND_SAND;
                uiComponent = new BattleTextBoxComponent(this, createMoveMessages(afterHealthUpdateText));
            } else if (currentStep == BattleStep.SECOND_EFFECT_SAND) {
                if (secondAttacker.getPokemon().hasFainted()) {
                    initializeFainting(false, secondAttacker.isUser(), secondAttacker, BattleStep.UPDATE_WEATHER, true);
                } else {
                    currentStep = getNextStep(BattleStep.UPDATE_WEATHER);
                }
                uiComponent = new BattleTextBoxComponent(this, createMoveMessages(afterHealthUpdateText));
            } else if (currentStep == BattleStep.FIRST_HAIL) {
                useHail(firstAttacker, true);
            } else if (currentStep == BattleStep.SECOND_HAIL) {
                if (firstAttacker.getPokemon().hasFainted()) {
                    initializeFainting(true, firstAttacker.isUser(), firstAttacker, null, false);
                } else {
                    useHail(secondAttacker, false);
                }
            } else if (currentStep == BattleStep.FIRST_EFFECT_HAIL) {
                currentStep = BattleStep.SECOND_HAIL;
                uiComponent = new BattleTextBoxComponent(this, createMoveMessages(afterHealthUpdateText));
            } else if (currentStep == BattleStep.SECOND_EFFECT_HAIL) {
                if (secondAttacker.getPokemon().hasFainted()) {
                    initializeFainting(false, secondAttacker.isUser(), secondAttacker, BattleStep.UPDATE_WEATHER, true);
                } else {
                    currentStep = getNextStep(BattleStep.UPDATE_WEATHER);
                }
                uiComponent = new BattleTextBoxComponent(this, createMoveMessages(afterHealthUpdateText));
            }  else if (currentStep == BattleStep.FIRST_BURN) {
                useBurn(firstAttacker, true);
            } else if (currentStep == BattleStep.SECOND_BURN) {
                useBurn(secondAttacker, false);
            } else if (currentStep == BattleStep.FIRST_POISON) {
                usePoison(firstAttacker, true);
            } else if (currentStep == BattleStep.SECOND_POISON) {
                usePoison(secondAttacker, false);
            } else if (currentStep == BattleStep.FIRST_HURT_POISON) {
                if (firstAttacker.getPokemon().hasFainted()) {
                    initializeFainting(true, firstAttacker.isUser(), firstAttacker, null, true);
                } else {
                    currentStep = getNextStep(BattleStep.FIRST_POISON);
                }
                uiComponent = new BattleTextBoxComponent(this, createMoveMessages(firstAttacker.getPokemon().getName() + " was hurt by poison."));
            } else if (currentStep == BattleStep.SECOND_HURT_POISON) {
                if (secondAttacker.getPokemon().hasFainted()) {
                    initializeFainting(false, secondAttacker.isUser(), secondAttacker, BattleStep.SECOND_POISON, true);
                } else {
                    currentStep = getNextStep(BattleStep.SECOND_POISON);
                }
                uiComponent = new BattleTextBoxComponent(this, createMoveMessages(secondAttacker.getPokemon().getName() + " was hurt by poison."));
            }  else if (currentStep == BattleStep.FIRST_HURT_BURN) {
                if (firstAttacker.getPokemon().hasFainted()) {
                    initializeFainting(true, firstAttacker.isUser(), firstAttacker, null, true);
                } else {
                    currentStep = getNextStep(BattleStep.FIRST_BURN);
                }
                uiComponent = new BattleTextBoxComponent(this, createMoveMessages(firstAttacker.getPokemon().getName() + " was hurt by burn."));
            } else if (currentStep == BattleStep.SECOND_HURT_BURN) {
                if (secondAttacker.getPokemon().hasFainted()) {
                    initializeFainting(false, secondAttacker.isUser(), secondAttacker, BattleStep.SECOND_BURN, true);
                } else {
                    currentStep = getNextStep(BattleStep.SECOND_BURN);
                }
                uiComponent = new BattleTextBoxComponent(this, createMoveMessages(secondAttacker.getPokemon().getName() + " was hurt by burn."));
            } else if (currentStep == BattleStep.DONE_END_TURN) {
                finishEndTurnState();
            } else if (currentStep == BattleStep.GET_NEXT_POKEMON) {
                battleStateManager.switchOutFaintedPokemon();
                    battleStateManager.setState(null);
                    battleStateManager.setReturningToFightOptions(true);
            } else if (currentStep == BattleStep.ENEMY_FAINTED) {
                battleLog("ENEMY POKEMON FAINTED");
                BattleTextBoxComponent btc = new BattleTextBoxComponent(this, createMoveMessages(faintingPokemon.getPokemon().getName() + " fainted!"));
                btc.setFinishType(BattleTextBox.BattleTextBoxFinish.TRIGGER);
                uiComponent = btc;
                currentStep = BattleStep.ENEMY_FAINT_TEXT;
            } else if (currentStep == BattleStep.ENEMY_FAINT_TEXT) {
                battleLog("ENEMY FAINT TEXT");
                BattlePokemon expReceiver = firstAttacker.isUser() ? firstAttacker.getPokemon() : secondAttacker.getPokemon();
                if (!faintingPokemon.isUser()) {
                    battleStateManager.setState(new ExpState(battleStateManager, expReceiver, faintingPokemon.getPokemon(), false));
                }
            } else if (currentStep == BattleStep.PLAYER_FAINT_TEXT) {
                battleLog("PLAYER FAINT TEXT");
                if (battleStateManager.isPlayerPartyWiped()) {
                    //Display Blacked Out State
                } else {
                    //TODO: Display Switch Pokemon? Check what happens when a party member faints
                }
            } else if (currentStep == BattleStep.TEXT_BEFORE_ENEMY_FAINT) {
                currentStep = BattleStep.ENEMY_FAINTED;
                uiComponent = new FaintAnimation(this, faintingPokemon.getPokemon(), false);
            }
        }
    }

    private void startEnemyPokemonFaint(BattleStatePokemon enemyPokemon) {
        currentStep = BattleStep.ENEMY_FAINTED;
        faintingPokemon = enemyPokemon;
        uiComponent = new FaintAnimation(this, enemyPokemon.getPokemon(), false);
    }

    private void initializeFainting(boolean isFaintingPokemonFirstAttack, boolean isUserPokemon, BattleStatePokemon faintedPokemon, BattleStep curBattleStep, boolean isWaitingForUIComponent) {
        battleLog(isUserPokemon + " First Attacker fainted..." + " " + faintedPokemon.getPokemon().getName());
        faintingPokemon = faintedPokemon;
        if (!isUserPokemon) {
            if (!isWaitingForUIComponent) {
                currentStep = BattleStep.ENEMY_FAINTED;
                uiComponent = new FaintAnimation(this, faintedPokemon.getPokemon(), false);
            } else {
                currentStep = BattleStep.TEXT_BEFORE_ENEMY_FAINT;
            }
            if (curBattleStep != null) {
                stepAfterEnemyFaint =  getNextStep(curBattleStep);
            }
        } else {
            //fainting
        }
    }

    private void finishEndTurnState() {
        battleLog("DONE END TURN STATE");
        if (battleStateManager.isUserPokemonFainted()
                && battleStateManager.isWildBattle() && !battleStateManager.isPlayerPartyWiped()) {
            currentStep = BattleStep.GET_NEXT_POKEMON;
        } else {
            battleStateManager.setState(null);
            battleStateManager.setReturningToFightOptions(true);
        }
    }




    private void useBurn(BattleStatePokemon battleStatePokemon, boolean isFirstAttacker) {
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        int damage = (int)Math.round(pokemon.getPokemon().getHealthStat() / 16.0);
        pokemon.subtractHealth(damage);
        battleLog(pokemon.getName() + " WAS HURT BY BURN - TOOK " + damage + " DAMAGE");
        currentStep = isFirstAttacker ? BattleStep.FIRST_HURT_BURN : BattleStep.SECOND_HURT_BURN;
        uiComponent = new HealthUpdater(this, pokemon);
    }

    private void usePoison(BattleStatePokemon battleStatePokemon, boolean isFirstAttacker) {
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        int damage = (int)Math.round(pokemon.getPokemon().getHealthStat() / 8.0);
        pokemon.subtractHealth(damage);
        battleLog(pokemon.getName() + " WAS HURT BY POISON");
        currentStep = isFirstAttacker ? BattleStep.FIRST_HURT_POISON : BattleStep.SECOND_HURT_POISON;
        uiComponent = new HealthUpdater(this, pokemon);
    }

    private void updateWeather() {
        battleLog("CHECK WEATHER");
        String weatherResult;
        if (field.onLastWeatherTurn()) {
            weatherResult = getWeatherResultText(true);
            field.clearWeather();
        } else {
            weatherResult = getWeatherResultText(false);
            field.adjustWeather();
        }
        if (field.getWeatherType() == WeatherType.RAIN) {
            currentStep = BattleStep.FIRST_RAIN;
        } else if (field.getWeatherType() == WeatherType.SUN) {
            currentStep = BattleStep.FIRST_SUN;
        } else if (field.getWeatherType() == WeatherType.SAND) {
            currentStep = BattleStep.FIRST_SAND;
        } else if (field.getWeatherType() == WeatherType.HAIL) {
            currentStep = BattleStep.FIRST_HAIL;
        } else {
            currentStep = getNextStep(BattleStep.UPDATE_WEATHER);
        }
        uiComponent = new BattleTextBoxComponent(this, weatherResult);
        battleLog("END WEATHER CHECK");
    }

    private void battleLog(String str) {
        Gdx.app.log("EndTurnState", str);
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

    private void useSandstorm(BattleStatePokemon battleStatePokemon, boolean isFirstAttacker) {
        AbilityId ability = battleStatePokemon.getPokemon().getAbility();
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        PokemonType[] sandstormImmuneTypes = {PokemonType.ROCK, PokemonType.GROUND, PokemonType.STEEL};
        AbilityId[] sandstormImmuneAbilities = {AbilityId.SAND_FORCE, AbilityId.SAND_RUSH, AbilityId.SAND_VEIL, AbilityId.MAGIC_GUARD, AbilityId.OVERCOAT};
        boolean hasSandstormImmuneType = Arrays.stream(sandstormImmuneTypes).anyMatch(n -> n == pokemon.getFirstType() || n == pokemon.getSecondType());
        boolean hasSandstormImmuneAbility = Arrays.stream(sandstormImmuneAbilities).anyMatch(n -> n == ability);
        if (hasSandstormImmuneAbility || hasSandstormImmuneType) { //TODO: Check for holding safety goggles
            battleLog("SANDSTORM DOES NOTHING TO " + pokemon.getName() + " DUE TO THEIR ABILITY OR TYPE");
            currentStep = isFirstAttacker ? BattleStep.SECOND_SAND : getNextStep(BattleStep.UPDATE_WEATHER);
        } else {
            damagePokemonFromSandstorm(pokemon);
            currentStep = isFirstAttacker ? BattleStep.FIRST_EFFECT_SAND : BattleStep.SECOND_EFFECT_SAND;
            uiComponent = new HealthUpdater(this, pokemon);
            afterHealthUpdateText = pokemon.getName() + " is buffeted from the sandstorm!";
        }
    }

    private void useSunlight(BattleStatePokemon battleStatePokemon, boolean isFirstAttacker) {
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        AbilityId ability = pokemon.getAbility();
        AbilityId[] sunDamagingAbilities = {AbilityId.DRY_SKIN, AbilityId.SOLAR_POWER};
        AbilityId[] sunRecoveringAbilities = {};
        boolean hasSunlightDamagingAbility = Arrays.stream(sunDamagingAbilities).anyMatch(n -> n == ability);
        boolean hasSunlightRecoveringAbility = Arrays.stream(sunRecoveringAbilities).anyMatch(n -> n == ability);
        if (hasSunlightDamagingAbility) {
            damagePokemonFromSunlight(pokemon);
            currentStep = isFirstAttacker ? BattleStep.FIRST_EFFECT_SUN : BattleStep.SECOND_EFFECT_SUN;
            uiComponent = new HealthUpdater(this, pokemon);
            if (ability == AbilityId.DRY_SKIN) {
                afterHealthUpdateText = pokemon.getName() + " took damage due to\nDry Skin in Sunlight";
            } else {
                afterHealthUpdateText = pokemon.getName() + " took damage due to\nSolar Power in Sunlight";
            }
        } else {
            battleLog("SUNLIGHT DOES NOTHING TO " + pokemon.getName());
            currentStep = isFirstAttacker ? BattleStep.SECOND_SUN : getNextStep(BattleStep.UPDATE_WEATHER);
        }
    }

    private void useRain(BattleStatePokemon battleStatePokemon, boolean isFirstAttacker) {
        AbilityId ability = battleStatePokemon.getPokemon().getAbility();
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        AbilityId[] smallRainRecoveringAbilities = {AbilityId.RAIN_DISH};
        AbilityId[] largeRainRecoveringAbilities = {AbilityId.DRY_SKIN};
        boolean hasSmallRainRecoveringAbility = Arrays.stream(smallRainRecoveringAbilities).anyMatch(n -> n == ability);
        boolean hasLargeRainRecoveringAbility = Arrays.stream(largeRainRecoveringAbilities).anyMatch(n -> n == ability);
        if (hasSmallRainRecoveringAbility
                && !pokemon.hasFullHealth()) {
            recoverPokemonFromRain(pokemon, 16.0);
            currentStep = isFirstAttacker ? BattleStep.FIRST_EFFECT_RAIN : BattleStep.SECOND_EFFECT_RAIN;
            uiComponent = new HealthUpdater(this, pokemon);
            afterHealthUpdateText = pokemon.getName() + " recovered health from Rain Dish.";
        } else if (hasLargeRainRecoveringAbility
                && !pokemon.hasFullHealth()) {
            recoverPokemonFromRain(pokemon, 8.0);
            currentStep = isFirstAttacker ? BattleStep.FIRST_EFFECT_RAIN : BattleStep.SECOND_EFFECT_RAIN;
            uiComponent = new HealthUpdater(this, pokemon);
            afterHealthUpdateText = pokemon.getName() + " recovered health from Dry Skin.";
        } else {
            battleLog("RAIN DOES NOTHING TO " + pokemon.getName());
            currentStep = isFirstAttacker ? BattleStep.SECOND_RAIN : getNextStep(BattleStep.UPDATE_WEATHER);
        }
    }

    private void useHail(BattleStatePokemon battleStatePokemon, boolean isFirstAttacker) {
        AbilityId ability = battleStatePokemon.getPokemon().getAbility();
        BattlePokemon pokemon = battleStatePokemon.getPokemon();
        AbilityId[] hailImmuneAbilities = {AbilityId.ICE_BODY, AbilityId.SNOW_CLOAK, AbilityId.MAGIC_GUARD, AbilityId.OVERCOAT};
        AbilityId[] hailRecoveringAbilities = {AbilityId.ICE_BODY};
        boolean hasHailRecoveringAbility = Arrays.stream(hailRecoveringAbilities).anyMatch(n -> n == ability);
        boolean hasHailImmuneAbility = Arrays.stream(hailImmuneAbilities).anyMatch(n -> n == ability);
        if (hasHailImmuneAbility ||
                pokemon.getFirstType() == PokemonType.ICE ||
                pokemon.getSecondType() == PokemonType.ICE) { //TODO: Check for holding safety goggles
            if (hasHailRecoveringAbility && !pokemon.hasFullHealth()) {
                recoverPokemonFromHail(pokemon);
                currentStep = isFirstAttacker ? BattleStep.FIRST_EFFECT_HAIL : BattleStep.SECOND_EFFECT_HAIL;
                uiComponent = new HealthUpdater(this, pokemon);
                afterHealthUpdateText = pokemon.getName() + " recovered health from Ice Body.";
            } else {
                battleLog("HAIL DOES NOTHING TO " + pokemon.getName() + " DUE TO THEIR ABILITY OR TYPE");
                currentStep = isFirstAttacker ? BattleStep.SECOND_HAIL : getNextStep(BattleStep.UPDATE_WEATHER);
            }
        } else {
            damagePokemonFromHail(pokemon);
            currentStep = isFirstAttacker ? BattleStep.FIRST_EFFECT_HAIL : BattleStep.SECOND_EFFECT_HAIL;
            uiComponent = new HealthUpdater(this, pokemon);
            afterHealthUpdateText = pokemon.getName() + " is buffeted by the hail.";
        }
    }

    private void recoverPokemonFromRain(BattlePokemon pokemon, double denominator) {
        int recoverAmount = (int)Math.round(pokemon.getPokemon().getHealthStat() / denominator);
        pokemon.addHealth(recoverAmount);
        battleLog(pokemon.getName() + " RECOVERED HEALTH IN THE RAIN WITH THE ABILITY: " + pokemon.getAbility().toString());
    }

    private void recoverPokemonFromHail(BattlePokemon pokemon) {
        int recoverAmount = (int)Math.round(pokemon.getPokemon().getHealthStat() / 16.0);
        pokemon.addHealth(recoverAmount);
        battleLog(pokemon.getName() + " RECOVERED HEALTH FROM HAIL WITH THE ABILITY: ICE BODY");
    }
    private void damagePokemonFromHail(BattlePokemon pokemon) {
        int damage = (int)Math.round(pokemon.getPokemon().getHealthStat() / 16.0);
        pokemon.subtractHealth(damage);
        battleLog(pokemon.getName() + " IS BUFFETED BY HAIL");
    }

    private void damagePokemonFromSunlight(BattlePokemon pokemon) {
        int damage = (int)Math.round(pokemon.getPokemon().getHealthStat() / 8.0);
        pokemon.subtractHealth(damage);
        if (pokemon.getAbility() == AbilityId.SOLAR_POWER) {
            battleLog(pokemon.getName() + " IS HURT FROM DRAWING IN SOLAR POWER");
        } else { //Having Dry skin is the only other way to get here.
            battleLog(pokemon.getName() + " IS HURT BY HAVING DRY SKIN IN THE SUNLIGHT");
        }
    }

    private void damagePokemonFromSandstorm(BattlePokemon pokemon) {
        int damage = (int)Math.round(pokemon.getPokemon().getHealthStat() / 16.0);
        pokemon.subtractHealth(damage);
        battleLog(pokemon.getName() + " IS BUFFETED BY THE SANDSTORM!");
    }

    private void endState() {
        BattleStatePokemon firstPokemon = battleStateManager.getFirstAttacker();
        BattleStatePokemon secondPokemon = battleStateManager.getSecondAttacker();
        if (hasFainted(firstPokemon)) {
            if (secondPokemon.isUser()) {
                battleStateManager.setState(new ExpState(battleStateManager, secondPokemon.getPokemon(), firstPokemon.getPokemon(), false));
            } else {
                checkPartyWipe();
            }
        } else if (hasFainted(secondPokemon)) {
            if (firstPokemon.isUser()) {
                battleStateManager.setState(new ExpState(battleStateManager, firstPokemon.getPokemon(), secondPokemon.getPokemon(), false));
            } else {
                checkPartyWipe();
            }
        } else {
            battleStateManager.setState(new SleepState(battleStateManager, false));
        }

    }

    private void checkPartyWipe() {
        if (!battleStateManager.isPlayerPartyWiped()) {
            //TODO: Set the state to the sendoutstate and then end the battle phase to select move
        } else {
            //TODO: BLACKED OUT
        }
    }

    private boolean hasFainted(BattleStatePokemon pokemon) {
        return pokemon.getPokemon().hasFainted();
    }

}
