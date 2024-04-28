package com.anime.arena.skill;

import com.anime.arena.field.Field;
import com.anime.arena.field.SubField;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.PokemonType;
import com.anime.arena.skill.effect.Effect;

import java.util.ArrayList;
import java.util.List;

public class EffectSkill extends Skill {

    protected List<Effect> effectList;
    public EffectSkill(int id, String name, String description, SkillCategory category, int pp, int currentPP, int accuracy, PokemonType moveType,
                       SkillTarget target, int subtype, int speedPriority, int basePower) {
        super(id, name, description, category, pp, currentPP, accuracy, moveType, target, subtype, speedPriority, basePower);
        effectList = new ArrayList<Effect>();
    }
    public EffectSkill(int id, String name, String description, SkillCategory category, int pp, int currentPP, int accuracy, PokemonType moveType,
                       SkillTarget target, int subtype, int speedPriority) {
        super(id, name, description, category, pp, currentPP, accuracy, moveType, target, subtype, speedPriority, 0);
        effectList = new ArrayList<Effect>();
    }

    @Override
    public List<String> use(BattlePokemon skillUser, BattlePokemon enemyPokemon, int skillUserPartyPosition, int enemyPokemonPartyPosition, Field field, SubField userField, SubField enemyField, boolean isFirstAttack, Skill targetSkill, List<BattlePokemon> skillUserParty, List<BattlePokemon> enemyPokemonParty) {
        refreshMoveCounters(skillUser);
        return useEffects(skillUser, enemyPokemon, field, userField, enemyField, isFirstAttack);
    }

    protected List<String> useEffects(BattlePokemon skillUser, BattlePokemon enemyPokemon, Field field, SubField userField, SubField enemyField, boolean isFirstAttack) {
        List<String> resultMessages = new ArrayList<String>();
        for (Effect eff : effectList) {
            List<String> effectMessages = eff.use(skillUser, enemyPokemon, field,userField,enemyField, isFirstAttack);
            if (effectMessages != null) {
                resultMessages.addAll(effectMessages);
            }
        }
        return resultMessages;
    }


    public void addEffect(Effect e) {
        effectList.add(e);
    }

    public List<Effect> getEffects() {
        return effectList;
    }

    public boolean hasOneEffect() {
        return effectList.size() == 1;
    }

    public boolean hasOnlyStatusEffects() {
        for (Effect e : effectList) {
            if (e.getEffectType() != MoveEffectSubtype.STATUS) {
                return false;
            }
        }
        return true;
    }
}
