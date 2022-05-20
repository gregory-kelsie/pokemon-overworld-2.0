package com.anime.arena.quest;

import com.anime.arena.objects.PlayerSwitches;

public class QuestSwitch {
    private int switchID;
    private String switchDescription;

    public QuestSwitch(int switchID, String switchDescription) {
        this.switchDescription = switchDescription;
        this.switchID = switchID;
    }

    public int getSwitchID() {
        return switchID;
    }

    public String getSwitchDescription() {
        return switchDescription;
    }

    public boolean isComplete(PlayerSwitches playerSwitches) {
        if (playerSwitches.isActive(switchID)) {
            return true;
        }
        return false;
    }
}
