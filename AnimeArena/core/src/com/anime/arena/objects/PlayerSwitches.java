package com.anime.arena.objects;

import java.util.HashMap;

public class PlayerSwitches {
    //Switch ID, Active
    private HashMap<Integer, Boolean> switches;

    //Trainer Switches. NPC ID and whether or not they've been battled.
    private HashMap<Integer, Boolean> trainerSwitches;
    public PlayerSwitches() {
        this.switches = new HashMap<>();
        switches.put(-1, true); //Just started pokemon adventure
        switches.put(0, false);
        switches.put(1, false);
        switches.put(2, false);
        switches.put(3, false); //Received starter from oak pokemon
        switches.put(4, false); //Assistant in Oak's lab that will give Pokeballs when true
        switches.put(5, false); //Assistant in Oak's lab that will give Potions when true
        this.trainerSwitches = new HashMap<>();
    }

    public boolean isActive(int switchID) {
        if (switches.containsKey(switchID)) {
            return switches.get(switchID);
        }
        return false;
    }

    public boolean battledTrainer(int trainerID) {
        if (trainerSwitches.containsKey(trainerID)) {
            return trainerSwitches.get(trainerID);
        }
        return false;
    }

    public void activateTrainerSwitch(int trainerID) {
        trainerSwitches.put(trainerID, true);
    }

    public void activateSwitch(int switchID) {
        if (switches.containsKey(switchID)) {
            switches.put(switchID, true);
        }
    }

    public void deactivateSwitch(int switchID) {
        if (switches.containsKey(switchID)) {
            switches.put(switchID, false);
        }
    }
}
