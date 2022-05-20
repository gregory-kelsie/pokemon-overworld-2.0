package com.anime.arena.objects;

import com.anime.arena.screens.PlayScreen;

public class OutfitFactory {
    private PlayerOutfit outfit;
    private PlayScreen screen;
    public OutfitFactory(PlayScreen screen, PlayerOutfit outfit) {
        this.outfit = outfit;
        this.screen = screen;
    }

    public PlayerBody createBody() {
        if (outfit.getBodyType() != null) {
            return new PlayerBody(screen.getBodyAtlas(), outfit.getBodyType());
        }
        return null;
    }

    public PlayerBody createSwimmingBody() {
        if (outfit.getBodyType() != null) {
            if (outfit.getBodyType().equals("female-dark") || outfit.getBodyType().equals("male-dark")) {
                return new PlayerBody(screen.getSwimmingAtlas(), "dark");
            } else if (outfit.getBodyType().equals("female-medium") || outfit.getBodyType().equals("male-medium")) {
                return new PlayerBody(screen.getSwimmingAtlas(), "medium");
            } else if (outfit.getBodyType().equals("female-light") || outfit.getBodyType().equals("male-light")) {
                return new PlayerBody(screen.getSwimmingAtlas(), "light");
            } else if (outfit.getBodyType().equals("female-pale") || outfit.getBodyType().equals("male-pale")) {
                return new PlayerBody(screen.getSwimmingAtlas(), "pale");
            }
        }
        return null;
    }


    public PlayerBody createHair() {
        if (outfit.getHairType() != null) {
            return new PlayerBody(screen.getHairAtlas(outfit.getHairType()), outfit.getHairColour());
        }
        return null;
    }

    public PlayerBody createTop() {
        if (outfit.getTop() != null) {
            return new PlayerBody(screen.getTopAtlas(), outfit.getTop());
        }
        return null;
    }

    public PlayerBody createBottom() {
        if (outfit.getBottom() != null) {
            return new PlayerBody(screen.getBottomAtlas(), outfit.getBottom());
        }
        return null;
    }

    public PlayerBody createBag() {
        if (outfit.getBag() != null) {
            return new PlayerBody(screen.getBagAtlas(), outfit.getBag());
        }
        return null;
    }


}
