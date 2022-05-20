package com.anime.arena.skill;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.AbilityId;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;
import com.anime.arena.pokemon.StatusCondition;
import com.badlogic.gdx.Gdx;

import java.util.List;

public class StatusEffect extends Effect {
    private StatusCondition statusEffect;
    public StatusEffect(int id, MoveEffectSubtype subtype, SkillTarget target, StatusCondition statusEffect) {
        super(id, subtype, target);
        this.statusEffect = statusEffect;
    }

    /**
     * Use the burn effect.
     * @param results The results String list after using the effect.
     * @param skillUser The Pokemon using the skill that causes this effect.
     * @param enemyPokemon The Pokemon not using the skill.
     * @param field
     * @param userField
     * @param enemyField
     * @param isFirstAttack Whether or not the effect is a result of the first attack.
     */
    @Override
    public void use(List<String> results, BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field,
                    SubField userField, SubField enemyField, boolean isFirstAttack) {
        if (!enemyPokemon.isStatused()) {
            if (statusEffect == StatusCondition.BURN) {
                useBurnEffect(skillUser, enemyPokemon);
            } else if (statusEffect == StatusCondition.POISON) {
                usePoisonEffect(skillUser, enemyPokemon);
            } else if (statusEffect == StatusCondition.POISON) {
                useParalyzeEffect(skillUser, enemyPokemon);
            } else if (statusEffect == StatusCondition.POISON) {
                useFreezeEffect(skillUser, enemyPokemon);
            } else if (statusEffect == StatusCondition.POISON) {
                //useSleepEffect(enemyPokemon);
            }
        }
    }

    private void useBurnEffect(BattlePokemon user, BattlePokemon enemy) {

        if (enemy.getFirstType() != PokemonType.FIRE && enemy.getSecondType() != PokemonType.FIRE) {
            enemy.inflictBurn();
            Gdx.app.log("StatusEffect", enemy.getPokemon().getConstantVariables().getName() + " was burned!");
        } else {
            Gdx.app.log("StatusEffect", "The burn effect failed because the enemy pokemon is a FIRE type");
        }
    }

    private void usePoisonEffect(BattlePokemon user, BattlePokemon pokemon) {
        if (user.getAbility() != AbilityId.CORROSION) {
            if (pokemon.getFirstType() == PokemonType.POISON || pokemon.getSecondType() == PokemonType.POISON) {
                Gdx.app.log("StatusEffect", "The poison failed because the enemy pokemon is a POISON type");
            } else if (pokemon.getFirstType() == PokemonType.STEEL || pokemon.getSecondType() == PokemonType.STEEL) {
                Gdx.app.log("StatusEffect", "The poison effect failed because the enemy pokemon is a STEEL type");
            } else {
                pokemon.inflictPoison();
            }
        } else {
            pokemon.inflictPoison();
        }
    }

    private void useParalyzeEffect(BattlePokemon user,BattlePokemon pokemon) {
        if (pokemon.getFirstType() == PokemonType.ELECTRIC || pokemon.getSecondType() == PokemonType.ELECTRIC) {
            Gdx.app.log("StatusEffect", "The paralyze failed because the enemy pokemon is an ELECTRIC type");
        } else if (pokemon.getFirstType() == PokemonType.GROUND || pokemon.getSecondType() == PokemonType.GROUND) {
            if (moveType == PokemonType.ELECTRIC) {
                Gdx.app.log("StatusEffect", "The paralyze effect failed because the enemy pokemon is a GROUND type and the move is an ELECTRIC type");
            } else {
                pokemon.inflictParalysis();
            }
        } else {
            pokemon.inflictParalysis();
        }
    }

    private void useFreezeEffect(BattlePokemon user, BattlePokemon pokemon) {
        if (pokemon.getFirstType() != PokemonType.ICE && pokemon.getSecondType() != PokemonType.ICE) {
            pokemon.inflictFreeze();
            Gdx.app.log("StatusEffect", pokemon.getPokemon().getConstantVariables().getName() + " was frozen!");
        } else {
            Gdx.app.log("StatusEffect", "The freeze effect failed because the enemy pokemon is a ICE type");
        }
    }
}
