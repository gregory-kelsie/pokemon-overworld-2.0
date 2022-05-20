package com.anime.arena.objects;

import com.anime.arena.interactions.TextBox;
import com.anime.arena.interactions.TextBoxFactory;
import com.anime.arena.screens.PlayScreen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TreeObject extends WorldObject {

    private Texture treeTexture;
    public TreeObject(int x, int y, PlayScreen screen) {
        super(x, y, screen);
        treeTexture = new Texture("sprites/tree.png");
    }

    public void manualInteract(Player player) {
        screen.playSelectSound();
        TextBoxFactory tbf = new TextBoxFactory(screen);
        TextBox tb = tbf.getCutTextBox(this);
        screen.setEvent(tb);
    }

    public void cut() {
        visible = false;
        dispose();
    }

    public void dispose() {
        treeTexture.dispose();
    }

    public void render(Batch batch) {
        batch.draw(treeTexture, x * 16 - 3, y * 16);
    }
}
