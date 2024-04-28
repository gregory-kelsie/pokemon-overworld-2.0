package com.anime.arena.battle.endturnlogic;

import com.anime.arena.battle.BattleStep;
import com.anime.arena.field.Field;
import com.anime.arena.pokemon.BattlePokemon;
import com.badlogic.gdx.Gdx;

public class EndTurnStep {

    protected BattlePokemon firstAttacker;
    protected BattlePokemon secondAttacker;
    protected Field field;
    protected BattleStep step;
    public EndTurnStep(BattleStep step, BattlePokemon firstAttacker, BattlePokemon secondAttacker, Field field) {
        this.firstAttacker = firstAttacker;
        this.secondAttacker = secondAttacker;
        this.field = field;
        this.step = step;
    }

    protected void battleLog(String str) {
        Gdx.app.log("EndTurnState", str);
    }

    public void execute() {

    }

    public boolean isExecutable() {
        return true;
    }

    public BattleStep getNextStep() {
        return null;
    }
}
