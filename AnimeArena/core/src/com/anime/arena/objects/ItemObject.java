package com.anime.arena.objects;

import com.anime.arena.interactions.TextBox;
import com.anime.arena.interactions.TextBoxFactory;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class ItemObject extends WorldObject{


    protected int itemId;
    protected int itemAmount;
    protected int itemObjectType;
    protected Texture itemTexture;

    protected String itemJingle;


    public ItemObject(int x, int y, PlayScreen screen, int itemId, int itemAmount, int itemObjectType) {
        super(x, y, screen);
        this.itemId = itemId;
        this.itemAmount = itemAmount;
        this.itemObjectType = itemObjectType;
        if (itemObjectType == 1) {
            itemTexture = new Texture("sprites/tm.png");
            this.itemJingle = "audio/SE/itemget.wav";
        } else {
            itemTexture = new Texture("sprites/item.png");
            this.itemJingle = "audio/SE/itemget.wav";
        }
    }

    public String getItemJingle() {
        return itemJingle;
    }

    public void manualInteract(Player player) {
        TextBoxFactory tbf = new TextBoxFactory(screen);
        TextBox tb = tbf.getItemTextBox(this);
        this.visible = false;
        dispose();
        screen.setEvent(tb);
    }


    public void dispose() {
        itemTexture.dispose();
    }

    public void render(Batch batch) {
        batch.draw(itemTexture, x * 16, y * 16);
    }

    public String getItemText() {
        if (itemAmount > 1) {
            return "You found " + itemAmount + " " + getItem() + "s!";
        } else {
            return "You found a " + getItem() + "!";
        }
    }

    private String getItem() {
        return "Pokeball";
    }
}
