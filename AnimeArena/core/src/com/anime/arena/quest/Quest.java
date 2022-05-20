package com.anime.arena.quest;

import com.anime.arena.objects.PlayerSwitches;
import com.anime.arena.screens.PlayScreen;

import java.util.List;

public class Quest {
    private int questID;
    private boolean complete;
    private String questName;
    private String questDescription;

    private List<PokemonQuest> pokemonQuestList;
    private List<PokemonTypeQuest> pokemonTypeQuestList;
    private List<QuestSwitch> questSwitchList;

    public Quest(int questID, String questName, String questDescription) {
        this.questID = questID;
        this.questName = questName;
        this.questDescription = questDescription;
        this.complete = false;
    }

    public int getQuestID() {
        return questID;
    }



    public void completeQuest() {
        this.complete = true;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean hasQuestSwitch(int switchID) {
        for (QuestSwitch questSwitch : questSwitchList) {
            if (questSwitch.getSwitchID() == switchID) {
                return true;
            }
        }
        return false;
    }

    public boolean isRequirementsComplete(PlayerSwitches switches) {
        for (PokemonQuest pq : pokemonQuestList) {
            if (!pq.isComplete()) {
                return false;
            }
        }
        for (PokemonTypeQuest ptq : pokemonTypeQuestList) {
            if (!ptq.isComplete()) {
                return false;
            }
        }
        for (QuestSwitch questSwitch : questSwitchList) {
            if (!questSwitch.isComplete(switches)) {
                return false;
            }
        }
        return true;
    }

    public void incrementPokemonTypeQuest(int pokemonType) {
        for (PokemonTypeQuest ptq : pokemonTypeQuestList) {
            if (!ptq.isComplete() && ptq.getPokemonType() == pokemonType) {
                ptq.increment();
            }
        }
    }

    public void incrementPokemonIDQuest(int pokemonID) {
        for (PokemonQuest pq : pokemonQuestList) {
            if (!pq.isComplete() && pq.getPokemonID() == pokemonID) {
                pq.increment();
            }
        }
    }

}
