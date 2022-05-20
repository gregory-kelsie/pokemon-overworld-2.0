package com.anime.arena.objects;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class PlayerOutfit {
    private char gender;
    private String bodyType;
    private String hairType;
    private String hairColour;
    private String top;
    private String bottom;
    private String bag;

    public PlayerOutfit() {

    }
    public PlayerOutfit(char gender, String bodyType, String hairType, String hairColour, String top, String bottom, String bag) {

        this.gender = gender;
        this.bodyType = bodyType;
        this.hairType = hairType;
        this.hairColour = hairColour;
        this.top = top;
        this.bottom = bottom;
        this.bag = bag;

    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getHairType() {
        return hairType;
    }

    public void setHairType(String hairType) {
        this.hairType = hairType;
    }

    public String getHairColour() {
        return hairColour;
    }

    public void setHairColour(String hairColour) {
        this.hairColour = hairColour;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public String getBottom() {
        return bottom;
    }

    public void setBottom(String bottom) {
        this.bottom = bottom;
    }

    public String getBag() {
        return bag;
    }

    public void setBag(String bag) {
        this.bag = bag;
    }

}
