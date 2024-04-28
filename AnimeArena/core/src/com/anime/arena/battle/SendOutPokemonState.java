package com.anime.arena.battle;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.field.WeatherType;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;
import com.anime.arena.pokemon.StatusCondition;
import com.anime.arena.skill.Skill;
import com.badlogic.gdx.Gdx;

public class SendOutPokemonState extends BattleState {

    private BattlePokemon userPokemon;
    private BattlePokemon enemyPokemon;
    private Field field;

    public enum SendOutPokemonReason {STARTED_BATTLE, PLAYER_SWITCH_IN, PLAYER_FAINT, OPPONENT_FAINT}

    private SubField abilityUserField;
    private boolean isAbilityPokemonUser;
    private boolean isExecutingFirstAbility;

    private SendOutPokemonReason sendOutReason;

    public SendOutPokemonState(BattleStateManager battleStateManager, BattlePokemon userPokemon, BattlePokemon enemyPokemon, Field field) {
        this.battleStateManager = battleStateManager;
        this.userPokemon = userPokemon;
        this.enemyPokemon = enemyPokemon;
        this.field = field;
        this.isExecutingFirstAbility = true;
        sendOutReason = SendOutPokemonReason.STARTED_BATTLE;
    }

    public void setSwitchSendOutReason(SendOutPokemonReason reason) {
        sendOutReason = reason;
    }

    private BattlePokemon getAbilityUserPokemon() {
        if (isAbilityPokemonUser) {
            return userPokemon;
        }
        return enemyPokemon;
    }
    
    private BattlePokemon getOtherPokemon() {
        if (isAbilityPokemonUser) {
            return enemyPokemon;
        }
        return userPokemon;       
    }

    private SubField getAbilityUserField() {
        if (isAbilityPokemonUser) {
            return field.getPlayerField();
        }
        return field.getOpponentField();
    }
    
    private SubField getOtherPokemonField() {
        if (isAbilityPokemonUser) {
            return field.getOpponentField();
        } else {
            return field.getPlayerField();
        }
    }

    private double getSpeed(BattlePokemon currentPokemon) {
        //Speed Clash
        double userSpeed = currentPokemon.getPokemon().getSpeedStat();
        int stage = currentPokemon.getSpeedStage();
        if (stage > 0) {
            userSpeed *= ((2 + stage) / 2);
        } else if (stage < 0) {
            userSpeed *= (2 / (Math.abs(stage) + 2));
        }
        if (currentPokemon.isParalyzed()) {
            userSpeed *= 0.5;
        }
        if (currentPokemon.getAbility() == AbilityId.SAND_RUSH &&
                field.getWeatherType() == WeatherType.SAND) {
            userSpeed *= 2;
        } else if (currentPokemon.getAbility() == AbilityId.SWIFT_SWIM &&
                (field.getWeatherType() == WeatherType.RAIN ||
                        field.getWeatherType() == WeatherType.HEAVY_RAIN)) {
            userSpeed *= 2;
        } else if (currentPokemon.getAbility() == AbilityId.CHLOROPHYLL &&
                (field.getWeatherType() == WeatherType.SUN ||
                        field.getWeatherType() == WeatherType.HARSH_SUNSHINE)) {
            userSpeed *= 2;
        }
        return userSpeed;
    }

    private void initFirstAbilityUser() {
        if (sendOutReason == SendOutPokemonReason.STARTED_BATTLE) {
            double userSpeed = getSpeed(userPokemon);
            double enemySpeed = getSpeed(enemyPokemon);
            if (userSpeed >= enemySpeed) {
                isAbilityPokemonUser = true;
            } else {
                isAbilityPokemonUser = false;
            }
        } else if (sendOutReason == SendOutPokemonReason.PLAYER_FAINT || sendOutReason == SendOutPokemonReason.PLAYER_SWITCH_IN) {
            isAbilityPokemonUser = true;
        } else {
            isAbilityPokemonUser = false;
        }
    }
    
    private void battleLog(String str) {
        Gdx.app.log("SendOutPokemonState", str);
    }

