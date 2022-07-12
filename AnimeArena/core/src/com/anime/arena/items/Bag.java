package com.anime.arena.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Bag {
    public List<BagItem> items;
    private List<BagItem> medicine;
    private List<BagItem> pokeballs;
    private List<BagItem> clothes;
    private List<BagItem> mail;

    public Bag() {
        items = new ArrayList<BagItem>();
        medicine = new ArrayList<BagItem>();
        mail = new ArrayList<BagItem>();
        pokeballs = new ArrayList<BagItem>();
        clothes = new ArrayList<BagItem>();
    }

    public void useItem(int itemID, List<BagItem> items) {
        BagItem item = getBagItem(itemID, items);
        if (item != null) {
            if (item.getAmount() == 1) {
                items.remove(item);
            } else if (item.getAmount() > 1) {
                item.decreaseAmount();
            }
        }
    }

    public BagItem getBagItem(int itemID, List<BagItem> bagItems) {
        for (BagItem item : bagItems) {
            if (item.getItem().getItemID() == itemID) {
                return item;
            }
        }
        return null;
    }

    public List<BagItem> getItems() {
        return items;
    }

    public List<BagItem> getMedicine() {
        return medicine;
    }

    public List<BagItem> getMail() {
        return mail;
    }

    public List<BagItem> getPokeballs() {
        return pokeballs;
    }

    public List<BagItem> getClothes() {
        return clothes;
    }

    public int countItems(Item i) {
        List<BagItem> bagItems;
        if (i.getItemType() == ItemType.MEDICINE.getValue() || i.getItemType() == ItemType.VITAMIN.getValue()) {
            bagItems = medicine;
        } else if (i.getItemType() == ItemType.HOLD_ITEM.getValue() ||
                i.getItemType() == ItemType.EVOLUTION_STONE.getValue()) {
            bagItems = items;
        } else if (i.getItemType() == ItemType.POKEBALL.getValue()) {
            bagItems = pokeballs;
        } else if (i.getItemType() == ItemType.CLOTHES.getValue()) {
            bagItems = clothes;
        } else {
            bagItems = new ArrayList<BagItem>();
        }
        if (bagItems != null) {
            BagItem bagItem = getBagItem(i.getItemID(), bagItems);
            if (bagItem != null) {
                return bagItem.getAmount();
            }
        }
        return 0;
    }

    public void addItem(ItemFactory itemFactory, int itemID, int amount) {
        Item i = itemFactory.createItem(itemID);
        if (i.getItemType() == ItemType.MEDICINE.getValue() || i.getItemType() == ItemType.VITAMIN.getValue()) {
            addToBag(itemFactory, itemID, amount, medicine);
        } else if (i.getItemType() == ItemType.HOLD_ITEM.getValue() ||
                i.getItemType() == ItemType.EVOLUTION_STONE.getValue()) {
            addToBag(itemFactory, itemID, amount, items);
        } else if (i.getItemType() == ItemType.POKEBALL.getValue()) {
            addToBag(itemFactory, itemID, amount, pokeballs);
        } else if (i.getItemType() == ItemType.CLOTHES.getValue()) {
            addToBag(itemFactory, itemID, amount, clothes);
        }

    }

    private void addToBag(ItemFactory itemFactory, int itemID, int amount, List<BagItem> items) {
        BagItem item = getBagItem(itemID, items);
        if (item != null) {
            item.increaseAmount(amount);
        } else {
            items.add(new BagItem(itemFactory.createItem(itemID), amount));
        }
    }

    public void removeFromBag(BagItem item, List<BagItem> items) {
        if (item != null) {
            item.decreaseAmount();
            if (item.getAmount() == 0) {
                items.remove(item);
            }
        }
    }

    public void useItem(BagItem bagItem) {
        Item i = bagItem.getItem();
        if (i.getItemType() == ItemType.MEDICINE.getValue() || i.getItemType() == ItemType.VITAMIN.getValue()) {
            removeFromBag(bagItem, medicine);
        } else if (i.getItemType() == ItemType.HOLD_ITEM.getValue() ||
                i.getItemType() == ItemType.EVOLUTION_STONE.getValue()) {
            removeFromBag(bagItem, items);
        } else if (i.getItemType() == ItemType.POKEBALL.getValue()) {
            removeFromBag(bagItem, pokeballs);
        } else if (i.getItemType() == ItemType.CLOTHES.getValue()) {
            removeFromBag(bagItem, clothes);
        }
    }

    public void addItem(ItemFactory itemFactory, int itemID) {
        addItem(itemFactory, itemID, 1);
    }
}
