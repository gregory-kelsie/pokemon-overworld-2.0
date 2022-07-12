package com.anime.arena.mart;

import com.anime.arena.items.Item;

public class MartItem {
    private Item item;
    private int price;

    public MartItem() {

    }
    public MartItem(Item item, int price) {
        this.item = item;
        this.price = price;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