    private void init() {
        BattlePokemon abilityUser = isExecutingFirstAbility ? getAbilityUserPokemon() : getOtherPokemon();
        SubField abilityUserField =  isExecutingFirstAbility ? getAbilityUserField() : getOtherPokemonField();
        BattlePokemon otherPokemon =  isExecutingFirstAbility ? getOtherPokemon() : getAbilityUserPokemon();
        SubField receiverField =  isExecutingFirstAbility ? getOtherPokemonField() : getAbilityUserField();
        battleLog("CHECKING SEND OUT ABILITIES FOR " + abilityUser.getName());
        if (abilityUser.getAbility() == AbilityId.INTIMIDATE) {
            if (receiverField.hasMist()) {
                battleLog(abilityUser.getName() + " USED INTIMIDATE BUT MIST PREVENTS THE ATTACK FROM DECREASING!");
            } else if (otherPokemon.getAbility() == AbilityId.CLEAR_BODY) {
                battleLog(otherPokemon.getName() + " PREVENTS " + abilityUser.getName() + "'s INTIMIDATE WITH THE ABILITY CLEAR BODY");
            } else if (otherPokemon.getAttackStage() > -6) {
                otherPokemon.decreaseAttackStage(1);
                battleLog(abilityUser.getName() + " USED INTIMIDATE AND " + otherPokemon.getName() + "'s ATTACK FELL!");

            } else {
                battleLog(abilityUser.getName() + " USED INTIMIDATE BUT " + otherPokemon.getName() + "'s ATTACK CANNOT BE LOWERED!");
            }

        } else if (abilityUser.getAbility() == AbilityId.DRIZZLE) {
            if (field.getWeatherType() == WeatherType.HEAVY_RAIN ||
                    field.getWeatherType() == WeatherType.HARSH_SUNSHINE ||
                    field.getWeatherType() == WeatherType.RAIN) {
                battleLog(abilityUser.getName() + "'s DRIZZLE HAS NO EFFECT...");
            } else if (otherPokemon.getAbility() == AbilityId.CLOUD_NINE) {
                battleLog(otherPokemon.getName() + " PREVENTS " + abilityUser.getName() + "'s DRIZZLE WITH THE ABILITY CLOUD NINE");
            } else {
                field.setWeather(WeatherType.RAIN, 5);
                battleLog(abilityUser.getName() + "'s DRIZZLE ABILITY STARTED RAIN");
            }
        } else if (abilityUser.getAbility() == AbilityId.DROUGHT) {
            if (field.getWeatherType() == WeatherType.HEAVY_RAIN ||
                    field.getWeatherType() == WeatherType.HARSH_SUNSHINE ||
                    field.getWeatherType() == WeatherType.SUN) {
                battleLog(abilityUser.getName() + "'s DROUGHT HAS NO EFFECT...");
            } else if (otherPokemon.getAbility() == AbilityId.CLOUD_NINE) {
                battleLog(otherPokemon.getName() + " PREVENTS " + abilityUser.getName() + "'s DROUGHT WITH THE ABILITY CLOUD NINE");
            } else {
                field.setWeather(WeatherType.SUN, 5);
                battleLog(abilityUser.getName() + "'s DROUGHT ABILITY CAUSED THE SUNLIGHT TO TURN HARSH");
            }
        } else if (abilityUser.getAbility() == AbilityId.SAND_STREAM) {
            if (field.getWeatherType() == WeatherType.HEAVY_RAIN ||
                    field.getWeatherType() == WeatherType.HARSH_SUNSHINE ||
                    field.getWeatherType() == WeatherType.SAND) {
                battleLog(abilityUser.getName() + "'s Sand Stream! It had no effect...");
            } else if (otherPokemon.getAbility() == AbilityId.CLOUD_NINE) {
                battleLog(otherPokemon.getName() + " prevents " + abilityUser.getName() + "'s Sand Stream with the ability Cloud Nine!");
            } else {
                field.setWeather(WeatherType.SAND, 5);
                battleLog(abilityUser.getName() + "'s Sand Stream! A sandstorm kicked up!");
            }
        } else if (abilityUser.getAbility() == AbilityId.SNOW_WARNING) {
            if (field.getWeatherType() == WeatherType.HEAVY_RAIN ||
                    field.getWeatherType() == WeatherType.HARSH_SUNSHINE ||
                    field.getWeatherType() == WeatherType.HAIL) {
                battleLog(abilityUser.getName() + "'s Snow Warning! It had no effect...");
            } else if (otherPokemon.getAbility() == AbilityId.CLOUD_NINE) {
                battleLog(otherPokemon.getName() + " prevents " + abilityUser.getName() + "'s Snow Warning with the ability Cloud Nine!");
            } else {
                field.setWeather(WeatherType.HAIL, 5);
                battleLog(abilityUser.getName() + "'s Snow Warning! It started to hail!");
            }
        } else if (abilityUser.getAbility() == AbilityId.PRESSURE) {
            battleLog(abilityUser.getName() + " is exerting its Pressure!");
        } else if (abilityUser.getAbility() == AbilityId.UNNERVE) {
            battleLog(abilityUser.getName() + "'s Unnerve! Enemies cannot eat berries in their presence!");
        } else if (abilityUser.getAbility() == AbilityId.MOLD_BREAKER) {
            battleLog(abilityUser.getName() + " breaks the mold!");
        } else if (abilityUser.getAbility() == AbilityId.CLOUD_NINE) {
            if (field.getWeatherType() != WeatherType.NORMAL) {
                battleLog(abilityUser.getName() + "'s Cloud Nine! The effects of weather disappeared.");
                field.clearWeather();
            } else {
                battleLog(abilityUser.getName() + "'s Cloud Nine! The weather is already NORMAL.");
            }
        }
        checkEntryHazards(abilityUser, abilityUserField);
    }

