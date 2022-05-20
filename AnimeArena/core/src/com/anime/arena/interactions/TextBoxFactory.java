package com.anime.arena.interactions;

import com.anime.arena.objects.BerryObject;
import com.anime.arena.objects.ItemObject;
import com.anime.arena.objects.Player;
import com.anime.arena.objects.TreeObject;
import com.anime.arena.screens.PlayScreen;

public class TextBoxFactory {
    private PlayScreen screen;

    public TextBoxFactory(PlayScreen screen) {
        this.screen = screen;
    }

    public TextBox getItemTextBox(ItemObject item) {
        TextBox itemTextBox = new TextBox(screen, item.getItemText(), item.getItemJingle());
        return itemTextBox;
    }

    public TextBox createTextBox(String text) {
        TextBox tb = new TextBox(screen, text);
        return tb;
    }

    public TextBox createJingleTextBox(String text, String jingle) {
        TextBox textBox = new TextBox(screen, text, jingle);
        return textBox;
    }


    public TextBox getSwimmingTextBox() {
        TextBox swimmingTextBox = new TextBox(screen,"The water is dyed a deep blue...\nWould you like to surf?");

        swimmingTextBox.addOption("Yes", new SwimEvent(screen));
        swimmingTextBox.addOption("No", null);
        return swimmingTextBox;
    }

    public TextBox getCutTextBox(TreeObject tree) {
        TextBox cutTextBox = new TextBox(screen,"There is a tree blocking the way...\nWould you like use cut?");
        TextBox cut = new TextBox(screen,"You used cut!!");
        cut.setNextEvent(new TreeEvent(screen, tree));
        cutTextBox.addOption("Yes", cut);
        cutTextBox.addOption("No", null);
        return cutTextBox;
    }

    public TextBox getBerryTextBox(BerryObject berry) {
        int berryAmount = berry.getBerryAmount();
        int exp = berry.getExp(berryAmount);
        String berryString = berryAmount == 1 ? "berry" : "berries";
        String prefix = berryAmount == 1  ? ("aeiou".contains(berry.getBerryName().substring(0, 0)) ? "an" : "a") : Integer.toString(berryAmount);
        TextBox berryTextBox = new TextBox(screen, "You found " + prefix + " " + berry.getBerryName() + " " + berryString + "!", "audio/SE/itemget.wav");
        berryTextBox.setNextEvent(new TextBox(screen, "Gained " + exp + " experience."));
        return berryTextBox;
    }

    public TextBox getWildPokemonPlaceholderTextBox() {
        return new TextBox(screen, "Trigger Wild Pokemon!");
    }
}
