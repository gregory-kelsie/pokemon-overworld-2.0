package com.anime.arena.battle;

import com.anime.arena.field.Field;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.skill.Skill;

public class BattleStateManager {
    private BattleStatePokemon firstAttacker;
    private BattleStatePokemon secondAttacker;
    private Field field;
    private BattleTextBox textBox;
    private BattleState currentState;

    public BattleStateManager() {

    }

    public void setState(BattleState state) {
        this.currentState = state;
    }

    public void update(float dt) {
        if (currentState != null) {
            currentState.update(dt);
        }
    }

    public Field getField() {
        return field;
    }

    public BattleStatePokemon getFirstAttacker() {
        return firstAttacker;
    }

    public BattleStatePokemon getSecondAttacker() {
        return secondAttacker;
    }

    public void setAttackers(BattlePokemon pokemon, Skill skill, boolean isUser,
                                 BattlePokemon slowPokemon, Skill slowSkill) {
        firstAttacker = new BattleStatePokemon(pokemon, skill);
        firstAttacker.setUser(isUser);
        secondAttacker = new BattleStatePokemon(slowPokemon, slowSkill);
        secondAttacker.setUser(!isUser);
    }


    public void setTextBox(BattleTextBox textBox) {
        this.textBox = textBox;
    }

    public BattleTextBox getTextBox() {
        return textBox;
    }

}