    private void checkEntryHazards(BattlePokemon abilityUser, SubField subField) {
        checkStealthRocks(abilityUser, subField);
        checkStickyWeb(abilityUser, subField);
        checkSpikes(abilityUser, subField);
        checkToxicSpikes(abilityUser, subField);
    }

    private void checkStealthRocks(BattlePokemon abilityUser, SubField subField) {
        battleLog("CHECKING STEALTH ROCKS ON " + abilityUser.getName() + "'S SUBFIELD");
        if (subField.hasStealthRocks()) {
            if (abilityUser.getAbility() == AbilityId.MAGIC_GUARD) {
                battleLog(abilityUser.getName() + " is immune to Stealth Rocks due to the ability Magic Guard.");
            } else {
                //Take Damage.
                int damage;
                if (abilityUser.getResistances().get(PokemonType.ROCK) == 0.25) {
                    damage = (int)Math.ceil(abilityUser.getPokemon().getHealthStat() * .03125);
                } else if (abilityUser.getResistances().get(PokemonType.ROCK) == 0.5) {
                    damage = (int)Math.ceil(abilityUser.getPokemon().getHealthStat() * .0625);
                } else if (abilityUser.getResistances().get(PokemonType.ROCK) == 1) {
                    damage = (int)Math.ceil(abilityUser.getPokemon().getHealthStat() * .125);
                } else if (abilityUser.getResistances().get(PokemonType.ROCK) == 2) {
                    damage = (int)Math.ceil(abilityUser.getPokemon().getHealthStat() * .25);
                } else {
                    damage = (int)Math.ceil(abilityUser.getPokemon().getHealthStat() * .5);
                }
                abilityUser.subtractHealth(damage);
                battleLog(abilityUser.getName() + " took " + damage + " damage from Stealth Rocks.");
            }
        }
    }

    private void checkStickyWeb(BattlePokemon abilityUser, SubField subField) {
        battleLog("CHECKING STICKY WEB ON " + abilityUser.getName() + "'S SUBFIELD");
        if (subField.hasStickyWeb()) {
            if (!immuneToStickyWeb(abilityUser)) {
                if (abilityUser.getSpeedStage() > -6) {
                    abilityUser.decreaseSpeedStage(1);
                    battleLog(abilityUser.getName() + " was caught in Sticky Web. " + abilityUser.getName() + "'s Speed fell!");
                }
            }
        }
    }

    private void checkToxicSpikes(BattlePokemon abilityUser, SubField subField) {
        battleLog("CHECKING TOXIC SPIKES ON " + abilityUser.getName() + "'S SUBFIELD");
        if (subField.hasToxicSpikes()) {
            if (absorbsToxicSpikes(abilityUser)) {
                battleLog(abilityUser.getName() + " used its poison to absorb the Toxic Spikes!");
                subField.removeToxicSpikes();
            } else if (immuneToToxicSpikes(abilityUser)) {
                battleLog(abilityUser.getName() + " is immune to the Toxic Spikes!");
            } else {
                //TODO: Implement badly poison
                abilityUser.setPreStatus(StatusCondition.POISON);
                battleLog(abilityUser.getName() + " was poisoned by the Toxic Spikes!");
            }
        }
    }

