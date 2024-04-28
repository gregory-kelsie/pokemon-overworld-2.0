package com.anime.arena.battle;

import com.anime.arena.pokemon.BattlePokemon;
import com.anime.arena.skill.Skill;
import com.badlogic.gdx.Gdx;

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
            Gdx.app.log("InitState", "FIRST ATTACKER (ENEMY): " + enemyPokemon.getName());
            Gdx.app.log("InitState", "NULL SKILL FOR SECOND ATTACKER (USER): " + userPokemon.getName());
            battleStateManager.setAttackers(enemyPokemon, enemySkill, false, userPokemon, null);
        } else if (enemySkill == null && userSkill != null) {
            Gdx.app.log("InitState", "FIRST ATTACKER (USER): " + userPokemon.getName());
            Gdx.app.log("InitState", "NULL SKILL FOR SECOND ATTACKER (ENEMY): " + enemyPokemon.getName());
            battleStateManager.setAttackers(userPokemon, userSkill, true, enemyPokemon, null);
        } else if (!nullSkills && userSkill.getSpeedPriority() > enemySkill.getSpeedPriority()) {
            logFasterPlayer(userPokemon.getName(), enemyPokemon.getName());
            logSpeedPriorities(userSkill.getSpeedPriority(), enemySkill.getSpeedPriority());
            battleStateManager.setAttackers(userPokemon, userSkill, true, enemyPokemon, enemySkill);
        } else if (!nullSkills && userSkill.getSpeedPriority() < enemySkill.getSpeedPriority()) {
            battleStateManager.setAttackers(enemyPokemon, enemySkill, false, userPokemon, userSkill);
            logFasterEnemy(userPokemon.getName(), enemyPokemon.getName());
            logSpeedPriorities(userSkill.getSpeedPriority(), enemySkill.getSpeedPriority());
        } else {
            double userSpeed = userPokemon.getTotalSpeed(battleStateManager.getField(),
                    battleStateManager.getField().getPlayerField());
            double enemySpeed = enemyPokemon.getTotalSpeed(battleStateManager.getField(),
                    battleStateManager.getField().getOpponentField());
            boolean isTrickRoomActive = battleStateManager.getField().hasTrickRoom();
            logSpeedsAndTrickRoom(userSpeed, enemySpeed, isTrickRoomActive);
            if ((userSpeed >= enemySpeed) && !isTrickRoomActive) {
                battleStateManager.setAttackers(userPokemon, userSkill, true, enemyPokemon, enemySkill);
                logFasterPlayer(userPokemon.getName(), enemyPokemon.getName());
            } else {
                battleStateManager.setAttackers(enemyPokemon, enemySkill, false, userPokemon, userSkill);
                logFasterEnemy(userPokemon.getName(), enemyPokemon.getName());
            }
        }
        battleStateManager.setState(new SleepState(battleStateManager, true));
    }

    private void logFasterPlayer(String userName, String enemyName) {
        Gdx.app.log("InitState", "FIRST ATTACKER (USER): " + userName);
        Gdx.app.log("InitState", "SECOND ATTACKER (ENEMY): " + enemyName);
    }

    private void logFasterEnemy(String userName, String enemyName) {
        Gdx.app.log("InitState", "FIRST ATTACKER (ENEMY): " + enemyName);
        Gdx.app.log("InitState", "SECOND ATTACKER (USER): " + userName);
    }

    private void logSpeedPriorities(int userSpeedPriority, int enemySpeedPriority) {
        Gdx.app.log("InitState", "USER SPEED PRIORITY: " + userSpeedPriority);
        Gdx.app.log("InitState", "ENEMY SPEED PRIORITY: " + enemySpeedPriority);
    }

    private void logSpeedsAndTrickRoom(double userSpeed, double enemySpeed, boolean isTrickRoomActive) {
        Gdx.app.log("InitState", "PLAYER SPEED: " + userSpeed);
        Gdx.app.log("InitState", "ENEMY SPEED: " + enemySpeed);
        Gdx.app.log("InitState", "IS TRICK ROOM ACTIVE: " + isTrickRoomActive);
    }
}
