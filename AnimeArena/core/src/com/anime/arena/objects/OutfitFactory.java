package com.anime.arena.objects;

import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.HashMap;

public class OutfitFactory {
    private PlayerOutfit outfit;
    private PlayScreen screen;
    private TextureAtlas bodyAtlas;
    private TextureAtlas swimmingAtlas;
    private HashMap<String, TextureAtlas> hairAtlases;
    private TextureAtlas topAtlas;
    private TextureAtlas bottomAtlas;
    private TextureAtlas bagAtlas;

    public OutfitFactory(PlayerOutfit outfit) {
        this.outfit = outfit;
        bodyAtlas = new TextureAtlas("sprites/player/Bodies.atlas");
        swimmingAtlas = new TextureAtlas("sprites/player/swimming.atlas");
        topAtlas = new TextureAtlas("sprites/player/Upper.atlas");
        bottomAtlas = new TextureAtlas("sprites/player/Lower.atlas");
        hairAtlases = new HashMap<String, TextureAtlas>();
        hairAtlases.put("MaleHair1", new TextureAtlas("sprites/player/MaleHair1.atlas"));
        hairAtlases.put("MaleHair2", new TextureAtlas("sprites/player/MaleHair2.atlas"));
        hairAtlases.put("MaleHair3", new TextureAtlas("sprites/player/MaleHair3.atlas"));
        hairAtlases.put("MaleHair4", new TextureAtlas("sprites/player/MaleHair4.atlas"));
        hairAtlases.put("FemaleHair1", new TextureAtlas("sprites/player/FemaleHair1.atlas"));
        hairAtlases.put("FemaleHair2", new TextureAtlas("sprites/player/FemaleHair2.atlas"));
        hairAtlases.put("FemaleHair3", new TextureAtlas("sprites/player/FemaleHair3.atlas"));
        hairAtlases.put("FemaleHair4", new TextureAtlas("sprites/player/FemaleHair4.atlas"));
    }
    public OutfitFactory(PlayScreen screen, PlayerOutfit outfit) {
        this.outfit = outfit;
        bodyAtlas = screen.getBodyAtlas();
        swimmingAtlas = screen.getSwimmingAtlas();
        hairAtlases = screen.getHairAtlases();
        topAtlas = screen.getTopAtlas();
        bottomAtlas = screen.getBottomAtlas();
        bagAtlas = screen.getBagAtlas();
    }

    public PlayerBody createBody() {
        if (outfit.getBodyType() != null) {
            return new PlayerBody(bodyAtlas, outfit.getBodyType());
        }
        return null;
    }

    public PlayerBody createSwimmingBody() {
        if (outfit.getBodyType() != null) {
            if (outfit.getBodyType().equals("female-dark") || outfit.getBodyType().equals("male-dark")) {
                return new PlayerBody(swimmingAtlas, "dark");
            } else if (outfit.getBodyType().equals("female-medium") || outfit.getBodyType().equals("male-medium")) {
                return new PlayerBody(swimmingAtlas, "medium");
            } else if (outfit.getBodyType().equals("female-light") || outfit.getBodyType().equals("male-light")) {
                return new PlayerBody(swimmingAtlas, "light");
            } else if (outfit.getBodyType().equals("female-pale") || outfit.getBodyType().equals("male-pale")) {
                return new PlayerBody(swimmingAtlas, "pale");
            }
        }
        return null;
    }


    public PlayerBody createHair() {
        if (outfit.getHairType() != null) {
            return new PlayerBody(hairAtlases.get(outfit.getHairType()), outfit.getHairColour());
        }
        return null;
    }

    public PlayerBody createTop() {
        if (outfit.getTop() != null) {
            return new PlayerBody(topAtlas, outfit.getTop());
        }
        return null;
    }

    public PlayerBody createBottom() {
        if (outfit.getBottom() != null) {
            return new PlayerBody(bottomAtlas, outfit.getBottom());
        }
        return null;
    }

    public PlayerBody createBag() {
        if (outfit.getBag() != null) {
            return new PlayerBody(bagAtlas, outfit.getBag());
        }
        return null;
    }


}