    private void checkSpikes(BattlePokemon abilityUser, SubField subField) {
        battleLog("CHECKING SPIKES ON " + abilityUser.getName() + "'S SUBFIELD");
        int layers = subField.getNumberOfSpikeLayers();
        boolean hasLayers = true;
        int damage = 0;
        if (layers == 1) {
            damage = (int)Math.ceil(abilityUser.getPokemon().getHealthStat() / 8.0);
        } else if (layers == 2) {
            damage = (int)Math.ceil(abilityUser.getPokemon().getHealthStat() / 6.0);
        } else if (layers == 3) {
            damage = (int)Math.ceil(abilityUser.getPokemon().getHealthStat() / 4.0);
        } else {
            hasLayers = false;
        }
        if (hasLayers) {
            if (!immuneToSpikes(abilityUser)) {
                abilityUser.subtractHealth(damage);
                battleLog(abilityUser.getName() + " took damage from spikes.");
            } else {
                battleLog(abilityUser.getName() + " is immune to spikes.");
            }

        }
    }

    private boolean immuneToStickyWeb(BattlePokemon abilityUser) {
        if (abilityUser.getFirstType() == PokemonType.FLYING ||
                abilityUser.getSecondType() == PokemonType.FLYING ||
                abilityUser.getAbility() == AbilityId.LEVITATE) {
            return true;
        }
        return false;
    }

    private boolean immuneToSpikes(BattlePokemon abilityUser) {
        if (abilityUser.getAbility() == AbilityId.MAGIC_GUARD) {
            //IMMUNE
            return true;
        } else if (abilityUser.getAbility() == AbilityId.LEVITATE ||
                abilityUser.getFirstType() == PokemonType.FLYING ||
                abilityUser.getSecondType() == PokemonType.FLYING ||
                abilityUser.isMagnetRisen()) {
            if (abilityUser.isIngrained() || field.hasGravity()) {
                //TAKES DAMAGE ANYWAYS
                return false;
            } else {
                //IMMUNE
                return true;
            }
        }
        return false;
    }

    private boolean immuneToPoison(BattlePokemon abilityUser) {
        if (abilityUser.getFirstType() == PokemonType.POISON ||
                abilityUser.getSecondType() == PokemonType.POISON ||
                abilityUser.getFirstType() == PokemonType.STEEL ||
                abilityUser.getSecondType() == PokemonType.STEEL) {
            return true;
        } else if (abilityUser.getAbility() == AbilityId.IMMUNITY ||
                (abilityUser.getAbility() == AbilityId.LEAF_GUARD &&
                        field.getWeatherType() == WeatherType.SUN)) {
            return true;
        }
        return false;
    }

    private boolean immuneToToxicSpikes(BattlePokemon abilityUser) {
        if (abilityUser.isStatused()) {
            return true;
        }
        else if (immuneToSpikes(abilityUser)) {
            return true;
        } else if (immuneToPoison(abilityUser)) {
            return true;
        }
        return false;
    }

    private boolean absorbsToxicSpikes(BattlePokemon abilityUser) {
        if ((abilityUser.getFirstType() == PokemonType.POISON ||
                abilityUser.getSecondType() == PokemonType.POISON) &&
                (abilityUser.getFirstType() != PokemonType.FLYING ||
                        abilityUser.getSecondType() != PokemonType.FLYING ||
                        abilityUser.getAbility() != AbilityId.LEVITATE)) {
            return true;
        }
        return false;
    }


    public void update(float dt) {
        initFirstAbilityUser();
        init();
        if (sendOutReason == SendOutPokemonReason.STARTED_BATTLE) {
            isExecutingFirstAbility = false;
            init();
            battleStateManager.setState(null);
        } else if (sendOutReason == SendOutPokemonReason.PLAYER_SWITCH_IN) {
            battleStateManager.setAttackerAfterSwitch(userPokemon, enemyPokemon, enemyPokemon.getFirstMove());
            battleStateManager.setState(new SleepState(battleStateManager, false));
        } else {
            battleStateManager.setState(null);
        }
    }
}
