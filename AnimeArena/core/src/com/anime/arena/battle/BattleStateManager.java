package com.anime.arena.battle;

import com.anime.arena.battle.ui.BattleTextBox;
import com.anime.arena.battle.ui.UIComponent;
import com.anime.arena.field.Field;
import com.anime.arena.pokemon.BasePokemonFactory;
import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.pokemon.Pokemon;
import com.anime.arena.screens.BattleScreenInterface;
import com.anime.arena.screens.BattleTestScreen;
import com.anime.arena.screens.PokemonBattleScreen;
import com.anime.arena.skill.Skill;
import com.badlogic.gdx.Gdx;

import java.util.List;

public class BattleStateManager {
    private BattleStatePokemon firstAttacker;
    private BattleStatePokemon secondAttacker;
    private Field field;

    private enum BattleType {WILD_BATTLE, TRAINER_BATTLE}

    private BattleType battleType;
    private BattleTextBox textBox;
    private BattleState currentState;
    private List<Pokemon> party;
    private boolean displayEnemyPokemon;

    private boolean returningToFightOptions;
    private boolean isExitingBattle;
    private BattleScreenInterface bts;
    private PokemonBattleScreen pokemonBattleScreen;

    public BattleStateManager(BattleScreenInterface bts, List<Pokemon> party, BattlePokemon userPokemon, Skill userSkill, BattlePokemon enemyPokemon, Skill enemySkill, Field field) {
        this.bts = bts;
        this.party = party;
        this.field = field;
        this.battleType = BattleType.WILD_BATTLE;
        this.returningToFightOptions = false;
        this.isExitingBattle = false;
        logPokemonAndMoveInfo(userPokemon, userSkill, enemyPokemon, enemySkill);
    }



    public BasePokemonFactory getPokemonFactory() {
        if (bts != null) {
            return bts.getBasePokemonFactory();
        }
        return pokemonBattleScreen.getBasePokemonFactory();
    }

    public boolean isWildBattle() {
        return battleType == BattleType.WILD_BATTLE;
    }

    public boolean isTrainerBattle() {
        return battleType == BattleType.TRAINER_BATTLE;
    }

    public BattleState getCurrentState() {
        return currentState;
    }

    public List<Pokemon> getParty() { return party; }

    public boolean isPlayerPartyWiped() {
        for (Pokemon p : party) {
            if (p.getCurrentHealth() > 0) {
                return false;
            }
        }
        return true;
    }

    private void logPokemonAndMoveInfo(BattlePokemon userPokemon, Skill userSkill, BattlePokemon enemyPokemon, Skill enemySkill) {
        Gdx.app.log("logPokemonAndMoveInfo", "PLAYER: " + userPokemon.getName() + " (" +
                userPokemon.getCurrentHealth() + "/" + userPokemon.getPokemon().getHealthStat() + ")");
        Gdx.app.log("logPokemonAndMoveInfo", "PLAYER STATUS: " + userPokemon.getPokemon().getUniqueVariables().getStatus());
        Gdx.app.log("logPokemonAndMoveInfo", "PLAYER MOVE: " + userSkill.getName());
        Gdx.app.log("logPokemonAndMoveInfo", "ENEMY: " + enemyPokemon.getName() + " (" +
                enemyPokemon.getCurrentHealth() + "/" + enemyPokemon.getPokemon().getHealthStat() + ")");
        Gdx.app.log("logPokemonAndMoveInfo", "ENEMY STATUS: " + enemyPokemon.getPokemon().getUniqueVariables().getStatus());
        Gdx.app.log("logPokemonAndMoveInfo", "ENEMY MOVE: " + enemySkill.getName());
    }

    public void setState(BattleState state) {
        this.currentState = state;
    }

    public void setReturningToFightOptions(boolean returningToFightOptions) {
        this.returningToFightOptions = returningToFightOptions;
    }

    public void setExitingBattle(boolean isExitingBattle) {
        this.isExitingBattle = isExitingBattle;
    }

    public boolean isReturningToFightOptions() {
        return returningToFightOptions;
    }

    public boolean isExitingBattle() {
        return isExitingBattle;
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

    public boolean isUserPokemonFainted() {
        if (firstAttacker.isUser() && firstAttacker.getPokemon().hasFainted()) {
            return true;
        } else if (secondAttacker.isUser() && secondAttacker.getPokemon().hasFainted()) {
            return true;
        }
        return false;
    }

    public void setAttackers(BattlePokemon pokemon, Skill skill, boolean isUser,
                                 BattlePokemon slowPokemon, Skill slowSkill) {
        firstAttacker = new BattleStatePokemon(pokemon, skill);
        firstAttacker.setUser(isUser);
        secondAttacker = new BattleStatePokemon(slowPokemon, slowSkill);
        secondAttacker.setUser(!isUser);
    }

    public void setAttackerAfterSwitch(BattlePokemon switchingPokemon, BattlePokemon attackingPokemon, Skill skill) {
        firstAttacker = new BattleStatePokemon(switchingPokemon, null);
        firstAttacker.setUser(true);
        secondAttacker = new BattleStatePokemon(attackingPokemon, skill);
        secondAttacker.setUser(false);
    }

    public void switchOutFaintedPokemon() {
        bts.switchOutFaintedPokemon();
    }

    public void openLearningMoveScreen(Pokemon learningPokemon, Skill learningMove, ExpState expState) {
        bts.setLearningMoveScreen(learningPokemon, learningMove, expState);
    }


    public void setTextBox(BattleTextBox textBox) {
        this.textBox = textBox;
    }

    public boolean hasState() {
        return currentState != null;
    }

    public BattleTextBox getTextBox() {
        if (currentState != null && currentState.getUiComponent() instanceof BattleTextBox) {
            return (BattleTextBox) currentState.getUiComponent();
        }
        return null;
    }

    public UIComponent getUIComponent() {
        if (currentState != null && currentState.getUiComponent() != null) {
            return currentState.getUiComponent();
        }
        return null;
    }

}
