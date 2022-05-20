package com.anime.arena.screens;

import com.anime.arena.AnimeArena;
import com.anime.arena.objects.CustomPlayer;
import com.anime.arena.objects.OutfitFactory;
import com.anime.arena.objects.PlayerOutfit;

public class TestScreen extends PlayScreen {
    private CustomPlayer customPlayer;
    public TestScreen(AnimeArena game) {
        super(game);
        String body = "male-light";
        String hairType = "MaleHair4";
        String hairColour = "red";
        String top = "hoodie black";
        String bottom = "jeans navy";
        PlayerOutfit outfit = new PlayerOutfit();
        outfit.setBodyType(body);
        outfit.setHairType(hairType);
        outfit.setHairColour(hairColour);
        outfit.setTop(top);
        outfit.setBottom(bottom);
        customPlayer = new CustomPlayer(outfit);
        OutfitFactory outfitFactory = new OutfitFactory(this, outfit);
        customPlayer.setBody(outfitFactory.createBody());
        customPlayer.setHair(outfitFactory.createHair());
        customPlayer.setTop(outfitFactory.createTop());
        customPlayer.setBottom(outfitFactory.createBottom());
        customPlayer.setBag(outfitFactory.createBag());
        customPlayer.initSpritePosition(0, 0);
    }

    public void changeHair(String newHairType, String newHairColour) {
        customPlayer.getOutfit().setHairType(newHairType);
        customPlayer.getOutfit().setHairColour(newHairColour);
        OutfitFactory outfitFactory = new OutfitFactory(this, customPlayer.getOutfit());
        customPlayer.setHair(outfitFactory.createHair());
    }
}
