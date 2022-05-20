package com.anime.arena.items;

public class BagItem {
    private Item item;
    private int amount;

    public BagItem(Item item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    public BagItem(Item item) {
        this.item = item;
        this.amount = 1;
    }

    public void decreaseAmount() {
        if (amount > 0) {
            amount--;
        } else {

        }
    }

    public void increaseAmount(int amount) {
        this.amount += amount;
    }


    public int getAmount() {
        return amount;
    }

    public Item getItem() {
        return item;
    }
}
