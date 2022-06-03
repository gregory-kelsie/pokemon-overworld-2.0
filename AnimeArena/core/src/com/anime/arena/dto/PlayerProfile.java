package com.anime.arena.dto;

import com.anime.arena.api.PokemonAPI;

public class PlayerProfile {
    private int uid;
    private String username;
    private int startedGame;
    private int money;
    private String gender;
    private String skinTone;
    private String hairStyle;
    private String hairColour;
    private int topID;
    private int bottomID;

    public PlayerProfile(int uid, String username, int startedGame) {
        this.uid = uid;
        this.username = username;
        this.startedGame = startedGame;
    }

    public PlayerProfile(int uid, String username, int startedGame, int money, String gender, String skinTone, String hairStyle, String hairColour) {
        this.uid = uid;
        this.username = username;
        this.startedGame = startedGame;
        this.money = money;
        this.gender = gender;
        this.skinTone = skinTone;
        this.hairStyle = hairStyle;
        this.hairColour = hairColour;
    }

    public int getTopID() {
        return topID;
    }

    public void setTopID(int topID) {
        this.topID = topID;
    }

    public int getBottomID() {
        return bottomID;
    }

    public void setBottomID(int bottomID) {
        this.bottomID = bottomID;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getStartedGame() {
        return startedGame;
    }

    public void setStartedGame(int startedGame) {
        this.startedGame = startedGame;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getSkinTone() {
        return skinTone;
    }

    public void setSkinTone(String skinTone) {
        this.skinTone = skinTone;
    }

    public String getHairStyle() {
        return hairStyle;
    }

    public void setHairStyle(String hairStyle) {
        this.hairStyle = hairStyle;
    }

    public String getHairColour() {
        return hairColour;
    }

    public void setHairColour(String hairColour) {
        this.hairColour = hairColour;
    }

    public void updateProfile() {
        PokemonAPI api = new PokemonAPI();
        api.createCharacter(this);
    }
}
