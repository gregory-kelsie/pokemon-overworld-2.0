package com.anime.arena.battle;

import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.skill.Skill;

public class InitState extends BattleState {
    private BattlePokemon userPokemon;
    private Skill userSkill;
    private BattlePokemon enemyPokemon;
    private Skill enemySkill;

    public InitState(BattleStateManager battleStateManager, BattlePokemon userPokemon, Skill userSkill, BattlePokemon enemyPokemon, Skill enemySkill) {
        this.battleStateManager = battleStateManager;
        this.userPokemon = userPokemon;
        this.userSkill = userSkill;
        this.enemySkill = enemySkill;
        this.enemyPokemon = enemyPokemon;
    }

    public void update(float dt) {
        boolean nullSkills = false;
        if (enemySkill == null && userSkill == null) {
            nullSkills = true;
        }

        if (userSkill == null && enemySkill != null) {
            battleStateManager.setAttackers(enemyPokemon, enemySkill, false, userPokemon, null);
        } else if (enemySkill == null && userSkill != null) {
            battleStateManager.setAttackers(userPokemon, userSkill, true, enemyPokemon, null);
        } else if (!nullSkills && userSkill.getSpeedPriority() > enemySkill.getSpeedPriority()) {
            battleStateManager.setAttackers(userPokemon, userSkill, true, enemyPokemon, enemySkill);
        } else if (!nullSkills && userSkill.getSpeedPriority() < enemySkill.getSpeedPriority()) {
            battleStateManager.setAttackers(enemyPokemon, enemySkill, false, userPokemon, userSkill);
        } else {
            double userSpeed = userPokemon.getTotalSpeed(battleStateManager.getField(),
                    battleStateManager.getField().getPlayerField());
            double enemySpeed = enemyPokemon.getTotalSpeed(battleStateManager.getField(),
                    battleStateManager.getField().getOpponentField());
            if ((userSpeed >= enemySpeed) && !battleStateManager.getField().hasTrickRoom()) {
                battleStateManager.setAttackers(userPokemon, userSkill, true, enemyPokemon, enemySkill);
            } else {
                battleStateManager.setAttackers(enemyPokemon, enemySkill, false, userPokemon, userSkill);
            }
        }

    }
}
