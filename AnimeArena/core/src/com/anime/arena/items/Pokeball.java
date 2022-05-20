package com.anime.arena.items;

public class Pokeball extends Item {
    private double catchRate;
    public Pokeball(int itemID, String name, String description, String itemImage, int itemType) {
        super(itemID, name, description, itemImage, itemType);
        this.catchRate = 1.0;
    }

    public double getCatchRate() {
        return catchRate;
    }

    public void setCatchRate(double catchRate) {
        this.catchRate = catchRate;
    }
}
