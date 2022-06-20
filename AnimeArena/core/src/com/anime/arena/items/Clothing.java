package com.anime.arena.items;

import com.anime.arena.dto.PlayerProfile;
import com.anime.arena.objects.OutfitFactory;
import com.anime.arena.objects.Player;
import com.anime.arena.pokemon.BasePokemonFactory;
import com.anime.arena.pokemon.Pokemon;

public class Clothing extends Item {
    private String clothingID; //The ID in the atlas
    private String gender; //Unisex, Male, Female - U, M, F
    private String clothingType; //Top or Bottom - T, B
    public Clothing(int itemID, String name, String description, String itemImage, int itemType) {
        super(itemID, name, description, itemImage, itemType);
        this.gender = "U";
    }

    public String getClothingID() {
        return clothingID;
    }

    public void setClothingID(String clothingID) {
        this.clothingID = clothingID;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getClothingType() {
        return clothingType;
    }

    public void setClothingType(String clothingType) {
        this.clothingType = clothingType;
    }

    @Override
    public boolean use(Player player, Pokemon p, BasePokemonFactory factory) {
        return false;
    }

    @Override
    public boolean use(Player player, ItemFactory itemFactory) {
        PlayerProfile profile = player.getPlayerProfile();
        if (gender.equals("U") || profile.getGender().equals(gender)) {
            if (clothingType.equals("T")) {
                int oldTopID = profile.getTopID();
                profile.setTopID(itemID);
                player.getOutfit().setTop(clothingID);
                OutfitFactory outfitFactory = new OutfitFactory(player.getOutfit());
                player.setTop(outfitFactory.createTop());
                player.initSpritePositions(0f, 0f);
                player.getBag().addItem(itemFactory, oldTopID);
                return true;
            } else if (clothingType.equals("B")) {
                int oldBottomsID = profile.getBottomID();
                profile.setBottomID(itemID);
                player.getOutfit().setBottom(clothingID);
                OutfitFactory outfitFactory = new OutfitFactory(player.getOutfit());
                player.setBottom(outfitFactory.createTop());
                player.initSpritePositions(0f, 0f);
                player.getBag().addItem(itemFactory, oldBottomsID);
                return true;
            }
        }
        return false;
    }
}
