package com.anime.arena.interactions;

import com.anime.arena.quest.PokemonQuest;
import com.anime.arena.quest.PokemonTypeQuest;
import com.anime.arena.quest.Quest;
import com.anime.arena.tools.Counter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.List;

public class QuestFactory {
    public QuestFactory() {

    }

    public Quest getQuest(String questID) {
        FileHandle file = Gdx.files.internal("scripts/quest" + questID + ".txt");
        String text = file.readString();
        String[] lines = text.split("\r\n");
        Counter index = new Counter();
        String questName = lines[index.getCounter()];
        String questDescription = lines[index.getCounter() + 1];
        index.increment(2);
        if (lines[index.getCounter()].equals("POKEMON_LIST")) {
            index.increment();
            List<PokemonQuest> pokemonQuestList = new ArrayList<>();
            while(!lines[index.getCounter()].equals("END_POKEMON_LIST")) {
                String[] pokemonAndAmount = lines[index.getCounter()].split(",");
                pokemonQuestList.add(new PokemonQuest(Integer.parseInt(pokemonAndAmount[0]), Integer.parseInt(pokemonAndAmount[1])));
                index.increment();
            }
            index.increment();
        }
        if (lines[index.getCounter()].equals("TYPE_LIST")) {
            index.increment();
            List<PokemonTypeQuest> pokemonTypeQuestList = new ArrayList<>();
            while(!lines[index.getCounter()].equals("END_TYPE_LIST")) {
                String[] typeAndAmount = lines[index.getCounter()].split(",");
                pokemonTypeQuestList.add(new PokemonTypeQuest(Integer.parseInt(typeAndAmount[0]), Integer.parseInt(typeAndAmount[1])));
                index.increment();
            }
            index.increment();
        }
        return null;
    }
}
